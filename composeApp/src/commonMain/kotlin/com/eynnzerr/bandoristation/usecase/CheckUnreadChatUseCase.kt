package com.eynnzerr.bandoristation.usecase

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CheckUnreadChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<Unit, Boolean, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<Boolean, Boolean> {
        // First call checkUnreadChat and then listen for its response.
        repository.checkUnreadChat()

        return repository.listenWebSocketForActions(listOf("setChatUnreadBadge"))
            .map { response ->
                UseCaseResult.Success(response.status == "success")
            }.first()
    }
}