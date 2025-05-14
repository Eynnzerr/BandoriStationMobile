package com.eynnzerr.bandoristation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomFilter(
    val type: List<String> = emptyList(),
    val keyword: List<String> = emptyList(),
    val user: List<UserInfo> = emptyList(),
) {
    fun isEmpty() = type.isEmpty() && keyword.isEmpty() && user.isEmpty()
}

@Serializable
data class RoomFilterWrapper(
    @SerialName("room_number_filter")
    val filter: RoomFilter,
)
