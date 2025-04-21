package com.eynnzerr.bandoristation.business.account

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.account.AccountSettings
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.CoroutineDispatcher

class GetUserInfoUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
): UseCase<String, AccountInfo, String>(dispatcher) {

    override suspend fun execute(token: String): UseCaseResult<AccountInfo, String> {
        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.InitializeAccountSetting(),
            token = token,
        ).handle(
            onSuccess = { usrSettingsContent ->
                val userSettings = NetResponseHelper.parseApiResponse<AccountSettings>(usrSettingsContent)
                if (userSettings != null) {
                    // using the fetched user_id to get full user information.
                    val userId = userSettings.usedId
                    AppLogger.d("GetUserInfoUseCase", "fetched used id: $userId")
                    repository.sendAuthenticHttpsRequest(
                        request = ApiRequest.GetUserInfo(id = userId),
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
                } else {
                    // response of InitializeAccountSetting is illegal.
                    return UseCaseResult.Error("Failed to parse InitializeAccountSetting response.")
                }
            },
            onFailure = {
                // failed on InitializeAccountSetting. Only hanppens when 1) no Internet 2) token invalid
                return UseCaseResult.Error(it)
            }
        )
    }
}