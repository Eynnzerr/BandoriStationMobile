package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountSummary(
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