package com.eynnzerr.bandoristation.usecase.websocket

import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWebSocketStateUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
): FlowUseCase<Unit, WebSocketClient.ConnectionState, Unit>(dispatcher) {
//    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        throwable.printStackTrace()
//    }
//    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun execute(parameters: Unit): Flow<UseCaseResult<WebSocketClient.ConnectionState, Unit>> {
        // Connect to WebSocket server，and listen for webSocket connection state. Only called once when app starts.
//        scope.launch(exceptionHandler) {
//            repository.connectWebSocket()
//        }

        return repository.listenWebSocketConnection().map { connectionState ->
            UseCaseResult.Success(connectionState)
        }
    }
}