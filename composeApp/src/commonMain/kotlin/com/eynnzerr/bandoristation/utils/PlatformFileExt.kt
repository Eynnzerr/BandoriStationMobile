package com.eynnzerr.bandoristation.utils

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import network.chaintech.cmpimagepickncrop.utils.ImageFileFormat
import network.chaintech.cmpimagepickncrop.utils.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val prefix = "data:image/png;base64,"

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

fun ImageBitmap.resizeCenterCrop(targetWidth: Int, targetHeight: Int): ImageBitmap {
    val sourceRatio = width.toFloat() / height.toFloat()
    val targetRatio = targetWidth.toFloat() / targetHeight.toFloat()

    // 计算源图中需要使用的区域
    val (srcWidth, srcHeight, srcX, srcY) = if (sourceRatio > targetRatio) {
        // 源图更宽，需要裁剪左右两边
        val usedWidth = (height * targetRatio).toInt()
        val offsetX = (width - usedWidth) / 2
        IntSize(usedWidth, height) to IntOffset(offsetX, 0)
    } else {
        // 源图更高，需要裁剪上下两边
        val usedHeight = (width / targetRatio).toInt()
        val offsetY = (height - usedHeight) / 2
        IntSize(width, usedHeight) to IntOffset(0, offsetY)
    }.let { (size, offset) ->
        listOf(size.width, size.height, offset.x, offset.y)
    }

    // 直接从源图的指定区域缩放到目标尺寸
    val resultBitmap = ImageBitmap(targetWidth, targetHeight)
    val canvas = Canvas(resultBitmap)

    canvas.drawImageRect(
        image = this,
        srcOffset = IntOffset(srcX, srcY),
        srcSize = IntSize(srcWidth, srcHeight),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(targetWidth, targetHeight),
        paint = Paint().apply {
            isAntiAlias = true
            filterQuality = FilterQuality.High
        }
    )

    return resultBitmap
}
