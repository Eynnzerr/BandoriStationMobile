package com.eynnzerr.bandoristation.business.account

import com.eynnzerr.bandoristation.model.ApiRequest

class UpdateAccountAggregator(
    private val bindQQUseCase: BindQQUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val updateBannerUseCase: UpdateBannerUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val updateIntroductionUseCase: UpdateIntroductionUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val updateEmailSendVCodeUseCase: UpdateEmailSendVCodeUseCase,
    private val updateEmailVerifyEmailUseCase: UpdateEmailVerifyEmailUseCase,
) {
    suspend fun bindQQ(qq: Long) = bindQQUseCase.invoke(qq)

    suspend fun updateAvatar(avatar: String) = updateAvatarUseCase.invoke(avatar)

    suspend fun updateBanner(banner: String) = updateBannerUseCase.invoke(banner)

    suspend fun updateUsername(username: String) = updateUsernameUseCase.invoke(username)

    suspend fun updateIntroduction(introduction: String) = updateIntroductionUseCase.invoke(introduction)

    suspend fun updatePassword(password: String, newPassword: String) = updatePasswordUseCase.invoke(
        ApiRequest.UpdatePassword(
            password = password,
            newPassword = newPassword
        )
    )

    suspend fun updateEmailSendVCode(email: String) = updateEmailSendVCodeUseCase.invoke(email)

    suspend fun updateEmailVerifyEmail(code: String) = updateEmailVerifyEmailUseCase.invoke(code)
}