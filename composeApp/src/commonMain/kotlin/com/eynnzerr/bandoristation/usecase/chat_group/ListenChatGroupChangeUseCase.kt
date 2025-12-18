package com.eynnzerr.bandoristation.usecase.chat_group

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.EncryptionSocketActions
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupChange
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListenChatGroupChangeUseCase (
    private val repository: AppRepository,
) : FlowUseCase<Unit, ChatGroupChange, Unit>(Dispatchers.IO) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<ChatGroupChange, Unit>> {
        return repository.listenEncryptionSocketForActions(listOf(EncryptionSocketActions.CHAT_GROUP_CHANGE))
            .map { response ->
                val change = NetResponseHelper.parseWebSocketResponse<ChatGroupChange>(response)
                change?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}