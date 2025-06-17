package com.eynnzerr.bandoristation.usecase.account

import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.SendVerificationResult
import kotlinx.coroutines.Dispatchers

class ResetPasswordSendVCodeUseCase(
    private val repository: AppRepository,
) : UseCase<String, String, String>(Dispatchers.IO) {
    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
        repository.sendHttpsRequest(
            request = ApiRequest.ResetPasswordSendVCode(parameters)
        ).handle(
            onSuccess = { response ->
                val result = NetResponseHelper.parseApiResponse<SendVerificationResult>(response)
                return result?.let { UseCaseResult.Success(it.email) } ?: UseCaseResult.Error("Failed to parse SendVerification response.")
            },
            onFailure = { return UseCaseResult.Error(it) }
        )
    }
}
