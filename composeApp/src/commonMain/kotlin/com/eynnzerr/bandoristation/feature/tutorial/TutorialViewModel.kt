package com.eynnzerr.bandoristation.feature.tutorial

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.usecase.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.usecase.SetUpClientUseCase
import com.eynnzerr.bandoristation.usecase.account.GetSelfInfoUseCase
import com.eynnzerr.bandoristation.usecase.account.LoginUseCase
import com.eynnzerr.bandoristation.usecase.account.LogoutUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordSendVCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordVerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.SendVerificationCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.SignupUseCase
import com.eynnzerr.bandoristation.usecase.account.VerifyEmailUseCase

class TutorialViewModel(
    private val setUpClientUseCase: SetUpClientUseCase,
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
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<TutorialState, Tuto> {
}