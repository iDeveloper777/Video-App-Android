package com.bestapps.trendingvideos.common

import io.branch.referral.Branch
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex


class GlobalApplication : Application() {

    protected override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        Branch.enableDebugMode()
        Branch.enableLogging()
        Branch.getAutoInstance(this).enableFacebookAppLinkCheck()


    }

}