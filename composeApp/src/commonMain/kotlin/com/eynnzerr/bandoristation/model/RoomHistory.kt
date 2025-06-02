package com.eynnzerr.bandoristation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.eynnzerr.bandoristation.data.local.converter.SourceInfoConverter
import com.eynnzerr.bandoristation.data.local.converter.UserInfoConverter
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "room_history")
@TypeConverters(SourceInfoConverter::class, UserInfoConverter::class)
data class RoomHistory(
    @PrimaryKey(autoGenerate = true)
    val historyId: Long = 0,
    @ColumnInfo(defaultValue = "")
    val number: String = "",
    @ColumnInfo(defaultValue = "")
    val rawMessage: String = "",
    @ColumnInfo(defaultValue = "")
    val sourceInfo: SourceInfo = SourceInfo(),
    @ColumnInfo(defaultValue = "")
    val type: String = "",
    @ColumnInfo(defaultValue = "0")
    val time: Long = 0,
    @ColumnInfo(defaultValue = "")
    val userInfo: UserInfo = UserInfo(),
    @ColumnInfo(defaultValue = "0")
    val loginId: Long = 0,
    @ColumnInfo(defaultValue = "-1")
    val duration: Long = -1,
)
