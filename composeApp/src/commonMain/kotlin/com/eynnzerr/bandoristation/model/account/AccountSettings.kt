package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountSettings(
    @SerialName("user_id") val usedId: Long,
    @SerialName("avatar") val avatar: String,
    @SerialName("role") val role: Int,
)
