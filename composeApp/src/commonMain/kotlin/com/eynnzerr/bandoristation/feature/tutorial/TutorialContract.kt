package com.eynnzerr.bandoristation.feature.tutorial

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState

data class TutorialState(
    val avatar: String = "",
    val username: String = "未登录",
    val isLoggedIn: Boolean = false,
    val countDown: Int = 0,
    val isStationConnected: Boolean = false,
    val isEncryptionConnected: Boolean = false,
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
    class RegisterEncryption: TutorialIntent()
}

sealed class TutorialEffect: UIEffect {
    data class ShowSnackbar(val text: String): TutorialEffect()
    class NavigateToHome: TutorialEffect()
    data class ControlLoginDialog(val visible: Boolean): TutorialEffect()
    data class ControlLoginDialogScreen(val destination: LoginScreenState): TutorialEffect()
    data class ControlHelpDialog(val visible: Boolean): TutorialEffect()
    class PopBackLoginDialog(): TutorialEffect()
}