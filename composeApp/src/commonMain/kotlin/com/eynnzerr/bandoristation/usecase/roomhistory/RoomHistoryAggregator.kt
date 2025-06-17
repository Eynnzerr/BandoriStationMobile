package com.eynnzerr.bandoristation.usecase.roomhistory

import com.eynnzerr.bandoristation.model.RoomHistory

class RoomHistoryAggregator(
    private val fetchAllHistory: FetchAllHistoryUseCase,
    private val addRoomHistory: AddRoomHistoryUseCase,
    private val editRoomHistory: EditRoomHistoryUseCase,
    private val deleteRoomHistory: DeleteRoomHistoryUseCase,
    private val deleteRoomHistories: DeleteRoomHistoriesUseCase,
) {
    fun fetchAllHistory(loginId: Long?) = fetchAllHistory.execute(loginId)
    suspend fun addRoomHistory(roomHistory: RoomHistory) = addRoomHistory.invoke(roomHistory)
    suspend fun editRoomHistory(roomHistory: RoomHistory) = editRoomHistory.invoke(roomHistory)
    suspend fun deleteRoomHistory(roomHistory: RoomHistory) = deleteRoomHistory.invoke(roomHistory)
    suspend fun deleteRoomHistories(roomHistories: List<RoomHistory>) = deleteRoomHistories.invoke(roomHistories)
}
