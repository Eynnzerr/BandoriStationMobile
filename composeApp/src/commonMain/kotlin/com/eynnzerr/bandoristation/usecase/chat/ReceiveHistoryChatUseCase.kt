package com.eynnzerr.bandoristation.usecase.chat

import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ChatLoadResponse
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceiveHistoryChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, ChatLoadResponse, Unit>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<ChatLoadResponse, Unit>> {
        return repository.listenWebSocketForActions(listOf("loadChatLog"))
            .map { response ->
                val initResponse = NetResponseHelper.parseWebSocketResponse<ChatLoadResponse>(response)
                initResponse?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}

