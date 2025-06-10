package com.eynnzerr.bandoristation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.random.Random

@Serializable
data class ChatMessage(
    @Transient val localId: String = Random.nextLong().toString(),
    @SerialName("timestamp") val timestamp: Long = 0,
    @SerialName("content") val content: String = "",
    @SerialName("user_info") val userInfo: UserInfo = UserInfo(),
)
