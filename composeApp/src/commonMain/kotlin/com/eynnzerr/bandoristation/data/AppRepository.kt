package com.eynnzerr.bandoristation.data

import com.eynnzerr.bandoristation.data.remote.RemoteDataSource
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.RoomUploadInfo

class AppRepository(
    private val remoteDataSource: RemoteDataSource
) {
    // control websocket
    suspend fun connectWebSocket() = remoteDataSource.connectWebSocket()
    fun disconnectWebSocket() = remoteDataSource.disconnectWebSocket()
    suspend fun <T> sendWebSocketRequest(action: String, data: T? = null) = remoteDataSource.sendWebSocketRequest(action, data)
    suspend fun <T> sendWebSocketRequestWithRetry(action: String, data: T? = null) = remoteDataSource.sendWebSocketRequestWithRetry(action, data, 0)
    fun listenWebSocketForActions(actions: List<String>) = remoteDataSource.listenWebSocketForActions(actions)
    fun listenForAll() = remoteDataSource.listenForAll()

    // page business
    suspend fun setWebSocketApiClient(params: ClientSetInfo) = remoteDataSource.setWebSocketApiClient(params)
    suspend fun setAccessPermission(token: String) = remoteDataSource.setAccessPermission(token)

    suspend fun initializeChatRoom() = remoteDataSource.initializeChatRoom()
    suspend fun checkUnreadChat() = remoteDataSource.checkUnreadChat()
    suspend fun uploadRoom(params: RoomUploadInfo) = remoteDataSource.uploadRoom(params)
    suspend fun sendChat(message: String) = remoteDataSource.sendChat(message)
    suspend fun getFirstRoomList() = remoteDataSource.getFirstRoomList()
    suspend fun loadChatHistory(lastTimestamp: Long) = remoteDataSource.loadChatHistory(lastTimestamp)
}