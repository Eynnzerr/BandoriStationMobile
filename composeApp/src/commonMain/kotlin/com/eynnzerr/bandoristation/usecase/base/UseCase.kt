package com.eynnzerr.bandoristation.usecase.base

import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in Parameters, Success, Error>(private val dispatcher: CoroutineDispatcher) {

    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(parameters: Parameters): UseCaseResult<Success, Error> {
        return withContext(dispatcher) {
            execute(parameters)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: Parameters): UseCaseResult<Success, Error>
}