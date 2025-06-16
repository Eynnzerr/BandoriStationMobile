package com.eynnzerr.bandoristation.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceKeys {
    val IS_FIRST_RUN = booleanPreferencesKey("is_first_run") // whether it's the first run of the app
    val IS_TOKEN_LOGIN = booleanPreferencesKey("is_token_login") // whether is logged in by token
    val IS_TOKEN_VALID = booleanPreferencesKey("is_token_valid") // whether token is valid
    val SERVER_TIME = longPreferencesKey("server_time") // server timestamp when connected
    val USER_TOKEN = stringPreferencesKey("user_token") // cached user token
    val USER_ID = longPreferencesKey("user_id") // cached user id
    val PRESET_WORDS = stringSetPreferencesKey("preset_words") // preset words for uploading
    val TEMP_TOKEN = stringPreferencesKey("temp_token") // temporary token used for signup
    val BAND_THEME = stringPreferencesKey("default") // app theme choice
    val FOLLOWING_LIST = stringSetPreferencesKey("following_list") // list of following users
    val FILTER_WORDS = stringSetPreferencesKey("filter_words") // list of filter words
    val FILTER_USERS = stringPreferencesKey("filter_users") // serialized list of filter users
    val FILTER_PJSK = booleanPreferencesKey("filter_pjsk") // whether to filter non-bandori rooms
    val CLEAR_OUTDATED_ROOM = booleanPreferencesKey("clear_outdated_room") // whether to auto clear outdated tooms
    val SHOW_PLAER_BRIEF = booleanPreferencesKey("show_player_brief") // whether to display player brief info in room cards
    val RECORD_ROOM_HISTORY = booleanPreferencesKey("record_room_history") // whether to record room join history
    val AUTO_UPLOAD_INTERVAL = longPreferencesKey("auto_upload_interval") // interval(seconds) for auto uploading room info
}