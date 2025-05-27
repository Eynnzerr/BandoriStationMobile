package com.eynnzerr.bandoristation.utils

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
suspend fun PlatformFile.toBase64String(): String? {
    return try {
        withContext(Dispatchers.IO) {
            val bytes = this@toBase64String.readBytes()
            val base64String = Base64.encode(bytes)
            "data:image/png;base64,$base64String"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}