package com.eynnzerr.bandoristation.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.eynnzerr.bandoristation.AppApplication

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
actual fun isNetworkAvailable(): Boolean {
    val cm = AppApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    val ret = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    AppLogger.d("Connectivity", "isNetworkAvailable: $ret")
    return ret
}
