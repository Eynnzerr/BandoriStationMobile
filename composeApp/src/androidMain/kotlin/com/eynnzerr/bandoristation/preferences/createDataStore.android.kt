package com.eynnzerr.bandoristation.preferences

import com.eynnzerr.bandoristation.AppApplication

actual fun produceDataStorePath(): String
    = AppApplication.context.filesDir.resolve(dataStoreFileName).absolutePath