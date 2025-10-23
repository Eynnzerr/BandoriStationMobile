package com.eynnzerr.bandoristation.usecase.encryption

import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.model.room.RoomAccessResponse

class EncryptionAggregator(
    private val listenRoomAccessRequest: ListenRoomAccessRequestUseCase,
    private val listenRoomAccessResponse: ListenRoomAccessResponseUseCase,
    private val registerEncryption: RegisterEncryptionUseCase,
    private val requestEncryptedRoomAccess: RequestEncryptedRoomAccessUseCase,
    private val respondToRoomAccess: RespondToRoomAccessUseCase,
    private val updateInviteCode: UpdateInviteCodeUseCase,
    private val uploadEncryptedRoom: UploadEncryptedRoomUseCase,
    private val verifyInviteCode: VerifyInviteCodeUseCase,
    private val addToWhitelistUseCase: AddToWhitelistUseCase,
    private val addToBlacklistUseCase: AddToBlacklistUseCase,
) {
    fun listenRoomAccessRequest() = listenRoomAccessRequest.invoke(Unit)

    fun listenRoomAccessResponse() = listenRoomAccessResponse.invoke(Unit)

    suspend fun registerEncryption() = registerEncryption.invoke(Unit)

    suspend fun requestEncryptedRoomAccess(request: RoomAccessRequest) = requestEncryptedRoomAccess.invoke(request)

    suspend fun respondToRoomAccess(response: RoomAccessResponse) = respondToRoomAccess.invoke(response)

    suspend fun updateInviteCode(code: String) = updateInviteCode.invoke(code)

    suspend fun uploadEncryptedRoom(roomNumber: String) = uploadEncryptedRoom.invoke(roomNumber)

    suspend fun verifyInviteCode(request: ApiRequest.VerifyInviteCodeRequest) = verifyInviteCode.invoke(request)

    suspend fun addToWhiteList(id: String) = addToWhitelistUseCase.invoke(id)

    suspend fun addToBlacklist(id: String) = addToBlacklistUseCase.invoke(id)
}