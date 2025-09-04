package com.eynnzerr.bandoristation

interface Platform {
    val type: String
    val name: String
    val versionName: String
}

expect fun getPlatform(): Platform