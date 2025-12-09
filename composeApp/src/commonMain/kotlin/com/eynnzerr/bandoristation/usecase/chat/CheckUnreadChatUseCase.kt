package com.eynnzerr.bandoristation.usecase.chat

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CheckUnreadChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, Boolean, Boolean>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<Boolean, Boolean>> {
        return repository.listenWebSocketForActions(listOf("setChatUnreadBadge"))
            .onStart {
                repository.checkUnreadChat()
            }
            .map { response ->
                UseCaseResult.Success(response.status == "success")
            }
    }
}