package com.eynnzerr.bandoristation.feature.account

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.AccountInfo
import com.eynnzerr.bandoristation.navigation.Screen

data class AccountState(
    val isTokenValid: Boolean = true,
    val isLoading: Boolean = true,
    val accountInfo: AccountInfo = AccountInfo(),
) : UIState {
    companion object {
        fun initial() = AccountState()
    }
}

sealed class AccountIntent: UIEvent {
    data class UpdateAccountInfo(val accountInfo: AccountInfo): AccountIntent()
    data class NotifyUpdateInfoFailed(val reason: String): AccountIntent()
}

sealed class AccountEffect: UIEffect {
    data class ShowSnackbar(val text: String): AccountEffect()
    data class NavigateToScreen(val destination: Screen): AccountEffect()
    data class CopyRoomNumber(val roomNumber: String): AccountEffect()
}