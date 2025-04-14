package com.eynnzerr.bandoristation.feature.account

import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.business.account.LoginUseCase
import com.eynnzerr.bandoristation.business.datastore.GetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase.Params
import com.eynnzerr.bandoristation.feature.account.AccountEffect.*
import com.eynnzerr.bandoristation.feature.account.AccountIntent.*
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.LoginError
import com.eynnzerr.bandoristation.model.account.LoginParams
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class AccountViewModel(
    private val loginUseCase: LoginUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val stringPreferenceUseCase: GetPreferenceUseCase<String>,
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
                   isLoggedIn = true,
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
               viewModelScope.launch(Dispatchers.IO) {
                   val token = event.token
                   val userInfoResult = getUserInfoUseCase(token)
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
                       }
                   }
               }
               null to null
           }

           is Login ->{
               viewModelScope.launch(Dispatchers.IO) {
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
                                   // TODO 验证邮箱
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
       }
    }
}