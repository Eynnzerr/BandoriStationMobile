package com.eynnzerr.bandoristation.data.remote.websocket

import com.eynnzerr.bandoristation.model.ApiResponseContent
import com.eynnzerr.bandoristation.model.WebSocketResponse
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NetResponseHelper : KoinComponent {
    val json: Json by inject()
    const val TAG = "NetResponseHelper"

    inline fun <reified T> parseWebSocketResponse(response: WebSocketResponse<JsonElement>): T? {
        return try {
            json.decodeFromJsonElement(response.response) // extract response while ignoring status and action.
        } catch (e: Exception) {
            AppLogger.d(TAG, "Failed to parse WebSocketResponse. Message: ${e.message}; Cause: ${e.cause}")
            null
        }
    }

    inline fun <reified T> parseApiResponse(response: ApiResponseContent): T? {
        return try {
            when (response) {
                is ApiResponseContent.ObjectContent -> {
                    json.decodeFromJsonElement(response.data)
                }
                is ApiResponseContent.StringContent -> {
                    AppLogger.d(TAG, "Ask for parseApiResponse for String response: ${response.text}")
                    null
                }
            }
        } catch (e: Exception) {
            AppLogger.d(TAG, "Failed to parse ApiResponseContent. Message: ${e.message}; Cause: ${e.cause}")
            null
        }
    }
}