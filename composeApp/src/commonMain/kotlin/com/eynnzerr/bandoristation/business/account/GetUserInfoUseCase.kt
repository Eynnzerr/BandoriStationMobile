package com.eynnzerr.bandoristation.business.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetUserInfoUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Long, AccountInfo, String>(dispatcher) {

    override suspend fun execute(parameters: Long): UseCaseResult<AccountInfo, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()
        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.GetUserInfo(id = parameters),
            token = token,
        ).handle(
            onSuccess = { userInfoContent ->
                // successfully fetched user information.
                val userInfo = NetResponseHelper.parseApiResponse<AccountInfo>(userInfoContent)
                return userInfo?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error("Failed to parse GetUserInfo response.")
            },
            onFailure = {
                // failed on GetUserInfo.
                return UseCaseResult.Error(it)
            }
        )
    }
}