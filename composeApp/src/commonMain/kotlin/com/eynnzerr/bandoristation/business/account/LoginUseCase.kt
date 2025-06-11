package com.eynnzerr.bandoristation.business.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.LoginError
import com.eynnzerr.bandoristation.model.account.LoginParams
import com.eynnzerr.bandoristation.model.account.LoginResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.CoroutineDispatcher

class LoginUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<LoginParams, AccountInfo, LoginError>(dispatcher) {

    override suspend fun execute(parameters: LoginParams): UseCaseResult<AccountInfo, LoginError> {
        repository.sendHttpsRequest(
            request = ApiRequest.Login(parameters.username, parameters.password),
        ).handle(
            onSuccess = { response ->
                val loginResult = NetResponseHelper.parseApiResponse<LoginResult>(response)

                if (loginResult != null) {
                    AppLogger.d("LoginInfoUseCase", "user id: ${loginResult.userId}, token: ${loginResult.token}")
                    if (loginResult.userId < 0) {
                        // need to verify email and token is temporary.
                        dataStore.edit { p -> p[PreferenceKeys.TEMP_TOKEN] = loginResult.token }
                        return UseCaseResult.Error(LoginError.NeedVerification(token = loginResult.token))
                    }

                    // Store following account information.
                    dataStore.edit { p ->
                        p[PreferenceKeys.FOLLOWING_LIST] = loginResult.followingUsers.map { fu -> fu.id.toString() }.toSet()
                    }

                    repository.sendAuthenticHttpsRequest(
                        request = ApiRequest.GetUserInfo(id = loginResult.userId),
                        token = loginResult.token,
                    ).handle(
                        onSuccess = { userInfoContent ->
                            // successfully fetched user information.
                            val userInfo = NetResponseHelper.parseApiResponse<AccountInfo>(userInfoContent)
                            return userInfo?.let {
                                dataStore.edit { preferences ->
                                    preferences[PreferenceKeys.USER_TOKEN] = loginResult.token
                                    preferences[PreferenceKeys.USER_ID] = loginResult.userId
                                }
                                UseCaseResult.Success(it)
                            } ?: UseCaseResult.Error(
                                LoginError.Other("Failed to parse GetUserInfo response.")
                            )
                        },
                        onFailure = {
                            // failed on GetUserInfo.
                            return UseCaseResult.Error(LoginError.Other(it))
                        }
                    )
                } else {
                    // response of Login is illegal.
                    return UseCaseResult.Error(LoginError.Other("Failed to parse Login response."))
                }
            },
            onFailure = {
                // failed on Login. Only hanppens when 1) no Internet 2) token invalid
                return UseCaseResult.Error(LoginError.Other(it))
            }
        )
    }
}