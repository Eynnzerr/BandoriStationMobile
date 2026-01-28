package com.eynnzerr.bandoristation.feature.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.usecase.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.usecase.account.GetEditProfileDataUseCase
import com.eynnzerr.bandoristation.usecase.account.GetSelfInfoUseCase
import com.eynnzerr.bandoristation.usecase.account.LoginUseCase
import com.eynnzerr.bandoristation.usecase.account.LogoutUseCase
import com.eynnzerr.bandoristation.usecase.account.SendVerificationCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.SignupUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateAccountAggregator
import com.eynnzerr.bandoristation.usecase.account.VerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordSendVCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordVerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.RoomHistoryAggregator
import com.eynnzerr.bandoristation.usecase.social.FollowUserUseCase
import com.eynnzerr.bandoristation.usecase.social.GetFollowerBriefUseCase
import com.eynnzerr.bandoristation.usecase.social.GetFollowingBriefUseCase
import com.eynnzerr.bandoristation.feature.account.AccountEffect.*
import com.eynnzerr.bandoristation.feature.account.AccountIntent.*
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.LoginError
import com.eynnzerr.bandoristation.model.account.LoginParams
import com.eynnzerr.bandoristation.model.account.SignupParams
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import com.eynnzerr.bandoristation.utils.AppLogger
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
    private val resetPasswordSendVCodeUseCase: ResetPasswordSendVCodeUseCase,
    private val resetPasswordVerifyEmailUseCase: ResetPasswordVerifyEmailUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val getFollowingBriefUseCase: GetFollowingBriefUseCase,
    private val getFollowerBriefUseCase: GetFollowerBriefUseCase,
    private val getEditProfileDataUseCase: GetEditProfileDataUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val updateAccountAggregator: UpdateAccountAggregator,
    private val roomHistoryAggregator: RoomHistoryAggregator,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<AccountState, AccountIntent, AccountEffect>(
    initialState = AccountState.initial()
) {
    override suspend fun onInitialize() {
        val token = dataStore.data.map {
            it[PreferenceKeys.USER_TOKEN] ?: ""
        }.first()
        if (token.isNotEmpty()) {
            sendEvent(GetUserInfo(token))
        } else {
            sendEffect(ControlLoginDialog(true))
        }

        viewModelScope.launch {
            roomHistoryAggregator.fetchAllHistory(null).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> {
                        sendEffect(ShowSnackbar(result.error.message ?: ""))
                    }
                    is UseCaseResult.Success -> {
                        AppLogger.d(TAG, "Successfully fetched room history. length: ${result.data.size}")
                        sendEvent(UpdateRoomHistory(result.data))
                    }
                }
            }
        }

        viewModelScope.launch {
            dataStore.data.collect { p ->
                val followingSet = p[PreferenceKeys.FOLLOWING_LIST] ?: emptySet()
                AppLogger.d(TAG, "Fetch following user id set: $followingSet")
                internalState.update {
                    it.copy(
                        followingIdList = followingSet.map { item -> item.toLongOrNull() ?: 0 }.toSet(),
                        accountInfo = it.accountInfo.copy(
                            accountSummary = it.accountInfo.accountSummary.copy(
                                following = followingSet.size
                            )
                        )
                    )
                }
            }
        }
    }

    override fun reduce(event: AccountIntent): Pair<AccountState?, AccountEffect?> {
       return when (event) {
           is UpdateAccountInfo -> {
               state.value.copy(
                   isLoading = false,
                   accountInfo = event.accountInfo,
                   isLoggedIn = event.accountInfo.isSelf,
               ) to ControlLoginDialog(visible = false)
           }

           is NotifyUpdateInfoFailed -> {
               sendEffect(ShowSnackbar(event.reason)) // side side effect

               state.value.copy(
                   isLoading = false,
                   accountInfo = AccountInfo(),
                   isLoggedIn = false,
               ) to ControlLoginDialog(visible = true)
           }

           is GetUserInfo -> {
               viewModelScope.launch {
                   val token = event.token
                   when (val userInfoResult = getSelfInfoUseCase(token)) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEvent(NotifyUpdateInfoFailed(userInfoResult.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEvent(UpdateAccountInfo(userInfoResult.data))
                           dataStore.edit { p ->
                               p[PreferenceKeys.USER_TOKEN] = token
                               p[PreferenceKeys.USER_NAME] = userInfoResult.data.accountSummary.username
                               p[PreferenceKeys.USER_AVATAR] = userInfoResult.data.accountSummary.avatar
                           }
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
                   when (val loginResult = loginUseCase.invoke(params)) {
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
                           setAccessPermissionUseCase.invoke(null)
                       }
                   }
               }
               null to null
           }

           is Logout -> {
               viewModelScope.launch {
                   when (val logoutResult = logoutUseCase.invoke(Unit)) {
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

           is VerifyEmail -> {
               viewModelScope.launch {
                   when (val verifyEmailResult = verifyEmailUseCase.invoke(event.code)) {
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

            is ResetPasswordSendVCode -> {
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

            is ResetPasswordVerifyCode -> {
                viewModelScope.launch {
                    val result = resetPasswordVerifyEmailUseCase.invoke(
                        ApiRequest.ResetPasswordVerifyEmail(
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

            is ResetPassword -> {
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

           is UpdateCountDown -> {
               state.value.copy(countDown = event.value) to null
           }

           is GetFollowers -> {
               viewModelScope.launch {
                   val selfId = state.value.accountInfo.accountSummary.userId
                   when (val followerBriefResult = getFollowerBriefUseCase.invoke(selfId)) {
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
                   when (val followingBriefResult = getFollowingBriefUseCase.invoke(Unit)) {
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
                   when (val profileResult = getEditProfileDataUseCase.invoke(Unit)) {
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
                   when (val response = followUserUseCase.invoke(event.id)) {
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

           is BindQQ -> {
               viewModelScope.launch {
                   when (val response = updateAccountAggregator.bindQQ(event.qq)) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(
                                   editProfileData = state.value.editProfileData.copy(
                                       qq = event.qq.toString()
                                   )
                               )
                           }
                       }
                   }
               }
               null to null
           }

           is UpdateAvatar -> {
               viewModelScope.launch {
                   when (val response = updateAccountAggregator.updateAvatar(event.avatarBase64)) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEvent(UpdateAccountInfo(
                               accountInfo = state.value.accountInfo.copy(
                                   accountSummary = state.value.accountInfo.accountSummary.copy(
                                       avatar = response.data
                                   )
                               )
                           ))
                       }
                   }
               }
               null to null
           }

           is UpdateBanner -> {
               viewModelScope.launch {
                   when (val response = updateAccountAggregator.updateBanner(event.bannerBase64)) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(
                                   accountInfo = state.value.accountInfo.copy(
                                       accountSummary = state.value.accountInfo.accountSummary.copy(
                                           banner = response.data
                                       )
                                   ),
                                   editProfileData = state.value.editProfileData.copy(
                                       banner = response.data
                                   )
                               )
                           }
                       }
                   }
               }
               null to null
           }

           is UpdateIntroduction -> {
               viewModelScope.launch {
                   val response = updateAccountAggregator.updateIntroduction(event.introduction)
                   when (response) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(
                                   accountInfo = state.value.accountInfo.copy(
                                       accountSummary = state.value.accountInfo.accountSummary.copy(
                                           introduction = event.introduction
                                       )
                                   ),
                                   editProfileData = state.value.editProfileData.copy(
                                       introduction = event.introduction
                                   )
                               )
                           }
                       }
                   }
               }
               null to null
           }

           is UpdateUsername -> {
               viewModelScope.launch {
                   when (val response = updateAccountAggregator.updateUsername(event.username)) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           internalState.update {
                               it.copy(
                                   accountInfo = state.value.accountInfo.copy(
                                       accountSummary = state.value.accountInfo.accountSummary.copy(
                                           username = event.username
                                       )
                                   ),
                                   editProfileData = state.value.editProfileData.copy(
                                       username = event.username
                                   )
                               )
                           }
                       }
                   }
               }
               null to null
           }

           is UpdatePassword -> {
               viewModelScope.launch {
                   val response = updateAccountAggregator.updatePassword(event.password, event.newPassword)
                   when (response) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEffect(ShowSnackbar("修改密码成功。"))
                       }
                   }
               }
               null to null
           }


           is SendUpdateEmailVCode -> {
               viewModelScope.launch {
                   when (val response = updateAccountAggregator.updateEmailSendVCode(event.email)) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEffect(ShowSnackbar("已发送验证码至${event.email}"))
                       }
                   }
               }
               null to null
           }

           is VerifyUpdateEmail -> {
               viewModelScope.launch {
                   val response = updateAccountAggregator.updateEmailVerifyEmail(event.code)
                   when (response) {
                       is UseCaseResult.Loading -> Unit
                       is UseCaseResult.Error -> {
                           sendEffect(ShowSnackbar(response.error))
                       }
                       is UseCaseResult.Success -> {
                           sendEffect(ShowSnackbar("邮箱验证成功。"))
                       }
                   }
               }
               null to null
           }

           is UpdateRoomHistory -> {
               state.value.copy(
                   roomHistory = event.roomHistory
               ) to null
           }

           is DeleteRoomHistory -> {
               viewModelScope.launch {
                   roomHistoryAggregator.deleteRoomHistory(event.roomHistory)
               }
               null to ShowSnackbar("删除该条上车记录。")
           }

           is CancelLoading -> {
               state.value.copy(isLoading = false) to ShowSnackbar("您取消了登录，建议登录以使用完整功能。")
           }
       }
    }
}

private const val TAG = "AccountViewModel"