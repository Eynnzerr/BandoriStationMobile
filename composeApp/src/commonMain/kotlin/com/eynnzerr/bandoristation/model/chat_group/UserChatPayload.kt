package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class UserChatPayload(
    val groupId: String,
    val user: SimpleUserInfo,
)