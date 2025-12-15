package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class ChatGroupSyncData(
    val groupId: String,
    val ownerId: String,
    val members: List<IdWrapper>,
    val recentMessages: List<ChatGroupMessage>
)

@Serializable
data class IdWrapper(
    val id: Long
)