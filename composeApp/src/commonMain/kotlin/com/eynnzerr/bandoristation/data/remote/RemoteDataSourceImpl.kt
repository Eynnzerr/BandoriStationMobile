package com.eynnzerr.bandoristation.data.remote

import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.RoomUploadInfo
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.delay

class RemoteDataSourceImpl(
    private val webSocketClient: WebSocketClient, // Injected
): RemoteDataSource {
    override val webSocketConnectionState = webSocketClient.connectionState
    override val webSocketResponseFlow = webSocketClient.responseFlow

    private val maxResendAttempts = 5
    private val resendDelayMillis = 1000L

    override suspend fun connectWebSocket()
        = webSocketClient.connect()

    // TODO BUG：非reified导致T的类型被丢失，无法传给reified函数sendRequest，导致Serializer报空指针
    override suspend fun <T> sendWebSocketRequest(action: String, data: T?)
        = webSocketClient.sendRequest(action, data)

    override suspend fun <T> sendWebSocketRequestWithRetry(
        action: String,
        data: T?,
        retryAttempts: Int
    ) {
        if (webSocketConnectionState.value is WebSocketClient.ConnectionState.Connected) {
            AppLogger.d(TAG, "Ready to send $action. Current retry attempts: $retryAttempts")
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
}

private const val TAG = "RemoteDataSourceImpl"