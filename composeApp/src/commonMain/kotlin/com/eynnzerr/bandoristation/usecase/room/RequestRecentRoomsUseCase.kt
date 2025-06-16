package com.eynnzerr.bandoristation.usecase.room

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class RequestRecentRoomsUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
): UseCase<Unit, Unit, Unit>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<Unit, Unit> {
        repository.getFirstRoomList()
        return UseCaseResult.Success(Unit)
    }
}