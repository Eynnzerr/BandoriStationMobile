package com.eynnzerr.bandoristation.feature.account

import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.business.account.GetEditProfileDataUseCase
import com.eynnzerr.bandoristation.business.account.GetSelfInfoUseCase
import com.eynnzerr.bandoristation.business.account.LoginUseCase
import com.eynnzerr.bandoristation.business.account.LogoutUseCase
import com.eynnzerr.bandoristation.business.account.SendVerificationCodeUseCase
import com.eynnzerr.bandoristation.business.account.SignupUseCase
import com.eynnzerr.bandoristation.business.account.VerifyEmailUseCase
import com.eynnzerr.bandoristation.business.datastore.GetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase.Params
import com.eynnzerr.bandoristation.business.social.FollowUserUseCase
import com.eynnzerr.bandoristation.business.social.GetFollowerBriefUseCase
import com.eynnzerr.bandoristation.business.social.GetFollowingBriefUseCase
import com.eynnzerr.bandoristation.feature.account.AccountEffect.*
import com.eynnzerr.bandoristation.feature.account.AccountIntent.*
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.LoginError
import com.eynnzerr.bandoristation.model.account.LoginParams
import com.eynnzerr.bandoristation.model.account.SignupParams
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AccountViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getSelfInfoUseCase: GetSelfInfoUseCase,
    private val signupUseCase: SignupUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val stringPreferenceUseCase: GetPreferenceUseCase<String>,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val getFollowingBriefUseCase: GetFollowingBriefUseCase,
    private val getFollowerBriefUseCase: GetFollowerBriefUseCase,
    private val getEditProfileDataUseCase: GetEditProfileDataUseCase,
    private val followUserUseCase: FollowUserUseCase,
) : BaseViewModel<AccountState, AccountIntent, AccountEffect>(
    initialState = AccountState.initial()
) {
    override suspend fun loadInitialData() {
        val token = stringPreferenceUseCase.invoke(GetPreferenceUseCase.Params(
            key = PreferenceKeys.USER_TOKEN,
            defaultValue = ""
        )).map {
            when (it) {
                is UseCaseResult.Error -> ""
                is UseCaseResult.Loading -> ""
                is UseCaseResult.Success -> it.data ?: ""
            }
        }.first()

        sendEvent(GetUserInfo(token))
    }

    override fun reduce(event: AccountIntent): Pair<AccountState?, AccountEffect?> {
       return when (event) {
           is UpdateAccountInfo -> {
               AccountState(
                   isLoading = false,
                   accountInfo = event.accountInfo,
                   isLoggedIn = event.accountInfo.isSelf,
               ) to ControlLoginDialog(visible = false)
           }

           is NotifyUpdateInfoFailed -> {
               sendEffect(ShowSnackbar(event.reason)) // side side effect

               AccountState(
                   isLoading = false,
                   accountInfo = AccountInfo(),
                   isLoggedIn = false,
               ) to ControlLoginDialog(visible = true)
           }

           is GetUserInfo -> {
               viewModelScope.launch {
                   val token = event.token
                   val userInfoResult = getSelfInfoUseCase(token)
                   when (userInfoResult) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEvent(NotifyUpdateInfoFailed(userInfoResult.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEvent(UpdateAccountInfo(userInfoResult.data))
                           setPreferenceUseCase(Params(
                               key = PreferenceKeys.USER_TOKEN,
                               value = token
                           ))
                           setAccessPermissionUseCase(token) // 每当重新拉取用户信息成功：用最新的token设置ws权限
                       }
                   }
               }
               null to null
           }

           is Login -> {
               viewModelScope.launch {
                   val params = LoginParams(
                       username = event.username,
                       password = event.password,
                   )
                   val loginResult = loginUseCase.invoke(params)
                   when (loginResult) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           when (val error = loginResult.error) {
                               is LoginError.NeedVerification -> {
                                   sendEffect(ControlLoginDialogScreen(LoginScreenState.VERIFY_EMAIL))
                               }
                               is LoginError.Other -> {
                                   sendEvent(NotifyUpdateInfoFailed(error.text))
                               }
                           }
                       }
                       is UseCaseResult.Success -> {
                           sendEvent(UpdateAccountInfo(loginResult.data))
                       }
                   }
               }
               null to null
           }

           is Logout -> {
               viewModelScope.launch {
                   val logoutResult = logoutUseCase.invoke(Unit)
                   when (logoutResult) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(logoutResult.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEvent(UpdateAccountInfo(AccountInfo()))
                           sendEffect(ControlDrawer(false))
                           sendEffect(ControlLoginDialogScreen(LoginScreenState.INITIAL))
                       }
                   }
               }
               null to null
           }

           is Signup -> {
               viewModelScope.launch {
                   val signupResult = signupUseCase.invoke(SignupParams(
                       username = event.username,
                       password = event.password,
                       email = event.email,
                   ))
                   when (signupResult) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(signupResult.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEffect(ControlLoginDialogScreen(LoginScreenState.VERIFY_EMAIL)) // 跳转到验证页面
                           sendEffect(ShowSnackbar("请在10分钟内完成邮箱验证。"))
                       }
                   }
               }
               null to null
           }

           is SendVerificationCode -> {
               viewModelScope.launch {
                   val sendCodeResult = sendVerificationCodeUseCase.invoke(Unit)
                   when (sendCodeResult) {
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

           is VerifyEmail -> {
               viewModelScope.launch {
                   val verifyEmailResult = verifyEmailUseCase.invoke(event.code)
                   when (verifyEmailResult) {
                       UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(verifyEmailResult.error))
                       }
                       is UseCaseResult.Success -> {
                           val token = verifyEmailResult.data
                           sendEvent(GetUserInfo(token))
                       }
                   }
               }
               null to null
           }

           is UpdateCountDown -> {
               state.value.copy(countDown = event.value) to null
           }

           is GetFollowers -> {
               viewModelScope.launch {
                   val selfId = state.value.accountInfo.accountSummary.userId
                   val followerBriefResult = getFollowerBriefUseCase.invoke(selfId)
                   when (followerBriefResult) {
                       UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(followerBriefResult.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(followers = followerBriefResult.data)
                           }
                       }
                   }
               }
               null to null
           }

           is GetFollowings -> {
               viewModelScope.launch {
                   val followingBriefResult = getFollowingBriefUseCase.invoke(Unit)
                   when (followingBriefResult) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(followingBriefResult.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(followings = followingBriefResult.data)
                           }
                       }
                   }
               }
               null to null
           }

           is GetEditProfileData -> {
               viewModelScope.launch {
                   val profileResult = getEditProfileDataUseCase.invoke(Unit)
                   when (profileResult) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(profileResult.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(editProfileData = profileResult.data)
                           }
                       }
                   }
               }
               null to null
           }

           is FollowUser -> {
               viewModelScope.launch {
                   val response = followUserUseCase.invoke(event.id)
                   when (response) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEffect(ShowSnackbar(response.data))
                       }
                   }
               }
               null to null
           }
       }
    }
}