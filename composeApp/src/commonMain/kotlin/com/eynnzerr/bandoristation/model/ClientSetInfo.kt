package com.eynnzerr.bandoristation.model

import com.eynnzerr.bandoristation.usecase.clientName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientSetInfo(
    @SerialName("client") val client: String,
    @SerialName("send_room_number") val sendRoomNumber: Boolean,
    @SerialName("send_chat") val sendChat: Boolean,
)