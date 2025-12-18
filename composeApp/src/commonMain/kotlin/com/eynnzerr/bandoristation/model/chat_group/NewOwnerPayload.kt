package com.eynnzerr.bandoristation.model.chat_group

import kotlinx.serialization.Serializable

@Serializable
data class NewOwnerPayload(
    val groupId: String,
    val newOwnerId: String,
)
