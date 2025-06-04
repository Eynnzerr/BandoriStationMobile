package com.eynnzerr.bandoristation.business.roomhistory

import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.RoomHistory
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class FetchAllHistoryUseCase(
    private val appRepository: AppRepository
) : FlowUseCase<Long?, List<RoomHistory>, Exception>(Dispatchers.IO) {

    override fun execute(parameters: Long?): Flow<UseCaseResult<List<RoomHistory>, Exception>> {
        return appRepository.fetchAllHistory(parameters)
            .map<List<RoomHistory>, UseCaseResult<List<RoomHistory>, Exception>> { historyList ->
                UseCaseResult.Success(historyList)
            }
            .catch { throwable ->
                emit(UseCaseResult.Error(throwable as? Exception ?: RuntimeException("Error fetching history")))
            }
    }
}
