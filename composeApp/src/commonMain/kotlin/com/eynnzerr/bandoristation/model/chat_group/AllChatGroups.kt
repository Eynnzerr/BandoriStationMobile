package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class AllChatGroups(
    val chatGroups: List<ChatGroupDetails>,
)