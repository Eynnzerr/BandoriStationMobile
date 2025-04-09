package com.eynnzerr.bandoristation.preferences

import com.eynnzerr.AppApplication

actual fun produceDataStorePath(): String
    = AppApplication.context.filesDir.resolve(dataStoreFileName).absolutePath