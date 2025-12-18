package com.eynnzerr.bandoristation.usecase.chat_group

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.EncryptionSocketActions
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListenErrorUseCase(
    private val repository: AppRepository,
) : FlowUseCase<Unit, String, Unit>(Dispatchers.IO) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<String, Unit>> {
        return repository.listenEncryptionSocketForActions(listOf(EncryptionSocketActions.ERROR))
            .map { response ->
                val error = NetResponseHelper.parseWebSocketResponse<String>(response)
                error?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}