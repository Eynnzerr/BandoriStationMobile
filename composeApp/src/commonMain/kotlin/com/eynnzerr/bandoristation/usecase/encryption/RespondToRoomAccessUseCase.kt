package com.eynnzerr.bandoristation.usecase.encryption

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.room.RoomAccessResponse
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class RespondToRoomAccessUseCase(
    private val repository: AppRepository,
): UseCase<RoomAccessResponse, Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: RoomAccessResponse): UseCaseResult<Unit, Unit> {
        repository.respondRoomAccess(parameters)
        return UseCaseResult.Success(Unit)
    }
}