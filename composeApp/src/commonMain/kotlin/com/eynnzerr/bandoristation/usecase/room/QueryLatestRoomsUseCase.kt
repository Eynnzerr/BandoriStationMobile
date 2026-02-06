package com.eynnzerr.bandoristation.usecase.room

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.room.RoomInfo
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class QueryLatestRoomsUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<Long, List<RoomInfo>, String>(dispatcher) {

    override suspend fun execute(parameters: Long): UseCaseResult<List<RoomInfo>, String> {
        repository.queryLatestRooms(latestTime = parameters).handle(
            onSuccess = { responseContent ->
                val rooms = NetResponseHelper.parseApiResponse<List<RoomInfo>>(responseContent)
                return rooms?.let { UseCaseResult.Success(it) }
                    ?: UseCaseResult.Error("Failed to parse query_room_number response.")
            },
            onFailure = { error ->
                return UseCaseResult.Error(error)
            }
        )
    }
}
