package com.eynnzerr.bandoristation.data

import com.eynnzerr.bandoristation.data.remote.RemoteDataSource
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.RoomUploadInfo

class AppRepository(
    private val remoteDataSource: RemoteDataSource
) {
    // control websocket
    suspend fun connectWebSocket() = remoteDataSource.connectWebSocket()
    fun disconnectWebSocket() = remoteDataSource.disconnectWebSocket()
    fun listenWebSocketForActions(actions: List<String>) = remoteDataSource.listenWebSocketForActions(actions)
    fun listenForAll() = remoteDataSource.listenForAll()
    fun listenWebSocketConnection() = remoteDataSource.webSocketConnectionState

    // page business
    suspend fun setWebSocketApiClient(params: ClientSetInfo) = remoteDataSource.setWebSocketApiClient(params)
    suspend fun setAccessPermission(token: String) = remoteDataSource.setAccessPermission(token)

    suspend fun initializeChatRoom() = remoteDataSource.initializeChatRoom()
    suspend fun checkUnreadChat() = remoteDataSource.checkUnreadChat()
    suspend fun uploadRoom(params: RoomUploadInfo) = remoteDataSource.uploadRoom(params)
    suspend fun sendChat(message: String) = remoteDataSource.sendChat(message)
    suspend fun getFirstRoomList() = remoteDataSource.getFirstRoomList()
    suspend fun loadChatHistory(lastTimestamp: Long) = remoteDataSource.loadChatHistory(lastTimestamp)

    // HTTPS
    suspend fun sendHttpsRequest(request: ApiRequest) = remoteDataSource.sendHttpsRequest(request)
    suspend fun sendAuthenticHttpsRequest(
        request: ApiRequest,
        token: String
    ) = remoteDataSource.sendAuthenticHttpsRequest(request, token)
    suspend fun sendApiRequest(request: ApiRequest) = remoteDataSource.sendApiRequest(request)
}