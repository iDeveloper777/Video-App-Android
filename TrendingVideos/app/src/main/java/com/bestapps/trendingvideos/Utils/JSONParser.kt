package me.rohanjahagirdar.outofeden.Utils

import android.R
import android.app.PendingIntent.getActivity
import android.content.Context
import android.provider.MediaStore
import com.bestapps.trendingvideos.Video
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Created by Rohan Jahagirdar on 07-01-2018.
 */
class JSONParser(c: Context) {
    var context = c

    fun loadJSONFromAsset(): String {
        var json = null
        try{
            var inputStream = context.assets.open("walkofeden.json")
            var size = inputStream.available()
            var buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer, Charset.forName("UTF-8"))
        } catch(ex: IOException) {
            return ""
        }
    }


    fun parseJSON() : JSONObject{
        try {
            var obj = JSONObject(loadJSONFromAsset())
            return obj
        } catch(e: JSONException) {
            var obj = JSONObject()
            return obj
        }
    }

    // Parse Video Array
    fun parseJSONToVieoArray(arr: JsonArray): ArrayList<Video>{
        val arr_Videos: ArrayList<Video> = ArrayList()

        for (element in arr) {
            val video: Video? = getVideoFromJSONObject(element)
            video?.let { arr_Videos.add(it) }
        }

        return arr_Videos
    }

    fun getVideoFromJSONObject(element: JsonElement): Video? {
        val jsonObj = element as JsonObject
        return Video(jsonObj.get("movieId").asInt,
            jsonObj.get("title").asString,
            jsonObj.get("titleAr").asString,
            jsonObj.get("thumbLink").asString,
            jsonObj.get("thumbLinkLarge").asString,
            jsonObj.get("isPremium").asInt,
            jsonObj.get("avgRate").asInt,
            jsonObj.get("isLibraryList").asInt,
            "")
    }

    //Parse Moive Link
    fun parseJSONToMovieLink(obj: JsonObject): String {
        var link: String = ""

        val arr: JsonArray = obj.getAsJsonArray("movies")
        if (arr.count() > 0) {
            val movieObject: JsonObject = arr[0] as JsonObject
            val arrVideos: JsonArray = movieObject.getAsJsonArray("videos")
            if (arrVideos.count() > 0) {
                val videoObject: JsonObject = arrVideos[0] as JsonObject
                link = videoObject.get("videoLink").asString
            }
        }
        return link
    }
}