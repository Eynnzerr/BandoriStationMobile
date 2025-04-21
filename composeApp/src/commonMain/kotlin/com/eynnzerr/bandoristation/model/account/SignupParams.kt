package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable

@Serializable
data class SignupParams(
    val username: String,
    val password: String,
    val email: String,
)
