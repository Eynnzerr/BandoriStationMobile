package com.eynnzerr.bandoristation.feature.account

import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.feature.account.AccountEffect.*
import com.eynnzerr.bandoristation.model.AccountInfo
import com.eynnzerr.bandoristation.model.UseCaseResult


class AccountViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : BaseViewModel<AccountState, AccountIntent, AccountEffect>(
    initialState = AccountState.initial()
) {
    override suspend fun loadInitialData() {
        val userInfoResult = getUserInfoUseCase(Unit)
        when (userInfoResult) {
            is UseCaseResult.Loading -> Unit
            is UseCaseResult.Error -> {
                sendEvent(AccountIntent.NotifyUpdateInfoFailed(userInfoResult.error))
            }
            is UseCaseResult.Success -> {
                sendEvent(AccountIntent.UpdateAccountInfo(userInfoResult.data))
            }
        }
    }

    override fun reduce(event: AccountIntent): Pair<AccountState?, AccountEffect?> {
       return when (event) {
           is AccountIntent.UpdateAccountInfo -> {
               AccountState(
                   isTokenValid = true,
                   isLoading = false,
                   accountInfo = event.accountInfo
               ) to null
           }

           is AccountIntent.NotifyUpdateInfoFailed -> {
               AccountState(
                   isTokenValid = false,
                   isLoading = false,
                   accountInfo = AccountInfo(),
               ) to ShowSnackbar(event.reason)
           }
       }
    }
}