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
    override fun execute(parameters: Unit): Flow<UseCaseResult<WebSocketClient.ConnectionState, Unit>> {
        return repository.listenWebSocketConnection().map { connectionState ->
            UseCaseResult.Success(connectionState)
        }
    }
}