package com.eynnzerr.bandoristation.business.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.EditProfileInfo
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetEditProfileDataUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, EditProfileInfo, String>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<EditProfileInfo, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.GetInitialData(),
            token = token
        ).handle(
            onSuccess = { response ->
                val profileInfo = NetResponseHelper.parseApiResponse<EditProfileInfo>(response)
                return profileInfo?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error("Failed to parse getInitialData Result.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}