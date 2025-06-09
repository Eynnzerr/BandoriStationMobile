package com.eynnzerr.bandoristation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.eynnzerr.bandoristation.AppApplication

actual fun isNetworkAvailable(): Boolean {
    val cm = AppApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
