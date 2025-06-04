package com.eynnzerr.bandoristation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object PathUtils {
    @OptIn(ExperimentalForeignApi::class)
    fun getDocumentDirectoryUrl() = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )

    fun getDocumentDirectory() = requireNotNull(getDocumentDirectoryUrl()?.path)
}