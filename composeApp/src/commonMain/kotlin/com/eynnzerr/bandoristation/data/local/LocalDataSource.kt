package com.eynnzerr.bandoristation.data.local

import com.eynnzerr.bandoristation.model.RoomHistory
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun fetchAllHistoryWithId(loginId: Long): Flow<List<RoomHistory>>
    fun fetchAllHistory(): Flow<List<RoomHistory>>
    suspend fun addToHistory(roomHistory: RoomHistory): Long
    suspend fun editHistory(roomHistory: RoomHistory)
    suspend fun deleteFromHistory(roomHistory: RoomHistory)
    suspend fun deleteFromHistory(roomHistories: List<RoomHistory>)
}
