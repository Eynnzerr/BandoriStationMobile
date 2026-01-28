package com.eynnzerr.bandoristation

class JVMPlatform: Platform {
    override val type: String = "Java"
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val versionName: String = "1.0.4"
}

actual fun getPlatform(): Platform = JVMPlatform()