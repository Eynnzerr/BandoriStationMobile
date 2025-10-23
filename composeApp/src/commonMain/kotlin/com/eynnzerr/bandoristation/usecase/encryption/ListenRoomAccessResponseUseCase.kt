package com.eynnzerr.bandoristation.usecase.encryption

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.EncryptionSocketActions
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.room.RoomAccessResponse
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListenRoomAccessResponseUseCase(
    private val repository: AppRepository,
) : FlowUseCase<Unit, RoomAccessResponse, Unit>(Dispatchers.IO) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<RoomAccessResponse, Unit>> {
        return repository.listenEncryptionSocketForActions(listOf(EncryptionSocketActions.ACCESS_RESULT))
            .map { response ->
                val notice = NetResponseHelper.parseWebSocketResponse<RoomAccessResponse>(response)
                notice?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}