package com.eynnzerr.bandoristation.data.remote.websocket

import com.eynnzerr.bandoristation.model.WebSocketResponse
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object WebSocketHelper : KoinComponent {
    val json: Json by inject()

    inline fun <reified T> parseWebSocketResponse(response: WebSocketResponse<JsonElement>): T? {
        return try {
            json.decodeFromJsonElement(response.response) // extract response while ignoring status and action.
        } catch (e: Exception) {
            AppLogger.d("WebSocketHelper", "Failed to parse WebSocketResponse. Message: ${e.message}; Cause: ${e.cause}")
            null
        }
    }
}