package com.eynnzerr.bandoristation.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * @param type 用户类型
 * @param userId 用户ID
 * @param username 用户名
 * @param avatar 用户头像文件名
 * @param playerBriefInfo 公开的游戏数据
 * 注意：当被ChatMessage使用时，有效字段仅包括user_id、username、avatar
 */
@Serializable
data class UserInfo(
    @SerialName("type") val type: String? = null,
    @SerialName("user_id") val userId: Long? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("role") val role: Int? = null,
    @SerialName("bandori_player_brief_info") val playerBriefInfo: PlayerBrief? = null
)