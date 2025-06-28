package com.eynnzerr.bandoristation

import android.os.Build
import com.eynnzerr.bandoristation.AppApplication

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val versionName: String
        get() = AppApplication.context.packageManager.getPackageInfo(
            AppApplication.context.packageName,
            0
        ).versionName ?: "1.0.0"
}

actual fun getPlatform(): Platform = AndroidPlatform()