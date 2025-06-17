package com.eynnzerr.bandoristation.usecase.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.SignupParams
import com.eynnzerr.bandoristation.model.account.SignupResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher

class SignupUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<SignupParams, String, String>(dispatcher) {

    override suspend fun execute(params: SignupParams): UseCaseResult<String, String> {
        repository.sendHttpsRequest(
            request = ApiRequest.Signup(
                username = params.username,
                password = params.password,
                email = params.email,
            ),
        ).handle(
            onSuccess = { responseContent ->
                val signupResult = NetResponseHelper.parseApiResponse<SignupResult>(responseContent)
                return signupResult?.let {
                    dataStore.edit { p -> p[PreferenceKeys.TEMP_TOKEN] = it.token }
                    UseCaseResult.Success(it.token)
                } ?: UseCaseResult.Error("Failed to parse Signup response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}