package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class ChatGroupSyncData(
    val groupId: String = GROUP_NOT_EXIST,
    val ownerId: String,
    val name: String = "",
    val members: List<IdWrapper>,
    val recentMessages: List<ChatGroupMessage>
)

@Serializable
data class IdWrapper(
    val id: Long
)

const val GROUP_NOT_EXIST = "-1"