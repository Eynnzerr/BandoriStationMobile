package com.eynnzerr.bandoristation.data.remote

import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.ApiResponse
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.room.RoomUploadInfo
import com.eynnzerr.bandoristation.model.WebSocketResponse
import com.eynnzerr.bandoristation.model.GithubRelease
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupMessage
import com.eynnzerr.bandoristation.model.chat_group.SendChatMessageRequest
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.model.room.RoomAccessResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonElement

interface RemoteDataSource {

    // websocket basic operation
    val webSocketConnectionState: StateFlow<WebSocketClient.ConnectionState>
    val webSocketResponseFlow: SharedFlow<WebSocketResponse<JsonElement>>
    suspend fun connectWebSocket()
    suspend fun <T> sendWebSocketRequest(action: String, data: T? = null)
    suspend fun <T> sendWebSocketRequestWithRetry(action: String, data: T? = null, retryAttempts: Int = 0)
    fun listenWebSocketForActions(actions: List<String>): Flow<WebSocketResponse<JsonElement>>
    fun listenForAll(): Flow<WebSocketResponse<JsonElement>>
    fun listenWebSocketConnectionState(): Flow<WebSocketClient.ConnectionState>
    suspend fun disconnectWebSocket()

    // websocket concrete business
    suspend fun getServerTimeOnce()
    suspend fun setWebSocketApiClient(params: ClientSetInfo)
    suspend fun setAccessPermission(token: String)
    suspend fun getFirstRoomList()
    suspend fun uploadRoom(params: RoomUploadInfo)
    suspend fun initializeChatRoom()
    suspend fun checkUnreadChat()
    suspend fun sendChat(message: String)
    suspend fun loadChatHistory(lastTimestamp: Long)

    // HTTPS API basic operation
    suspend fun sendHttpsRequest(request: ApiRequest): ApiResponse
    suspend fun sendAuthenticHttpsRequest(request: ApiRequest, token: String): ApiResponse

    suspend fun sendApiRequest(request: ApiRequest): ApiResponse

    suspend fun fetchLatestRelease(owner: String, repo: String): GithubRelease

    // Encryption related
    suspend fun sendEncryptionRequest(
        path: String,
        request: ApiRequest,
        token: String? = null,
    ): ApiResponse

    // Encryption websocket basic operation
    val encryptionSocketConnectionState: StateFlow<WebSocketClient.ConnectionState>
    val encryptionSocketResponseFlow: SharedFlow<WebSocketResponse<JsonElement>>
    suspend fun connectEncryptionWebSocket()
    suspend fun <T> sendEncryptionSocketRequest(action: String, data: T? = null)
    suspend fun <T> sendEncryptionSocketRequestWithRetry(action: String, data: T? = null, retryAttempts: Int = 0)
    fun listenEncryptionSocketForActions(actions: List<String>): Flow<WebSocketResponse<JsonElement>>
    fun listenEncryptionSocketConnectionState(): Flow<WebSocketClient.ConnectionState>
    suspend fun disconnectEncryptionSocket()

    // Encryption Business
    suspend fun requestRoomAccess(request: RoomAccessRequest)
    suspend fun respondRoomAccess(response: RoomAccessResponse)

    suspend fun sendChatGroupMessage(message: SendChatMessageRequest)
}