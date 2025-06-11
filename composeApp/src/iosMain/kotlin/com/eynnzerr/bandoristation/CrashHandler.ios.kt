package com.eynnzerr.bandoristation

import androidx.compose.ui.window.ComposeUIViewController
import com.eynnzerr.bandoristation.ui.crash.CrashReportPage
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import platform.Foundation.NSSetUncaughtExceptionHandler
import platform.Foundation.NSUncaughtExceptionHandler
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import kotlin.experimental.ExperimentalNativeApi
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)
actual fun installCrashHandler() {

    // handle kotlin exceptions
    setUnhandledExceptionHook { throwable ->
        val message = "platform: ${getPlatform().name} version: ${getPlatform().versionName}"
        AppLogger.d("iOS CrashHandler", message)

        val controller = ComposeUIViewController {
            CrashReportPage(message = message) {
                val root = UIApplication.sharedApplication.keyWindow?.rootViewController
                root?.dismissViewControllerAnimated(true, completion = {
                    dispatch_async(dispatch_get_main_queue()) {
                        terminateWithUnhandledException(throwable)
                    }
                })
            }
        }

        val windowScene = UIApplication.sharedApplication.connectedScenes.firstOrNull() as UIWindowScene?
        windowScene?.let { scene ->
            val window = scene.windows.firstOrNull() as UIWindow?
            window?.let {
                it.rootViewController = controller
                it.makeKeyAndVisible()
            }
        }
    }

    // handle native exceptions
    val handler: CPointer<NSUncaughtExceptionHandler> = staticCFunction { exception ->
        AppLogger.d("iOS CrashHandler", exception?.reason ?: "")
    }
    NSSetUncaughtExceptionHandler(handler)
}
