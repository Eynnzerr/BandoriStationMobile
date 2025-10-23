package com.eynnzerr.bandoristation.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun generateUUID() : String = Uuid.random().toString()
