package com.eynnzerr.bandoristation.model.room

import kotlinx.serialization.Serializable

/**
 * 车牌查看请求数据体，用于请求者的上传参数和发给房主的响应
 * @param requestId 请求的唯一标识符，由app端通过UUID生成
 * @param targetUserId 待查看车牌的房主车站ID
 * @param requesterId 发起查看的用户车站ID
 * @param requesterName 发起查看的用户名称
 * @param requesterAvatar 发起查看的用户头像
 */
@Serializable
data class RoomAccessRequest(
    val requestId: String,
    val targetUserId: String,
    val requesterId: String,
    val requesterName: String,
    val requesterAvatar: String,
)

/**
 * 车牌查看回复数据体，用于房主的上传参数和发给请求者的响应
 * @param requestId 请求的唯一标识符，上传时填写原请求的id
 * @param approved 是否同意查看车牌
 * @param roomNumber 真实车牌号，上传时不携带或填写空
 * @param message 其它消息（如拒绝提示），上传时不携带或填写空
 */
@Serializable
data class RoomAccessResponse(
    val requestId: String,
    val approved: Boolean,
    val roomNumber: String? = null,
    val message: String? = null,
)