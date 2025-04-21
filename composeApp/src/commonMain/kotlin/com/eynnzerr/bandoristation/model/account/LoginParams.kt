package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable

@Serializable
data class LoginParams(
    val username: String,
    val password: String,
)
