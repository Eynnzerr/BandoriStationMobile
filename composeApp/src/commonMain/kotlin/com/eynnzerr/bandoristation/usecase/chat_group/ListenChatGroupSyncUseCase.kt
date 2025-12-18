package com.eynnzerr.bandoristation.usecase.chat_group

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.EncryptionSocketActions
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupChange
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupSyncData
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListenChatGroupSyncUseCase(
    private val repository: AppRepository,
) : FlowUseCase<Unit, ChatGroupSyncData, Unit>(Dispatchers.IO) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<ChatGroupSyncData, Unit>> {
        return repository.listenEncryptionSocketForActions(listOf(EncryptionSocketActions.CHAT_STATE_SYNC))
            .map { response ->
                val change = NetResponseHelper.parseWebSocketResponse<ChatGroupSyncData>(response)
                change?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
            }
    }
}