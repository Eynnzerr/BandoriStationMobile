package com.eynnzerr.bandoristation.business.time

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class GetServerTimeUseCase(
    private val repository: AppRepository,
): UseCase<Unit, Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit): UseCaseResult<Unit, Unit> {
        repository.getServerTimeOnce()
        return UseCaseResult.Success(Unit)
    }
}