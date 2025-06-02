package com.eynnzerr.bandoristation.data.local.converter

import androidx.room.TypeConverter
import com.eynnzerr.bandoristation.model.UserInfo
import kotlinx.serialization.json.Json

class UserInfoConverter {
    @TypeConverter
    fun fromSourceInfo(userInfo: UserInfo) = Json.encodeToString(userInfo)

    @TypeConverter
    fun toSourceInfo(userInfo: String) = Json.decodeFromString<UserInfo>(userInfo)
}