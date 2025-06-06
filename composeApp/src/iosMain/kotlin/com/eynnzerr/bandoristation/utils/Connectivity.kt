package com.eynnzerr.bandoristation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityFlags
import platform.SystemConfiguration.SCNetworkReachabilityFlagsVar
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable

@OptIn(ExperimentalForeignApi::class)
actual fun isNetworkAvailable(): Boolean = memScoped {
    val reachability = SCNetworkReachabilityCreateWithName(null, "apple.com")
        ?: return@memScoped false

    val flags = alloc<SCNetworkReachabilityFlagsVar>()
    val success = SCNetworkReachabilityGetFlags(reachability, flags.ptr)

    if (success) {
        val value = flags.value.toInt()
        val reachable = value and kSCNetworkReachabilityFlagsReachable.toInt() != 0
        val needConnection = value and kSCNetworkReachabilityFlagsConnectionRequired.toInt() != 0
        reachable && !needConnection
    } else {
        false
    }
}
