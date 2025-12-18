package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class ChatGroupChange(
    val chatGroups: List<ChatGroupDetails>,
    val changeStatus: GroupChangeStatus,
)

enum class GroupChangeStatus {
    UPSERTED,
    REMOVED
}