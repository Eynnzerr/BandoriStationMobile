package com.eynnzerr.bandoristation.usecase.encryption

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.EncryptionSocketActions
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListenRoomAccessRequestUseCase(
    private val repository: AppRepository,
) : FlowUseCase<Unit, RoomAccessRequest, Unit>(Dispatchers.IO) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<RoomAccessRequest, Unit>> {
        return repository.listenEncryptionSocketForActions(listOf(EncryptionSocketActions.ACCESS_REQUEST_RECEIVED))
            .map { response ->
                val notice = NetResponseHelper.parseWebSocketResponse<RoomAccessRequest>(response)
                notice?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}