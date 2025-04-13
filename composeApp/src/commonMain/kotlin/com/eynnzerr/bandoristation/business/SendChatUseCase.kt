package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class SendChatUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) : UseCase<String, Unit, Unit>(dispatcher) {

    override suspend fun execute(parameters: String): UseCaseResult<Unit, Unit> {
        repository.sendChat(parameters)
        return UseCaseResult.Success(Unit)
    }
}