package com.eynnzerr.bandoristation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            startActivity(
                Intent(this, CrashActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .apply {
                        putExtra("msg", "platform: ${getPlatform().name} version: ${getPlatform().versionName}\n" + e.stackTraceToString())
                    }
            )
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}