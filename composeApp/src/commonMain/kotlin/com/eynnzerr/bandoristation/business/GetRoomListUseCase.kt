package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketHelper
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRoomListUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, List<RoomInfo>, List<RoomInfo>>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<List<RoomInfo>, List<RoomInfo>>> {
        return repository.listenWebSocketForActions(listOf("sendRoomNumberList"))
            .map { response ->
                val roomList = WebSocketHelper.parseWebSocketResponse<List<RoomInfo>>(response)
                roomList?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(emptyList())
            }
    }
}