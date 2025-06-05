package com.eynnzerr.bandoristation

import androidx.compose.ui.window.ComposeUIViewController
import com.eynnzerr.bandoristation.ui.crash.CrashReportPage
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import kotlin.system.setUnhandledExceptionHook

actual fun installCrashHandler() {
    setUnhandledExceptionHook { throwable ->
        val message = "platform: ${getPlatform().name} version: ${getPlatform().versionName}\n" +
            throwable.stackTraceToString()
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController
        val controller = ComposeUIViewController {
            CrashReportPage(message = message) {
                root?.dismissViewControllerAnimated(true, null)
            }
        }
        root?.presentViewController(controller, true, null)
    }
}
