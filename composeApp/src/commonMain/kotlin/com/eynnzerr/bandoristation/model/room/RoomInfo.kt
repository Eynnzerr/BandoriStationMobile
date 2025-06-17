package com.eynnzerr.bandoristation.model.room

import com.eynnzerr.bandoristation.model.SourceInfo
import com.eynnzerr.bandoristation.model.UserInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * 房间信息
 * @param number 房间号
 * @param rawMessage 房间要求
 * @param sourceInfo 数据来源信息
 * @param type 房间类型
 * @param time 房间发布时间，13位时间戳（服务器时区）
 * @param userInfo 发布者用户信息
 */
@Serializable
data class RoomInfo(
    @SerialName("number") val number: String? = null,
    @SerialName("raw_message") val rawMessage: String? = null,
    @SerialName("source_info") val sourceInfo: SourceInfo? = SourceInfo(),
    @SerialName("type") val type: String? = null,
    @SerialName("time") val time: Long? = null,
    @SerialName("user_info") val userInfo: UserInfo? = UserInfo()
)