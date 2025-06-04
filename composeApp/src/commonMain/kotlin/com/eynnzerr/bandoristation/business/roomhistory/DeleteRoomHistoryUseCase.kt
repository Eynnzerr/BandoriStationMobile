package com.eynnzerr.bandoristation.business.roomhistory

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.RoomHistory
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class DeleteRoomHistoryUseCase(
    private val appRepository: AppRepository
) : UseCase<RoomHistory, Unit, Exception>(Dispatchers.IO) {

    override suspend fun execute(parameters: RoomHistory): UseCaseResult<Unit, Exception> {
        return try {
            appRepository.deleteFromHistory(parameters)
            UseCaseResult.Success(Unit)
        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}
