package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ChatLoadResponse
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class InitializeChatRoomUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<Unit, ChatLoadResponse, ChatLoadResponse>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<ChatLoadResponse, ChatLoadResponse> {
        // First call initializeChatRoom and then listen for its response.
        repository.initializeChatRoom()

        return repository.listenWebSocketForActions(listOf("initializeChatRoom"))
            .map { response ->
                val initResponse = NetResponseHelper.parseWebSocketResponse<ChatLoadResponse>(response)
                initResponse?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(ChatLoadResponse())
            }.first()
    }
}