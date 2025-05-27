package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditProfileInfo(
    val username: String = "",
    val email: String = "",
    val qq: String = "",
    val banner: String = "",
    val introduction: String = "",
    @SerialName("player_id") val playerId: PlayerId = PlayerId(),
    @SerialName("main_game_account") val mainGameAccount: MainGameAccount = MainGameAccount(),
)

@Serializable
data class PlayerId(
    val cn: List<Long> = emptyList(),
    val jp: List<Long> = emptyList(),
)

@Serializable
data class MainGameAccount(
    val server: String = "",
    @SerialName("player_id") val playerId: Long = 0
)