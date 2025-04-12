package com.eynnzerr.bandoristation.model

import kotlinx.serialization.Serializable

/**
 * WebSocket请求消息
 * TODO 用密封类会更好
 * @param action 操作名称
 * @param data 请求数据，可以是任意类型或null
 */
@Serializable
data class WebSocketRequest<T>(
    val action: String,
    val data: T? = null
)

/**
 * WebSocket响应消息
 * @param status 响应状态："success"或"failure"
 * @param action 操作名称
 * @param response 响应数据，可以是任意类型
 */
@Serializable
data class WebSocketResponse<T>(
    val status: String,
    val action: String,
    val response: T
)

/**
 * 新版Websocket Request
 */
sealed class WSRequest(
    val action: String = "",
)

/**
 * 新版Websocket Response
 */
sealed class WSResponse(
    val status: String = "success",
    val action: String = "",
)