package com.eynnzerr.bandoristation.usecase.chat

import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

class GetChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, List<ChatMessage>, List<ChatMessage>>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<List<ChatMessage>, List<ChatMessage>>> {
        AppLogger.d("GetChatUseCase", "Start listening for sendChat action.")

        return repository.listenWebSocketForActions(listOf("sendChat"))
            .map { response ->
                val newMessageList = NetResponseHelper.parseWebSocketResponse<List<ChatMessage>>(response)
                AppLogger.d("GetChatUseCase", "Receive new message list. Size: ${newMessageList?.size}")
                newMessageList?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(emptyList<ChatMessage>())
            }.onCompletion {
                AppLogger.d("GetChatUseCase", "sendChat flow is completed or canceled.")
            }
    }
}