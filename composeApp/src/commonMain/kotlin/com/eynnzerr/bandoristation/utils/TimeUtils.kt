package com.eynnzerr.bandoristation.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatTimestamp(timestamp: Long): String {
    val timeAsSeconds = (timestamp / 1000).toInt()

    val hours = timeAsSeconds / 3600
    val minutes = (timeAsSeconds % 3600) / 60
    val seconds = timeAsSeconds % 60

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

fun formatTimeDifference(currentTimeMillis: Long, pastTimeMillis: Long) = formatTimestamp(currentTimeMillis - pastTimeMillis)

fun formatTimestampAsDate(timestamp: Long): String {
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