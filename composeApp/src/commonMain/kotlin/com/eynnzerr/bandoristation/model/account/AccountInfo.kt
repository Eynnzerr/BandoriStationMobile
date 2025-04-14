package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class AccountInfo(
    @SerialName("summary") val summary: Summary = Summary(),
    @SerialName("room_number_history") val roomNumberHistory: ArrayList<RoomNumberHistory> = arrayListOf(),
    @SerialName("room_number_history_append_flag") val roomNumberHistoryAppendFlag: Boolean = false,
    // @SerialName("bandori_profile") val bandoriProfile: BandoriProfile = BandoriProfile(),
    @SerialName("is_self") val isSelf: Boolean = true,
)

@Serializable
data class Summary(
    @SerialName("user_id") val userId: Long = 0,
    @SerialName("username") val username: String = "未登录",
    @SerialName("avatar") val avatar: String = "",
    @SerialName("banner") val banner: String = "",
    @SerialName("introduction") val introduction: String = "",
    @SerialName("role") val role: Int = 0,
    // @SerialName("main_game_account") val mainGameAccount: ArrayList<String> = arrayListOf(),
    @SerialName("ban_status") val banStatus: BanStatus = BanStatus(),
    @SerialName("following") val following: Int = 0,
    @SerialName("follower") val follower: Int = 0
)

@Serializable
data class RoomNumberHistory(
    @SerialName("number") val number: String = "",
    @SerialName("raw_message") val rawMessage: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("time") val time: Long = 0,
    @SerialName("source_name") val sourceName: String = ""
)

@Serializable
data class BanStatus(
    @SerialName("is_banned") val isBanned: Boolean = false,
    @SerialName("interval") val interval: Long = 0
)

@Serializable
data class BandoriProfile(
    @SerialName("jp") val jp: ArrayList<String> = arrayListOf(),
    @SerialName("cn") val cn: ArrayList<String> = arrayListOf() // TODO
)