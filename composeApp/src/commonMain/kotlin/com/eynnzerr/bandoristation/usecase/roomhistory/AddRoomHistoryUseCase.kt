package com.eynnzerr.bandoristation.usecase.roomhistory

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.room.RoomHistory
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class AddRoomHistoryUseCase(
    private val appRepository: AppRepository
) : UseCase<RoomHistory, Unit, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: RoomHistory): UseCaseResult<Unit, String> {
        return try {
            val addNum = appRepository.addToHistory(parameters)
            if (addNum > 0) {
                AppLogger.d("AddRoomHistoryUseCase", "Successfully saved room history.")
                UseCaseResult.Success(Unit)   
            } else {
                UseCaseResult.Error("Failed to save room history.")
            }
        } catch (e: Exception) {
            UseCaseResult.Error(e.message ?: "")
        }
    }
}
