package com.eynnzerr.bandoristation.usecase.room

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.room.RoomUploadInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class UploadRoomUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<RoomUploadInfo, Unit, Unit>(dispatcher) {

    override suspend fun execute(parameters: RoomUploadInfo): UseCaseResult<Unit, Unit> {
        repository.uploadRoom(parameters)
        return UseCaseResult.Success(Unit)
    }
}