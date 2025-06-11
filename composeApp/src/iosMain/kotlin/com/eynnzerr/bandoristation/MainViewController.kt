package com.eynnzerr.bandoristation

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.eynnzerr.bandoristation.installCrashHandler

fun MainViewController(): UIViewController {
    installCrashHandler()
    return ComposeUIViewController { App() }
}
