package com.eynnzerr.bandoristation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomUploadInfo(
    @SerialName("room_number") val number: String = "",
    @SerialName("description") val description: String = "",
)
