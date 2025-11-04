package com.eynnzerr.bandoristation.usecase.encryption

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetEncryptionSocketStateUseCase(
    private val repository: AppRepository,
): FlowUseCase<Unit, WebSocketClient.ConnectionState, Unit>(Dispatchers.IO) {
    override fun execute(parameters: Unit): Flow<UseCaseResult<WebSocketClient.ConnectionState, Unit>> {
        return repository.listenEncryptionSocketConnectionState().map { connectionState ->
            UseCaseResult.Success(connectionState)
        }
    }
}