package com.eynnzerr.bandoristation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    @SerialName("timestamp") val timestamp: Long = 0,
    @SerialName("content") val content: String = "",
    @SerialName("user_info") val userInfo: UserInfo = UserInfo(),
)
