package com.eynnzerr.bandoristation.usecase.chat

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class RequestHistoryChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<Long, Unit, Unit>(dispatcher) {
    override suspend fun execute(parameters: Long): UseCaseResult<Unit, Unit> {
        repository.loadChatHistory(parameters)
        return UseCaseResult.Success(Unit)
    }
}