package com.eynnzerr.bandoristation.model.account

sealed class LoginError {
    data class NeedVerification(val token: String) : LoginError()
    data class Other(val text: String) : LoginError()
}