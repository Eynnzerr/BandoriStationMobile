package com.eynnzerr.bandoristation.usecase.social

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.AccountSummary
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetFollowingBriefUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, List<AccountSummary>, String>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<List<AccountSummary>, String> {
        val ids = dataStore.data
            .map { p -> p[PreferenceKeys.FOLLOWING_LIST] ?: emptySet() }
            .first()
            .map { it.toLong() }

        repository.sendApiRequest(
            request = ApiRequest.GetUserBriefInfo(
                ids = ids
            )
        ).handle(
            onSuccess = { responseContent ->
                val accountSummaryList = NetResponseHelper.parseApiResponse<List<AccountSummary>>(responseContent)
                return accountSummaryList?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error("Failed to parse getUserBriefInfo result.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            },
        )
    }
}