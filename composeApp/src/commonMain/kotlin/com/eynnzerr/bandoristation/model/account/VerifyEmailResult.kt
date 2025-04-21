package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailResult(
    val token: String = "",
)
