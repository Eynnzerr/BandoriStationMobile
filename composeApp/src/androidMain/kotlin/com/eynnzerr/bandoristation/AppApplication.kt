package com.eynnzerr.bandoristation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.eynnzerr.bandoristation.installCrashHandler

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        installCrashHandler()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}