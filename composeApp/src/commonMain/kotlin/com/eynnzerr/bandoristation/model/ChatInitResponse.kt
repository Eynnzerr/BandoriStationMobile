package com.eynnzerr.bandoristation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInitResponse(
    @SerialName("message_list") val messageList: List<ChatMessage> = emptyList(),
    @SerialName("self_id") val selfId: Long = 0,
    @SerialName("is_end") val isEnd: Boolean = true,
)