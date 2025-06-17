package com.eynnzerr.bandoristation.usecase.room

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetRoomListUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, List<RoomInfo>, List<RoomInfo>>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<List<RoomInfo>, List<RoomInfo>>> {
        // First, call getRoomNumberList to get first batch of room data. Then, listen for sendRoomNumberList.
        return repository.listenWebSocketForActions(listOf("sendRoomNumberList"))
            .onStart {
                repository.getFirstRoomList()
            }
            .map { response ->
                val roomList = NetResponseHelper.parseWebSocketResponse<List<RoomInfo>>(response)
                roomList?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(emptyList())
            }
    }
}