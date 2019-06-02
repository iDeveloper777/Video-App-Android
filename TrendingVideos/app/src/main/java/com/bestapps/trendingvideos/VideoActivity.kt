package com.bestapps.trendingvideos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import android.os.Build
import android.support.annotation.RequiresApi
import android.webkit.*
import com.kaopiz.kprogresshud.KProgressHUD


class VideoActivity : AppCompatActivity() {
    var video_url: String = "https://player.vimeo.com/external/312357694.m3u8?s=8ab61cfa2bc3da0b2d3c7f43a01b0ae10621714a"
    var jcVideoPlayerStandard: JCVideoPlayerStandard? = null
    var hud: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        setupVideoPlayer()
    }

    private fun setupVideoPlayer() {
        video_url = intent.getStringExtra("video_url")

        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setMaxProgress(100)
            .show()


//        jcVideoPlayerStandard = this.findViewById(R.id.videoplayer)
//        jcVideoPlayerStandard?.run {
//            setUp(video_url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "Video")
////            thumbImageView.setImageResource(R.drawable.cover_image)
//        }

        val vimeoVideo =
            "<html><body><iframe width=\"420\" height=\"315\" src=\"${video_url}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

        val webView: WebView = findViewById<WebView>(R.id.myWebView)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (hud != null) {
                    hud!!.dismiss()
                    hud = null
                }
                super.onPageFinished(view, url)
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
            }
        }

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.domStorageEnabled = true
        webView.loadData(vimeoVideo, "text/html", "utf-8")

    }

    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }
}
