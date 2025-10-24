package com.eynnzerr.bandoristation.feature.tutorial

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.feature.tutorial.TutorialEffect.*
import com.eynnzerr.bandoristation.feature.tutorial.TutorialIntent.*
import com.eynnzerr.bandoristation.model.ApiRequest.*
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.LoginError
import com.eynnzerr.bandoristation.model.account.LoginParams
import com.eynnzerr.bandoristation.model.account.SignupParams
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import com.eynnzerr.bandoristation.usecase.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.usecase.account.GetSelfInfoUseCase
import com.eynnzerr.bandoristation.usecase.account.LoginUseCase
import com.eynnzerr.bandoristation.usecase.account.LogoutUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordSendVCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordVerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.SendVerificationCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.SignupUseCase
import com.eynnzerr.bandoristation.usecase.account.VerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.encryption.GetEncryptionSocketStateUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RegisterEncryptionUseCase
import com.eynnzerr.bandoristation.usecase.websocket.GetWebSocketStateUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorialViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getSelfInfoUseCase: GetSelfInfoUseCase,
    private val signupUseCase: SignupUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val resetPasswordSendVCodeUseCase: ResetPasswordSendVCodeUseCase,
    private val resetPasswordVerifyEmailUseCase: ResetPasswordVerifyEmailUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val registerEncryptionUseCase: RegisterEncryptionUseCase,
    private val getWebSocketStateUseCase: GetWebSocketStateUseCase,
    private val getEncryptionSocketStateUseCase: GetEncryptionSocketStateUseCase,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<TutorialState, TutorialIntent, TutorialEffect>(
    initialState = TutorialState.initial()
) {
    override suspend fun onInitialize() {
        // 如果当前已经登录了则直接获取账号信息
        val token = dataStore.data.map {
            it[PreferenceKeys.USER_TOKEN] ?: ""
        }.first()
        if (token.isNotEmpty()) {
            sendEvent(TutorialIntent.GetUserInfo(token))
        }

        // 进入该页面一次后，之后不再显示该页面
        dataStore.edit { p -> p[PreferenceKeys.IS_FIRST_LAUNCH] = false }

        // 监听当前2个服务器ws的连接情况
        viewModelScope.launch {
            launch {
                getWebSocketStateUseCase(Unit)
                    .map { it is UseCaseResult.Success && it.data is WebSocketClient.ConnectionState.Connected }
                    .collect { isConnected ->
                        internalState.update { it.copy(isStationConnected = isConnected) }
                    }
            }

            launch {
                getEncryptionSocketStateUseCase(Unit)
                    .map { it is UseCaseResult.Success && it.data is WebSocketClient.ConnectionState.Connected }
                    .collect { isConnected ->
                        internalState.update { it.copy(isEncryptionConnected = isConnected) }
                    }
            }
        }
    }

    override fun reduce(event: TutorialIntent): Pair<TutorialState?, TutorialEffect?> {
        return when (event) {
            is TutorialIntent.GetUserInfo -> {
                // 直接使用token登录，或验证邮箱后
                viewModelScope.launch {
                    val token = event.token
                    when (val userInfoResult = getSelfInfoUseCase(token)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(userInfoResult.error))
                        }
                        is UseCaseResult.Success -> {
                            dataStore.edit { p ->
                                p[PreferenceKeys.USER_TOKEN] = token
                                p[PreferenceKeys.USER_NAME] = userInfoResult.data.accountSummary.username
                                p[PreferenceKeys.USER_AVATAR] = userInfoResult.data.accountSummary.avatar
                                p[PreferenceKeys.USER_ID] = userInfoResult.data.accountSummary.userId
                            }
                            internalState.update {
                                it.copy(
                                    isLoggedIn = true,
                                    username = userInfoResult.data.accountSummary.username,
                                    avatar = userInfoResult.data.accountSummary.avatar,
                                )
                            }
                            setAccessPermissionUseCase(token) // 登录成功后，用token设置ws权限
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.Login -> {
                viewModelScope.launch {
                    val params = LoginParams(
                        username = event.username,
                        password = event.password,
                    )
                    when (val loginResult = loginUseCase.invoke(params)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            when (loginResult.error) {
                                is LoginError.NeedVerification -> {
                                    sendEffect(
                                        ControlLoginDialogScreen(
                                            LoginScreenState.VERIFY_EMAIL
                                        )
                                    )
                                }
                                is LoginError.Other -> {
                                    sendEffect(ShowSnackbar(loginResult.error.text))
                                }
                            }
                        }
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(
                                    isLoggedIn = true,
                                    username = loginResult.data.accountSummary.username,
                                    avatar = loginResult.data.accountSummary.avatar,
                                )
                            }

                            sendEffect(ControlLoginDialog(false))
                            setAccessPermissionUseCase(null)
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.Logout -> {
                viewModelScope.launch {
                    when (val logoutResult = logoutUseCase.invoke(Unit)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(logoutResult.error))
                        }
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(
                                    isLoggedIn = false,
                                    username = "未登录",
                                    avatar = "",
                                )
                            }

                            sendEffect(ControlLoginDialogScreen(LoginScreenState.INITIAL))
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.ResetPassword -> {
                viewModelScope.launch {
                    when (val response = resetPasswordUseCase.invoke(event.password)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> sendEffect(ShowSnackbar(response.error))
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("重置密码成功。"))
                            sendEffect(ControlLoginDialogScreen(LoginScreenState.PASSWORD_LOGIN))
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.ResetPasswordSendVCode -> {
                viewModelScope.launch {
                    when (val result = resetPasswordSendVCodeUseCase.invoke(event.email)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> sendEffect(ShowSnackbar(result.error))
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("已发送验证码至${result.data}"))
                            var countDown = 60
                            while (countDown >= 0) {
                                delay(1000)
                                sendEvent(UpdateCountDown(countDown--))
                            }
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.ResetPasswordVerifyCode -> {
                viewModelScope.launch {
                    val result = resetPasswordVerifyEmailUseCase.invoke(
                        ResetPasswordVerifyEmail(
                            email = event.email,
                            code = event.code
                        )

                    )
                    when (result) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> sendEffect(ShowSnackbar(result.error))
                        is UseCaseResult.Success -> {
                            sendEffect(ControlLoginDialogScreen(LoginScreenState.RESET_PASSWORD))
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.SendVerificationCode -> {
                viewModelScope.launch {
                    when (val sendCodeResult = sendVerificationCodeUseCase.invoke(Unit)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(sendCodeResult.error))
                        }
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("已发送验证码至${sendCodeResult.data}"))
                            // 启动倒计时
                            var countDown = 60
                            while (countDown >= 0) {
                                delay(1000)
                                sendEvent(UpdateCountDown(countDown--))
                            }
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.Signup -> {
                viewModelScope.launch {
                    val signupResult = signupUseCase.invoke(
                        SignupParams(
                            username = event.username,
                            password = event.password,
                            email = event.email,
                        )
                    )
                    when (signupResult) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(signupResult.error))
                        }
                        is UseCaseResult.Success -> {
                            sendEffect(ControlLoginDialogScreen(LoginScreenState.VERIFY_EMAIL))
                            sendEffect(ShowSnackbar("请在10分钟内完成邮箱验证。"))
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.UpdateAccountInfo -> {
                state.value.copy(
                    username = event.accountInfo.accountSummary.username,
                    avatar = event.accountInfo.accountSummary.avatar,
                    isLoggedIn = event.accountInfo.isSelf,
                ) to ControlLoginDialog(visible = false)
            }

            is TutorialIntent.UpdateCountDown -> {
                state.value.copy(countDown = event.value) to null
            }

            is TutorialIntent.VerifyEmail -> {
                viewModelScope.launch {
                    when (val verifyEmailResult = verifyEmailUseCase.invoke(event.code)) {
                        UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(verifyEmailResult.error))
                        }
                        is UseCaseResult.Success -> {
                            val token = verifyEmailResult.data
                            sendEvent(TutorialIntent.GetUserInfo(token))
                        }
                    }
                }
                null to null
            }

            is TutorialIntent.RegisterEncryption -> {
                viewModelScope.launch {
                    when (val result = registerEncryptionUseCase.invoke(Unit)) {
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("注册/刷新加密服务成功。"))
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }
        }
    }

}