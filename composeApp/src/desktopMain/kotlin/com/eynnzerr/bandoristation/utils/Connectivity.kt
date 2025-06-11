package com.eynnzerr.bandoristation.utils

import java.net.NetworkInterface

actual fun isNetworkAvailable(): Boolean {
    val interfaces = NetworkInterface.getNetworkInterfaces() ?: return false
    for (iface in interfaces.asSequence()) {
        if (iface.isUp && !iface.isLoopback) {
            return true
        }
    }
    return false
}
