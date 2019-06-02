package com.bestapps.trendingvideos.Common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.bestapps.trendingvideos.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class RoundedImageView1(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {
    constructor(context: Context) : this(context, null)

    private var drawable: Drawable? = null
        set(value) {
            field = value
            postInvalidate()
        }

    fun loadImage(url: String?) {
        if (url == null) {
            drawable = null
        } else {
            Picasso.get()
                .load(url)
//                .placeholder(R.drawable.cover_image)
                .into(this, object: com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        print("dd")
                    }

                    override fun onError(e: Exception?) {
                        print("dddd")
                    }
                })
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawable?.setBounds(0, 0, width, height)
        drawable?.draw(canvas)
    }

    fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        drawable = placeHolderDrawable
    }

    fun onBitmapFailed(errorDrawable: Drawable?) {
        drawable = errorDrawable
    }

    fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        val roundedDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedDrawable.cornerRadius = (DEFAULT_RADIUS).toFloat()
        drawable = roundedDrawable
    }

    companion object {
        private const val DEFAULT_RADIUS = 4
    }
}

private fun Any.into(roundedImageView: RoundedImageView1, callback: Callback) {

}
