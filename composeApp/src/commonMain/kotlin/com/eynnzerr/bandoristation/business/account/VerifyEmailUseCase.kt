package com.eynnzerr.bandoristation.business.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.VerifyEmailResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class VerifyEmailUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, String, String>(dispatcher) {

    override suspend fun execute(code: String): UseCaseResult<String, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.TEMP_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.VerifyEmail(code),
            token = token
        ).handle(
            onSuccess = { responseContent ->
                val verifyEmailResult = NetResponseHelper.parseApiResponse<VerifyEmailResult>(responseContent)
                return verifyEmailResult?.let {
                    dataStore.edit { p -> p[PreferenceKeys.USER_TOKEN] = it.token }
                    UseCaseResult.Success(it.token)
                } ?: UseCaseResult.Error("Failed to parse VerifyEmail response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}