package com.eynnzerr.bandoristation.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceKeys {
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_run") // whether it's the first run of the app
    val IS_TOKEN_LOGIN = booleanPreferencesKey("is_token_login") // whether is logged in by token
    val SERVER_TIME = longPreferencesKey("server_time") // server timestamp when connected
    val USER_TOKEN = stringPreferencesKey("user_token") // cached user token
    val USER_ID = longPreferencesKey("user_id") // cached user id
    val USER_AVATAR = stringPreferencesKey("user_avatar") // cached user avatar
    val USER_NAME = stringPreferencesKey("user_name") // cached username
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
    val ENABLE_ENCRYPTION = booleanPreferencesKey("enable_encryption") // enable room number encryption
    val ENCRYPTION_INVITE_CODE = stringPreferencesKey("encryption_invite_code") // invite code required for viewing user's room number
    val ENCRYPTION_TOKEN = stringPreferencesKey("encryption_token") // token used to communicate with encryption server.
    val REGISTER_EXPIRE_TIME = longPreferencesKey("register_expire_time") // last timestamp of register encryption
    val CURRENT_CHAT_GROUP = stringPreferencesKey("current_chat_group") // current chat group ID, "" if not joined any group.
}