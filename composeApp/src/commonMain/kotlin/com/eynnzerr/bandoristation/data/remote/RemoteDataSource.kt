package com.eynnzerr.bandoristation.data.remote

import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.WebSocketResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonElement

interface RemoteDataSource {

    // websocket related
    val webSocketConnectionState: StateFlow<WebSocketClient.ConnectionState>
    val webSocketResponseFlow: SharedFlow<WebSocketResponse<JsonElement>>
    suspend fun connectWebSocket()
    suspend fun <T> sendWebSocketRequest(action: String, data: T? = null) // TODO 废弃
    suspend fun <T> sendWebSocketRequestWithRetry(action: String, data: T? = null, retryAttempts: Int = 0) // TODO 废弃
    fun listenWebSocketForActions(actions: List<String>): Flow<WebSocketResponse<JsonElement>>
    fun listenForAll(): Flow<WebSocketResponse<JsonElement>>
    fun disconnectWebSocket()

    // concrete business
    suspend fun setWebSocketApiClient(params: ClientSetInfo)
    suspend fun setAccessPermission(token: String)
    suspend fun initializeChatRoom()
    suspend fun checkUnreadChat()

    // HTTP API related
}