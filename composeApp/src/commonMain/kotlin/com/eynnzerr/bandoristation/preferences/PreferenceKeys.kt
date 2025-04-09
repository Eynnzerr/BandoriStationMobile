package com.eynnzerr.bandoristation.preferences

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val USER_TOKEN = stringPreferencesKey("user_token") // cached user token
    val USER_ID = stringPreferencesKey("user_id") // cached user id
}