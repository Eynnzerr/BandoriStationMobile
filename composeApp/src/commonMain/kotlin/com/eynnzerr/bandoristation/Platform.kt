package com.eynnzerr.bandoristation

interface Platform {
    val name: String
    val versionName: String
}

expect fun getPlatform(): Platform