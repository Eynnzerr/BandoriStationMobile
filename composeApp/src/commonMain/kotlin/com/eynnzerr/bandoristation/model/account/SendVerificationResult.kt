package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable

@Serializable
data class SendVerificationResult(
    val email: String = ""
)
