package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketHelper
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, List<ChatMessage>, List<ChatMessage>>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<List<ChatMessage>, List<ChatMessage>>> {
        return repository.listenWebSocketForActions(listOf("sendRoomNumberList"))
            .map { response ->
                val newMessageList = WebSocketHelper.parseWebSocketResponse<List<ChatMessage>>(response)
                newMessageList?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(emptyList())
            }
    }
}