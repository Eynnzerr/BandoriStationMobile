package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class OwnerInfo(
    val id: String,
    val name: String,
    val avatar: String
)

@Serializable
data class ChatGroupDetails(
    val id: String,
    val name: String,
    val owner: OwnerInfo,
    val memberCount: Long,
    val createdAt: String,
    val lastActivityAt: String
)