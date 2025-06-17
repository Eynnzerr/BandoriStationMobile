package com.eynnzerr.bandoristation.data.local

import com.eynnzerr.bandoristation.model.RoomHistory
import kotlinx.coroutines.flow.Flow
import kotlin.getValue

class LocalDataSourceImpl(private val database: AppDatabase) : LocalDataSource {
    private val roomHistoryDao by lazy {
        database.getRoomHistoryDao()
    }

    override fun fetchAllHistoryWithId(loginId: Long): Flow<List<RoomHistory>> = roomHistoryDao.fetchAllHistoryWithId(loginId)
    override fun fetchAllHistory(): Flow<List<RoomHistory>> = roomHistoryDao.fetchAllHistory()
    override suspend fun addToHistory(roomHistory: RoomHistory): Long = roomHistoryDao.addToHistory(roomHistory)
    override suspend fun editHistory(roomHistory: RoomHistory) = roomHistoryDao.updateHistory(roomHistory)
    override suspend fun deleteFromHistory(roomHistory: RoomHistory) = roomHistoryDao.deleteFromHistory(roomHistory)
    override suspend fun deleteFromHistory(roomHistories: List<RoomHistory>) = roomHistoryDao.deleteFromHistory(roomHistories)
}
