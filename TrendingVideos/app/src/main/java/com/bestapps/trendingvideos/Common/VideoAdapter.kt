package com.bestapps.trendingvideos

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Outline
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.github.saran2020.sliderating.SlideRatingView
import com.squareup.picasso.Picasso

import java.lang.Exception
import java.net.URL


class VideoAdapter(private val context: Context,
                   private val dataSource: ArrayList<Video>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Video? {
        return dataSource.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.listview_item, parent, false)

        val titleTextView: TextView = rowView.findViewById(R.id.vido_title)
        val thumbImageView: ImageView = rowView.findViewById(R.id.video_image)
        val ratingBar: RatingBar = rowView.findViewById(R.id.rating_bar)
//        val ratingView: SlideRatingView = rowView.findViewById(R.id.slide_rating)

        val video: Video? = getItem(position)
        if (video != null) {
            titleTextView.setText(video.title)
            ratingBar.rating = video.avgRate.toFloat()

//            ratingView.setRating(video.avgRate.toFloat())

            Picasso.get()
                .load(video.thumbLink)
                .resize(200,200)
                .centerCrop()
//                .placeholder(R.drawable.cover_image)
                .into(thumbImageView, object: com.squareup.picasso.Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {

                    }
                })
        }

        return rowView
    }
}

