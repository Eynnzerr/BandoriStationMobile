package com.eynnzerr.bandoristation.usecase.social

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.AccountSummary
import com.eynnzerr.bandoristation.model.social.UserId
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetFollowerBriefUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Long, List<AccountSummary>, String>(dispatcher) {

    override suspend fun execute(parameters: Long): UseCaseResult<List<AccountSummary>, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.GetFollowerList(
                id = parameters
            ),
            token = token
        ).handle(
            onSuccess = { responseContent ->
                val followers = NetResponseHelper.parseApiResponse<List<UserId>>(responseContent)
                followers?.let {
                    val ids = followers.map { it.id }
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
                } ?: return UseCaseResult.Error("Failed to parse getFollowerList result.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}