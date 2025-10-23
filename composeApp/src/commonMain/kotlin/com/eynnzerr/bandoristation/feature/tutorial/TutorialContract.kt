package com.eynnzerr.bandoristation.feature.tutorial

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState

data class TutorialState(
    val accountInfo: AccountInfo = AccountInfo(),
    val isLoggedIn: Boolean = false,
    val countDown: Int = 0,
) : UIState {
    companion object {
        fun initial() = TutorialState()
    }
}

sealed class TutorialIntent: UIEvent {
    data class UpdateAccountInfo(val accountInfo: AccountInfo): TutorialIntent()
    data class GetUserInfo(val token: String): TutorialIntent()
    data class Login(val username: String, val password: String): TutorialIntent()
    class Logout(): TutorialIntent()
    data class Signup(
        val username: String,
        val password: String,
        val email: String,
    ) : TutorialIntent()
    class SendVerificationCode(): TutorialIntent()
    data class VerifyEmail(val code: String): TutorialIntent()
    data class ResetPasswordSendVCode(val email: String): TutorialIntent()
    data class ResetPasswordVerifyCode(val email: String, val code: String): TutorialIntent()
    data class ResetPassword(val password: String): TutorialIntent()
    data class UpdateCountDown(val value: Int): TutorialIntent()
}

sealed class TutorialEffect: UIEffect {
    data class ShowSnackbar(val text: String): TutorialEffect()
    data class NavigateToScreen(val destination: Screen): TutorialEffect()
    data class ControlLoginDialog(val visible: Boolean): TutorialEffect()
    data class ControlLoginDialogScreen(val destination: LoginScreenState): TutorialEffect()
    class PopBackLoginDialog(): TutorialEffect()
}