package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignupResult(
    @SerialName("token") val token: String = "",
    @SerialName("redirect_to") val redirectTo: String = "",
)
