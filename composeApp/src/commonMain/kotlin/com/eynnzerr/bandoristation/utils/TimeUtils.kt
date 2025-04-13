package com.eynnzerr.bandoristation.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatTimeDifference(currentTimeMillis: Long, pastTimeMillis: Long): String {
    val timeDiffMillis = currentTimeMillis - pastTimeMillis
    val timeDiffSeconds = (timeDiffMillis / 1000).toInt()

    val hours = timeDiffSeconds / 3600
    val minutes = (timeDiffSeconds % 3600) / 60
    val seconds = timeDiffSeconds % 60

    return buildString {
        if (hours > 0) {
            append("${hours}小时")
        }
        if (minutes > 0 || hours > 0) {
            append("${minutes}分钟")
        }
        append("${seconds}秒")
    }
}

fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}/${
        localDateTime.monthNumber.toString().padStart(2, '0')
    }/${
        localDateTime.dayOfMonth.toString().padStart(2, '0')
    } ${
        localDateTime.hour.toString().padStart(2, '0')
    }:${
        localDateTime.minute.toString().padStart(2, '0')
    }:${
        localDateTime.second.toString().padStart(2, '0')
    }"
}