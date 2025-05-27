package com.eynnzerr.bandoristation.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceKeys {
    val SERVER_TIME = longPreferencesKey("server_time") // server timestamp when connected
    val USER_TOKEN = stringPreferencesKey("user_token") // cached user token
    val PRESET_WORDS = stringSetPreferencesKey("preset_words") // preset words for uploading
    val TEMP_TOKEN = stringPreferencesKey("temp_token") // temporary token used for signup
    val BAND_THEME = stringPreferencesKey("default") // app theme choice
    val FOLLOWING_LIST = stringSetPreferencesKey("following_list") // list of following users
    val FILTER_WORDS = stringSetPreferencesKey("filter_words") // list of filter words
    val FILTER_USERS = stringPreferencesKey("filter_users") // serialized list of filter users
    val FILTER_PJSK = booleanPreferencesKey("filter_pjsk") // whether to filter non-bandori rooms
}