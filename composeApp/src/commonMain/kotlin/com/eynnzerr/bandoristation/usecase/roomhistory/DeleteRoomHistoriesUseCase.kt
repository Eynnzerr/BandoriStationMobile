package com.eynnzerr.bandoristation.usecase.roomhistory

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.room.RoomHistory
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class DeleteRoomHistoriesUseCase(
    private val appRepository: AppRepository
) : UseCase<List<RoomHistory>, Unit, Exception>(Dispatchers.IO) {

    override suspend fun execute(parameters: List<RoomHistory>): UseCaseResult<Unit, Exception> {
        return try {
            appRepository.deleteFromHistory(parameters)
            UseCaseResult.Success(Unit)
        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}
