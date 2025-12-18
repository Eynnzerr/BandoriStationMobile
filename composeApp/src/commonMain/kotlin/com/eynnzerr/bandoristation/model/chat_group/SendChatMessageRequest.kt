package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class SendChatMessageRequest(
    val content: String,
    val username: String,
    val avatar: String,
)