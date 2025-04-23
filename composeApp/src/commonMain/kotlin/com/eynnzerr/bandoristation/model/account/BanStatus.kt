package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BanStatus(
    @SerialName("is_banned") val isBanned: Boolean = false,
    @SerialName("interval") val interval: Long = 0
)