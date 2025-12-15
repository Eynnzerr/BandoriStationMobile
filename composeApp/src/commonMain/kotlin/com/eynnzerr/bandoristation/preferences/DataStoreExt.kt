package com.eynnzerr.bandoristation.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

suspend fun DataStore<Preferences>.readToken() = data.first()[PreferenceKeys.USER_TOKEN] ?: ""

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

suspend fun DataStore<Preferences>.readCurrentChatGroupId() = data.first()[PreferenceKeys.CURRENT_CHAT_GROUP] ?: ""

suspend fun DataStore<Preferences>.writeCurrentChatGroupId(id: String) {
    edit { p ->
        p[PreferenceKeys.CURRENT_CHAT_GROUP] = id
    }
}