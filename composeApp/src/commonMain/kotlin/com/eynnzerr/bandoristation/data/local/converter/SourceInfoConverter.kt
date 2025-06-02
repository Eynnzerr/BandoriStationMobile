package com.eynnzerr.bandoristation.data.local.converter

import androidx.room.TypeConverter
import com.eynnzerr.bandoristation.model.SourceInfo
import kotlinx.serialization.json.Json

class SourceInfoConverter {
    @TypeConverter
    fun fromSourceInfo(sourceInfo: SourceInfo) = Json.encodeToString(sourceInfo)

    @TypeConverter
    fun toSourceInfo(sourceInfo: String) = Json.decodeFromString<SourceInfo>(sourceInfo)

}