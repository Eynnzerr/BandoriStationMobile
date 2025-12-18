package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class SimpleUserInfo(
    val id: String,
    val name: String,
    val avatar: String
)