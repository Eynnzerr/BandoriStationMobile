package com.eynnzerr.bandoristation.usecase.base

import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in Parameters, Success, Error>(private val dispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: Parameters): Flow<UseCaseResult<Success, Error>> {
        return execute(parameters)
            .flowOn(dispatcher)
            .catch { exception ->
                exception.printStackTrace()
            }
    }

    abstract fun execute(parameters: Parameters): Flow<UseCaseResult<Success, Error>>
}