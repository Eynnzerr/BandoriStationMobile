package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarResult(
    val avatar: String,
)
