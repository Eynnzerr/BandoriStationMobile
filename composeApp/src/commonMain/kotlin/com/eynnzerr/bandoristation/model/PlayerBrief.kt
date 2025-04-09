package com.eynnzerr.bandoristation.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class PlayerBrief(
    @SerialName("server") val server: String = "",
    @SerialName("user_id") val userId: Int = -1,
    @SerialName("degrees") val degrees: ArrayList<Int> = arrayListOf(),
    @SerialName("main_deck") val mainDeck: ArrayList<MainDeck> = arrayListOf(),
    @SerialName("band_power") val bandPower: Int = -1,
    @SerialName("is_valid_power") val isValidPower: Boolean = false,
    @SerialName("latest_update_time") val latestUpdateTime: Int = 0,
)

@Serializable
data class MainDeck(
  @SerialName("illust") val illust: String = "",
  @SerialName("situationId") val situationId: Int = -1,
  @SerialName("trainingStatus") val trainingStatus: String = ""
)