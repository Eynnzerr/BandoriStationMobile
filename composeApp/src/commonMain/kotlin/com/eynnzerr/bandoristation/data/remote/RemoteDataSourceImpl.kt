package com.eynnzerr.bandoristation.data.remote

import com.eynnzerr.bandoristation.data.remote.https.HttpsClient
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.ApiResponse
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.RoomUploadInfo
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.delay

class RemoteDataSourceImpl(
    private val webSocketClient: WebSocketClient,
    private val httpsClient: HttpsClient,
): RemoteDataSource {
    override val webSocketConnectionState = webSocketClient.connectionState
    override val webSocketResponseFlow = webSocketClient.responseFlow

    private val maxResendAttempts = 5
    private val resendDelayMillis = 1000L

    override suspend fun connectWebSocket()
        = webSocketClient.connect()

    override suspend fun <T> sendWebSocketRequest(action: String, data: T?)
        = webSocketClient.sendRequest(action, data)

    override suspend fun <T> sendWebSocketRequestWithRetry(
        action: String,
        data: T?,
        retryAttempts: Int
    ) {
        if (webSocketConnectionState.value is WebSocketClient.ConnectionState.Connected) {
            webSocketClient.sendRequest(action, data)
        } else if (retryAttempts >= maxResendAttempts) {
            return
        } else {
            AppLogger.d(TAG, "Try to send $action when websocket is not connected. Resend after $resendDelayMillis ms.")
            delay(resendDelayMillis)
            sendWebSocketRequestWithRetry(action, data, retryAttempts + 1)
        }
    }

    override fun listenWebSocketForActions(actions: List<String>)
        = webSocketClient.listenForActions(actions)

    override fun listenForAll()
        = webSocketClient.listenForAll()

    override fun listenWebSocketConnectionState()
        = webSocketClient.connectionState

    override fun disconnectWebSocket()
        = webSocketClient.disconnect()

    override suspend fun setWebSocketApiClient(params: ClientSetInfo)
        = webSocketClient.sendRequestWithRetry(
            action = "setClient",
            data = params
        )

    override suspend fun setAccessPermission(token: String)
        = webSocketClient.sendRequestWithRetry(
            action = "setAccessPermission",
            data = mapOf("token" to token)
        )

    override suspend fun getFirstRoomList()
        = webSocketClient.sendRequestWithRetry(
            action = "getRoomNumberList",
            data = Unit,
        )

    override suspend fun initializeChatRoom()
        = webSocketClient.sendRequestWithRetry(
            action = "initializeChatRoom",
            data = Unit,
        )

    override suspend fun checkUnreadChat()
        = webSocketClient.sendRequestWithRetry(
            action = "checkUnreadChat",
            data = Unit,
        )

    override suspend fun uploadRoom(params: RoomUploadInfo)
        = webSocketClient.sendRequestWithRetry(
            action = "sendRoomNumber",
            data = params,
        )

    override suspend fun sendChat(message: String)
        = webSocketClient.sendRequestWithRetry(
            action = "sendChat",
            data = mapOf("message" to message)
        )

    override suspend fun loadChatHistory(lastTimestamp: Long)
        = webSocketClient.sendRequestWithRetry(
            action = "loadChatLog",
            data = mapOf("timestamp" to lastTimestamp),
        )

    override suspend fun sendHttpsRequest(request: ApiRequest): ApiResponse {
        // AppLogger.d(TAG, "Send https request ${request.group}:${request.function} to server.")
        return httpsClient.sendRequest(request)
    }

    override suspend fun sendAuthenticHttpsRequest(
        request: ApiRequest,
        token: String
    ): ApiResponse {
        // AppLogger.d(TAG, "Send https request ${request.group}:${request.function} to server with token: $token")
        return httpsClient.sendAuthenticatedRequest(request, token)
    }

    override suspend fun sendApiRequest(request: ApiRequest)
        = httpsClient.sendApiRequest(request)
}

private const val TAG = "RemoteDataSourceImpl"