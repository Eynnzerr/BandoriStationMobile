package com.eynnzerr.bandoristation.business.account

import com.eynnzerr.bandoristation.data.AppRepository
import kotlinx.coroutines.CoroutineDispatcher

class SignupUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
) {

}