package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
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