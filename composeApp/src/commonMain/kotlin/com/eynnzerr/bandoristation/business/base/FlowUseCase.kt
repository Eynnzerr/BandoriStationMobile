package com.eynnzerr.bandoristation.business.base

import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in Parameters, Success, Error>(private val dispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: Parameters): Flow<UseCaseResult<Success, Error>> {
        return execute(parameters)
            .flowOn(dispatcher)
    }

    abstract fun execute(parameters: Parameters): Flow<UseCaseResult<Success, Error>>
}