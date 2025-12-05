package com.eynnzerr.bandoristation.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class FollowUserResult(
    @SerialName("user_id") val uid: Long = -1,
    @SerialName("time") val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
