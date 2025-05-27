package com.eynnzerr.bandoristation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit

fun main() = application {
    FileKit.init(appId = "BandoriStation Mobile")

    Window(
        onCloseRequest = ::exitApplication,
        title = "BandoriStationM",
    ) {
        App()
    }
}