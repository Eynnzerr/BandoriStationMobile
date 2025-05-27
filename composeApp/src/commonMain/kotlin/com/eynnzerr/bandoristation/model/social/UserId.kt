package com.eynnzerr.bandoristation.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserId(
    @SerialName("user_id") val id: Long,
    val time: Long,
)
