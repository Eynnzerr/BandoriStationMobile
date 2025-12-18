package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class ChatGroupMessage(
    val id: Long,
    val senderId: String,
    val content: String,
    val username: String,
    val avatar: String,
    val createdAt: String,
    val type: GroupMessageType = GroupMessageType.TEXT,
)

enum class GroupMessageType {
    TEXT, SYSTEM
}