package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class AccountInfo(
    @SerialName("summary") val accountSummary: AccountSummary = AccountSummary(),
    @SerialName("room_number_history") val roomNumberHistory: ArrayList<RoomNumberHistory> = arrayListOf(),
    @SerialName("room_number_history_append_flag") val roomNumberHistoryAppendFlag: Boolean = false,
    // @SerialName("bandori_profile") val bandoriProfile: BandoriProfile = BandoriProfile(),
    @SerialName("is_self") val isSelf: Boolean = false,
)

@Serializable
data class RoomNumberHistory(
    @SerialName("number") val number: String = "",
    @SerialName("raw_message") val rawMessage: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("time") val time: Long = 0,
    @SerialName("source_name") val sourceName: String = ""
)

@Serializable
data class BandoriProfile(
    @SerialName("jp") val jp: ArrayList<String> = arrayListOf(),
    @SerialName("cn") val cn: ArrayList<String> = arrayListOf() // TODO
)