package com.eynnzerr.bandoristation

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform