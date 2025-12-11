package com.eynnzerr.bandoristation.handler

import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.chat.CheckUnreadChatUseCase
import com.eynnzerr.bandoristation.usecase.websocket.GetWebSocketStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn

class CheckUnreadChatHandler(
    private val getWebSocketStateUseCase: GetWebSocketStateUseCase,
    private val checkUnreadChatUseCase: CheckUnreadChatUseCase,
    private val scope: CoroutineScope
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val unreadState: SharedFlow<Boolean> =
        getWebSocketStateUseCase(Unit).flatMapLatest { result ->
            if (result is UseCaseResult.Success &&
                result.data is WebSocketClient.ConnectionState.Connected
            ) {
                flow {
                    delay(1000)
                    checkUnreadChatUseCase(Unit).mapNotNull { result ->
                        (result as? UseCaseResult.Success)?.data
                    }.collect {
                        emit(it)
                    }
                }
            } else {
                flow { emit(false) }
            }
        }.shareIn(scope, SharingStarted.Lazily, replay = 1)
}
