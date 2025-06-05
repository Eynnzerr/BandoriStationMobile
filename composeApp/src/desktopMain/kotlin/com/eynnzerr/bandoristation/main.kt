package com.eynnzerr.bandoristation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.desktop_icon
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    FileKit.init(appId = "BandoriStation Mobile")

    Window(
        onCloseRequest = ::exitApplication,
        title = "BandoriStationM",
        icon = painterResource(Res.drawable.desktop_icon)
    ) {
        App()
    }
}