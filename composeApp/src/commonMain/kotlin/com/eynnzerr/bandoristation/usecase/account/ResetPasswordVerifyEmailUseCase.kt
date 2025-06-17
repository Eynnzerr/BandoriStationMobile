package com.eynnzerr.bandoristation.usecase.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.VerifyEmailResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.Dispatchers

class ResetPasswordVerifyEmailUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
) : UseCase<String, String, String>(Dispatchers.IO) {
    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
        repository.sendHttpsRequest(
            request = ApiRequest.VerifyEmail(parameters)
        ).handle(
            onSuccess = { response ->
                val result = NetResponseHelper.parseApiResponse<VerifyEmailResult>(response)
                return result?.let {
                    dataStore.edit { p -> p[PreferenceKeys.TEMP_TOKEN] = it.token }
                    UseCaseResult.Success(it.token)
                } ?: UseCaseResult.Error("Failed to parse VerifyEmail response.")
            },
            onFailure = { return UseCaseResult.Error(it) }
        )
    }
}
