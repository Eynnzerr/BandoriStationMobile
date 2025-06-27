package com.eynnzerr.bandoristation.utils

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import network.chaintech.cmpimagepickncrop.utils.ImageFileFormat
import network.chaintech.cmpimagepickncrop.utils.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val prefix = "data:image/png;base64,"

@OptIn(ExperimentalEncodingApi::class)
suspend fun PlatformFile.toBase64String(): String? {
    return try {
        withContext(Dispatchers.IO) {
            val bytes = this@toBase64String.readBytes()
            val base64String = Base64.encode(bytes)
            "$prefix$base64String"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalEncodingApi::class)
suspend fun ImageBitmap.toBase64String(): String? {
    return try {
        withContext(Dispatchers.IO) {
            val bytes = this@toBase64String.toByteArray(ImageFileFormat.PNG)
            bytes?.let { imageBytes ->
                val base64String = Base64.encode(imageBytes)
                "$prefix$base64String"
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun ImageBitmap.resizeTo(targetWidth: Int, targetHeight: Int): ImageBitmap {
    val newBitmap = ImageBitmap(targetWidth, targetHeight)
    val canvas = Canvas(newBitmap)

    canvas.drawImageRect(
        image = this,
        srcOffset = IntOffset.Zero,
        srcSize = IntSize(this.width, this.height),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(targetWidth, targetHeight),
        paint = Paint().apply {
            isAntiAlias = true
            filterQuality = FilterQuality.High
        }
    )

    return newBitmap
}