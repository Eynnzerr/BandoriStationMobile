package com.eynnzerr.bandoristation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.eynnzerr.bandoristation.ui.crash.CrashReportPage

actual fun installCrashHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        e.printStackTrace()
        val message = "platform: ${getPlatform().name} version: ${getPlatform().versionName}\n" +
            e.stackTraceToString()
        application {
            Window(onCloseRequest = ::exitApplication, title = "Crash Report") {
                CrashReportPage(message = message) {
                    exitApplication()
                }
            }
        }
    }
}
