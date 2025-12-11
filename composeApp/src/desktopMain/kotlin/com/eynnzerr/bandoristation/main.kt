package com.eynnzerr.bandoristation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.desktop_icon
import com.eynnzerr.bandoristation.handler.installCrashHandler
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    installCrashHandler()

    Window(
        onCloseRequest = ::exitApplication,
        title = "BandoriStationM",
        icon = painterResource(Res.drawable.desktop_icon)
    ) {
        App()
    }
}