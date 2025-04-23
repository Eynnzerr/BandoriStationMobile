package com.eynnzerr.bandoristation.preferences

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceKeys {
    val USER_TOKEN = stringPreferencesKey("user_token") // cached user token
    val USER_ID = stringPreferencesKey("user_id") // cached user id
    val PRESET_WORDS = stringSetPreferencesKey("preset_words") // preset words for uploading
    val TEMP_TOKEN = stringPreferencesKey("temp_token") // temporary token used for signup
    val BAND_THEME = stringPreferencesKey("default")
    val FOLLOWING_LIST = stringSetPreferencesKey("following_list") // list of following users
}