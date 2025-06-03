package com.eynnzerr.bandoristation.utils

import com.eynnzerr.bandoristation.model.RoomHistory
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

enum class TimeGranularity(val displayName: String) {
    DAILY("最近30天"),
    MONTHLY("最近12个月"),
    YEARLY("按年统计")
}

// 按时间粒度统计访问次数
fun getCountDataByGranularity(
    historyList: List<RoomHistory>,
    granularity: TimeGranularity
): List<Float> {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()

    val ret = when (granularity) {
        TimeGranularity.DAILY -> {
            // 最近30天，每天的访问次数
            val counts = mutableMapOf<LocalDate, Int>()
            val startDate = now.minus(30, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone).date

            // 初始化30天的数据
            for (i in 0 until 30) {
                val date = startDate.plus(i, DateTimeUnit.DAY)
                counts[date] = 0
            }

            // 统计每天的访问次数
            historyList.forEach { history ->
                val date = Instant.fromEpochMilliseconds(history.time)
                    .toLocalDateTime(timeZone).date
                if (date >= startDate) {
                    counts[date] = (counts[date] ?: 0) + 1
                }
            }

            counts.entries.sortedBy { it.key }.map { it.value.toFloat() }
        }

        TimeGranularity.MONTHLY -> {
            // 最近12个月，每月的访问次数
            val counts = mutableMapOf<String, Int>()
            val startMonth = now.minus(12, DateTimeUnit.MONTH, timeZone).toLocalDateTime(timeZone)

            // 初始化12个月的数据
            for (i in 0 until 12) {
                val month = startMonth.toInstant(timeZone).plus(i, DateTimeUnit.MONTH, timeZone)
                    .toLocalDateTime(timeZone)
                val key = "${month.year}-${month.monthNumber.toString().padStart(2, '0')}"
                counts[key] = 0
            }

            // 统计每月的访问次数
            historyList.forEach { history ->
                val dateTime = Instant.fromEpochMilliseconds(history.time)
                    .toLocalDateTime(timeZone)
                val key = "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}"
                if (counts.containsKey(key)) {
                    counts[key] = counts[key]!! + 1
                }
            }

            counts.entries.sortedBy { it.key }.map { it.value.toFloat() }
        }

        TimeGranularity.YEARLY -> {
            // 按年统计
            val counts = mutableMapOf<Int, Int>()

            historyList.forEach { history ->
                val year = Instant.fromEpochMilliseconds(history.time)
                    .toLocalDateTime(timeZone).year
                counts[year] = (counts[year] ?: 0) + 1
            }

            counts.entries.sortedBy { it.key }.map { it.value.toFloat() }
        }
    }

    AppLogger.d(TAG, "getCountDataByGranularity result: $ret")

    return ret
}

// 按时间粒度统计使用时长
fun getDurationDataByGranularity(
    historyList: List<RoomHistory>,
    granularity: TimeGranularity
): List<Float> {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()

    // 过滤掉duration为-1的记录
    val validHistoryList = historyList.filter { it.duration >= 0 }

    val ret = when (granularity) {
        TimeGranularity.DAILY -> {
            val durations = mutableMapOf<LocalDate, Long>()
            val startDate = now.minus(30, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone).date

            for (i in 0 until 30) {
                val date = startDate.plus(i, DateTimeUnit.DAY)
                durations[date] = 0L
            }

            validHistoryList.forEach { history ->
                val date = Instant.fromEpochMilliseconds(history.time)
                    .toLocalDateTime(timeZone).date
                if (date >= startDate) {
                    durations[date] = (durations[date] ?: 0L) + history.duration
                }
            }

            // 转换为分钟
            durations.entries.sortedBy { it.key }.map { it.value.toFloat() / 60000f }
        }

        TimeGranularity.MONTHLY -> {
            val durations = mutableMapOf<String, Long>()
            val startMonth = now.minus(12, DateTimeUnit.MONTH, timeZone).toLocalDateTime(timeZone)

            for (i in 0 until 12) {
                val month = startMonth.toInstant(timeZone).plus(i, DateTimeUnit.MONTH, timeZone)
                    .toLocalDateTime(timeZone)
                val key = "${month.year}-${month.monthNumber.toString().padStart(2, '0')}"
                durations[key] = 0L
            }

            validHistoryList.forEach { history ->
                val dateTime = Instant.fromEpochMilliseconds(history.time)
                    .toLocalDateTime(timeZone)
                val key = "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}"
                if (durations.containsKey(key)) {
                    durations[key] = durations[key]!! + history.duration
                }
            }

            durations.entries.sortedBy { it.key }.map { it.value.toFloat() / 60000f }
        }

        TimeGranularity.YEARLY -> {
            val durations = mutableMapOf<Int, Long>()

            validHistoryList.forEach { history ->
                val year = Instant.fromEpochMilliseconds(history.time)
                    .toLocalDateTime(timeZone).year
                durations[year] = (durations[year] ?: 0L) + history.duration
            }

            durations.entries.sortedBy { it.key }.map { it.value.toFloat() / 60000f }
        }
    }

    AppLogger.d(TAG, "getDurationDataByGranularity result: $ret")

    return ret
}

// 车头描述 词云提取 （单词划分依据：空格）
fun extractWords(text: String): List<String> {
    val cleanText = text.replace(Regex("<[^>]*>"), " ")

    return cleanText.split(Regex("\\s+"))
        .filter { word ->
            word.isNotBlank() && !isLongNumber(word)
        }
}

// 过滤车牌
fun isLongNumber(word: String): Boolean {
    return word.length > 5 && word.all { it.isDigit() }
}

fun extractKeywordsFromRoomHistory(
    historyList: List<RoomHistory>,
    topN: Int = 10
): Map<String, Int> {
    val wordCount = mutableMapOf<String, Int>()

    historyList.forEach { history ->
        val words = extractWords(history.rawMessage)
        words.forEach { word ->
            wordCount[word] = (wordCount[word] ?: 0) + 1
        }
    }

    // 返回出现次数最多的前N个词
    return wordCount.entries
        .sortedByDescending { it.value }
        .take(topN)
        .associate { it.key to it.value }
}

private const val TAG = "StatisticsUtils"