package com.eynnzerr.bandoristation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eynnzerr.bandoristation.model.RoomHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomHistoryDao {

    @Query("SELECT * FROM room_history WHERE loginId = :loginId")
    fun fetchAllHistoryWithId(loginId: Long): Flow<List<RoomHistory>>

    @Query("SELECT * FROM room_history")
    fun fetchAllHistory(): Flow<List<RoomHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(roomHistory: RoomHistory) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHistory(roomHistory: RoomHistory)

    @Delete
    suspend fun deleteFromHistory(roomHistory: RoomHistory)

    @Delete
    suspend fun deleteFromHistory(roomHistories: List<RoomHistory>)
}
