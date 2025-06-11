package com.eynnzerr.bandoristation

import android.content.Intent

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
