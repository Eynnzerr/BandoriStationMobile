package com.eynnzerr.bandoristation.business.account

class UpdateAccountAggregator(
    private val bindQQUseCase: BindQQUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val updateBannerUseCase: UpdateBannerUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val updateIntroductionUseCase: UpdateIntroductionUseCase,
) {
    suspend fun bindQQ(qq: Long) = bindQQUseCase.invoke(qq)

    suspend fun updateAvatar(avatar: String) = updateAvatarUseCase.invoke(avatar)

    suspend fun updateBanner(banner: String) = updateBannerUseCase.invoke(banner)

    suspend fun updateUsername(username: String) = updateUsernameUseCase.invoke(username)

    suspend fun updateIntroduction(introduction: String) = updateIntroductionUseCase.invoke(introduction)
}