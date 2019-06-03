package com.bestapps.trendingvideos


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View

import com.google.gson.JsonObject
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import com.kaopiz.kprogresshud.KProgressHUD
import me.rohanjahagirdar.outofeden.Utils.JSONParser
import android.widget.*
import com.google.gson.JsonArray
import kotlin.math.ceil

import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.json.JSONObject

public class MainActivity() : AppCompatActivity() {
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var gridView: GridView
    private lateinit var btnMovies: Button
    private lateinit var btnSeries: Button
    private lateinit var tvMoviesUnderline: TextView
    private lateinit var tvSeriesUnderline: TextView

    var hud: KProgressHUD? = null

    private var arr_Videos: ArrayList<Video> = ArrayList()
    private var arr_Series: ArrayList<Video> = ArrayList()
    private val API_URL: String = "https://40bnxiajgf.execute-api.us-east-1.amazonaws.com/live/apislive/getlibrarylist"
    private val MOVIE_URL: String = "https://40bnxiajgf.execute-api.us-east-1.amazonaws.com/live/apislive/movies"
    private var nTotal = 0
    private var nLimit = 0
    private var nPage = 0
    private var nCurrentPage = 0

    private var nCategory = 0
    private var prevCategory: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initButtons()
        initSwipeLayout()
        //get Video list
        getVideoList(nCurrentPage, false)
    }

    //BranchIO
    override fun onStart() {
        super.onStart()

        // Branch init
        Branch.getInstance(this).initSession(object : Branch.BranchReferralInitListener {
            override fun onInitFinished(referringParams: JSONObject, error: BranchError?) {
                if (error == null) {
                    Log.e("BRANCH SDK", referringParams.toString())
                    // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                    // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
                } else {
                    Log.e("BRANCH SDK", error.message)
                }
            }
        }, this.intent.data, this)
    }

    public override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }

    //Buttons
    private fun initButtons() {
        btnMovies = findViewById(R.id.btn_movies)
        btnSeries = findViewById(R.id.btn_series)
        tvMoviesUnderline = findViewById(R.id.text_movies_underline)
        tvSeriesUnderline = findViewById(R.id.text_series_underline)

        btnMovies.setOnClickListener {
            setCategory(0)
        }
        btnSeries.setOnClickListener {
            setCategory(1)
        }

        prevCategory = nCategory
        setCategory(nCategory)
    }

    @SuppressLint("ResourceAsColor")
    private fun setCategory(cat: Int) {
        nCategory = cat

        if (cat == 0) {
            tvMoviesUnderline.visibility = View.VISIBLE
            tvSeriesUnderline.visibility = View.INVISIBLE
            btnMovies.setTextColor(Color.parseColor("#FFFFFF"))
            btnSeries.setTextColor(Color.parseColor("#FF9800"))
        }else {
            tvMoviesUnderline.visibility = View.INVISIBLE
            tvSeriesUnderline.visibility = View.VISIBLE
            btnMovies.setTextColor(Color.parseColor("#FF9800"))
            btnSeries.setTextColor(Color.parseColor("#FFFFFF"))
        }

        if (prevCategory != nCategory) {  initComponents() }
        prevCategory = nCategory
    }

    //Swipe
    private fun initSwipeLayout() {
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh)

        mSwipeRefreshLayout.setOnRefreshListener {
            if(nCurrentPage < nPage - 1) {
                nCurrentPage++
                getVideoList(nCurrentPage, true)
            }else {
                mSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initComponents(){
        gridView = this.findViewById(R.id.video_grid_view)

        var arr: ArrayList<Video> = ArrayList()
        if (nCategory == 0) {
            arr = arr_Videos
        }else {
            arr = arr_Series
        }

        val adapter = VideoAdapter(this, arr)
        gridView.setAdapter(adapter)

        gridView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedVideo: Video
                if (nCategory == 0) {
                    selectedVideo = arr_Videos.get(position)
                }else{
                    selectedVideo = arr_Series.get(position)
                }

                if (selectedVideo.movieLink == "") {
                    getMovieLink(position)
                }else {
                    goToVideoActivity(selectedVideo.movieLink)
                }
            }
        }
    }

    //Get Video List
    private fun getVideoList(page: Int, swipeRefresh: Boolean) {
        if (swipeRefresh == false) {
            hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show()
        }

        val body = JsonObject()
        body.addProperty("isPremium", 1)
        body.addProperty("type", 1)
        body.addProperty("page", page)

        Ion.with(this)
            .load(API_URL)
            .setJsonObjectBody(body)
            .asJsonObject()
            .setCallback(FutureCallback<JsonObject> { e, result ->
                if (swipeRefresh == false) {
                    if (this.hud != null) {
                        this.hud!!.dismiss()
                    }
                }else {
                    mSwipeRefreshLayout.isRefreshing = false
                }

                if (e == null) {
                    fetchJSONObject(result)
                }
            })
    }

    private fun fetchJSONObject(obj: JsonObject) {
        if (obj.get("Status").toString() == "200"){
            nTotal = obj.get("Total").asInt
            nLimit = obj.get("Limit").asInt
            nPage = ceil(nTotal.toDouble() / nLimit.toDouble()).toInt()
            var arr = JSONParser(this).parseJSONToVieoArray(obj.getAsJsonArray("Result"))

            for (video in arr) {
                arr_Videos.add(0, video)
                arr_Series.add(video)
            }

            initComponents()
        }
    }

    //Get Video Link
    private fun getMovieLink(position: Int) {
        val selectedVideo: Video = arr_Videos.get(position)

        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setMaxProgress(100)
            .show()

        val body = JsonObject()

        body.addProperty("isPremium", 0)
        body.addProperty("userId", 36)
        body.addProperty("movieId", selectedVideo.moveId)

        Ion.with(this)
            .load(MOVIE_URL)
            .setJsonObjectBody(body)
            .asJsonObject()
            .setCallback(FutureCallback<JsonObject> { e, result ->
                if (this.hud != null) {
                    this.hud!!.dismiss()
                }

                if (e == null) {
                    fetchMovieJSONObject(result, position)
                }
            })
    }

    private fun fetchMovieJSONObject(obj: JsonObject, position: Int) {
        if (obj.get("Status").toString() == "200"){
            var arr: JsonArray = obj.getAsJsonArray("Result")
            if (arr.count() > 0) {
                var link: String = JSONParser(this).parseJSONToMovieLink(arr[0] as JsonObject)
                var video: Video = arr_Videos.get(position)

                video.movieLink = link
                arr_Videos.set(position, video)
                if (link != "") {
                    goToVideoActivity(link)
                }
            }
        }
    }

    //Go To Video Activity
    private fun goToVideoActivity(video_url: String) {
        val intent = Intent(this, VideoActivity::class.java)
        intent.putExtra("video_url", video_url)
        startActivity(intent)
    }
}

