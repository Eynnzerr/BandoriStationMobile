package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock.System

class UpdateTimestampUseCase(
    dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, Long, Long>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<Long, Long>> {
        return flow {
            while (true) {
                emit(UseCaseResult.Success(System.now().toEpochMilliseconds()))
                delay(1000)
            }
        }
    }
}