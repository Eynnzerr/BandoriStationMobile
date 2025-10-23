package com.eynnzerr.bandoristation.usecase.encryption

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class RequestEncryptedRoomAccessUseCase(
    private val repository: AppRepository,
): UseCase<RoomAccessRequest, Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: RoomAccessRequest): UseCaseResult<Unit, Unit> {
        repository.requestRoomAccess(parameters)
        return UseCaseResult.Success(Unit)
    }
}