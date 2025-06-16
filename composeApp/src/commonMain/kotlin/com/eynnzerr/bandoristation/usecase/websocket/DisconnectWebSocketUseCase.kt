package com.eynnzerr.bandoristation.usecase.websocket

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class DisconnectWebSocketUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
): UseCase<Unit, Unit, Unit>(dispatcher) {
    override suspend fun execute(parameters: Unit): UseCaseResult<Unit, Unit> {
        repository.disconnectWebSocket()
        return UseCaseResult.Success(Unit)
    }
}