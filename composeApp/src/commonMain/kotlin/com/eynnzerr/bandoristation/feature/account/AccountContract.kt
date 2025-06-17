package com.eynnzerr.bandoristation.feature.account

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.room.RoomHistory
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.account.AccountSummary
import com.eynnzerr.bandoristation.model.account.EditProfileInfo
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState

data class AccountState(
    val isLoading: Boolean = true,
    val accountInfo: AccountInfo = AccountInfo(),
    val isLoggedIn: Boolean = false,
    val countDown: Int = 0,
    val followers: List<AccountSummary> = emptyList(),
    val followings: List<AccountSummary> = emptyList(),
    val editProfileData: EditProfileInfo = EditProfileInfo(),
    val roomHistory: List<RoomHistory> = emptyList(),
) : UIState {
    companion object {
        fun initial() = AccountState()
    }
}

sealed class AccountIntent: UIEvent {
    data class UpdateAccountInfo(val accountInfo: AccountInfo): AccountIntent()
    data class NotifyUpdateInfoFailed(val reason: String): AccountIntent()
    data class GetUserInfo(val token: String): AccountIntent()
    data class Login(val username: String, val password: String): AccountIntent()
    class CancelLoading(): AccountIntent()
    class Logout(): AccountIntent()
    data class Signup(
        val username: String,
        val password: String,
        val email: String,
    ) : AccountIntent()
    class SendVerificationCode(): AccountIntent()
    data class VerifyEmail(val code: String): AccountIntent()
    data class ResetPasswordSendVCode(val email: String): AccountIntent()
    data class ResetPasswordVerifyCode(val email: String, val code: String): AccountIntent()
    data class ResetPassword(val password: String): AccountIntent()
    data class UpdateCountDown(val value: Int): AccountIntent()
    class GetFollowers : AccountIntent()
    class GetFollowings : AccountIntent()
    class GetEditProfileData : AccountIntent()
    data class FollowUser(val id: Long) : AccountIntent()
    data class UpdateAvatar(val avatarBase64: String) : AccountIntent()
    data class UpdateBanner(val bannerBase64: String) : AccountIntent()
    data class UpdateUsername(val username: String) : AccountIntent()
    data class UpdateIntroduction(val introduction: String) : AccountIntent()
    data class UpdatePassword(val password: String, val newPassword: String) : AccountIntent()
    data class BindQQ(val qq: Long) : AccountIntent()
    data class SendUpdateEmailVCode(val email: String): AccountIntent()
    data class VerifyUpdateEmail(val code: String): AccountIntent()
    data class UpdateRoomHistory(val roomHistory: List<RoomHistory>) : AccountIntent()
    data class DeleteRoomHistory(val roomHistory: RoomHistory) : AccountIntent()
}

sealed class AccountEffect: UIEffect {
    data class ShowSnackbar(val text: String): AccountEffect()
    data class NavigateToScreen(val destination: Screen): AccountEffect()
    data class CopyRoomNumber(val roomNumber: String): AccountEffect()
    data class ControlFollowerDialog(val visible: Boolean): AccountEffect()
    data class ControlFollowingDialog(val visible: Boolean): AccountEffect()
    data class ControlLoginDialog(val visible: Boolean): AccountEffect()
    data class ControlEditProfileDialog(val visible: Boolean): AccountEffect()
    data class ControlDrawer(val visible: Boolean): AccountEffect()
    data class ControlLoginDialogScreen(val destination: LoginScreenState): AccountEffect()
    class PopBackLoginDialog(): AccountEffect()
}