package com.eynnzerr.bandoristation.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

suspend fun DataStore<Preferences>.readToken() = data.first()[PreferenceKeys.USER_TOKEN] ?: ""

suspend fun DataStore<Preferences>.readUserIdAsString() = data.first()[PreferenceKeys.USER_ID]?.toString() ?: ""

suspend fun DataStore<Preferences>.readBasicUserInfo(): Triple<String, String, String> {
    return with(data.first()) {
        Triple(
            first = this[PreferenceKeys.USER_ID]?.toString() ?: "",
            second = this[PreferenceKeys.USER_NAME] ?: "",
            third = this[PreferenceKeys.USER_AVATAR] ?: "",
        )
    }
}

suspend fun DataStore<Preferences>.readEncryptionToken() = data.first()[PreferenceKeys.ENCRYPTION_TOKEN] ?: ""

suspend fun DataStore<Preferences>.writeEncryptionToken(token: String) {
    edit { p ->
        p[PreferenceKeys.ENCRYPTION_TOKEN] = token
    }
}

suspend fun DataStore<Preferences>.readIfEncryptionEnabled(): Boolean {
    val now = Clock.System.now().toEpochMilliseconds()
    val (expireTimestamp, token) = with(data.first()) {
        (this[PreferenceKeys.REGISTER_EXPIRE_TIME] ?: now) to this[PreferenceKeys.ENCRYPTION_TOKEN]
    }

    val encryptionValidDays = (expireTimestamp - now).milliseconds.inWholeDays
    return token != null && encryptionValidDays > 0
}