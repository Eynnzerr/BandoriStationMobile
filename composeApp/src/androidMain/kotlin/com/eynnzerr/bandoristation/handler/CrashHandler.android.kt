package com.eynnzerr.bandoristation.handler

import android.content.Intent
import com.eynnzerr.bandoristation.AppApplication
import com.eynnzerr.bandoristation.CrashActivity
import com.eynnzerr.bandoristation.getPlatform

actual fun installCrashHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        e.printStackTrace()
        val ctx = AppApplication.context
        ctx.startActivity(
            Intent(ctx, CrashActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .apply {
                    putExtra(
                        "msg",
                        "platform: ${getPlatform().name} version: ${getPlatform().versionName}\n" +
                            e.stackTraceToString()
                    )
                }
        )
    }
}
