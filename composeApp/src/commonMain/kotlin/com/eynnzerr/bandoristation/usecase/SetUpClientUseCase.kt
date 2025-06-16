package com.eynnzerr.bandoristation.usecase

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class SetUpClientUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<ClientSetInfo, Unit, Unit>(dispatcher) {

    override suspend fun execute(parameters: ClientSetInfo): UseCaseResult<Unit, Unit> {
        // simply call setClient
        repository.setWebSocketApiClient(parameters)
        return UseCaseResult.Success(Unit) // WebSocket has no sync response so just return Unit.
    }
}