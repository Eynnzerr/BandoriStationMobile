package com.eynnzerr.bandoristation.business.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.SendVerificationResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UpdateEmailSendVCodeUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.TEMP_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.UpdateEmailSendVCode(
                email = parameters,
            ),
            token = token
        ).handle(
            onSuccess = { responseContent ->
                val sendVerificationResult = NetResponseHelper.parseApiResponse<SendVerificationResult>(responseContent)
                return sendVerificationResult?.let { UseCaseResult.Success(it.email) } ?: UseCaseResult.Error("Failed to parse SendVerification response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}