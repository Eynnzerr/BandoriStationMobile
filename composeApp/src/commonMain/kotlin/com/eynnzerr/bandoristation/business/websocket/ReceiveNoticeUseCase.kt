package com.eynnzerr.bandoristation.business.websocket

import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceiveNoticeUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, String, Unit>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<String, Unit>> {
        return repository.listenWebSocketForActions(listOf("sendNotice"))
            .map { response ->
                val notice = NetResponseHelper.parseWebSocketResponse<String>(response)
                notice?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}