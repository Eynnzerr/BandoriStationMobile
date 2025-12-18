package com.eynnzerr.bandoristation.data.remote.websocket

object EncryptionSocketActions {
    // 车牌加密相关
    const val REQUEST_ACCESS = "request_access" // app -> server 用户发起车牌查看请求时调用
    const val RESPOND_ACCESS = "respond_access" // app -> server 房主收到查看请求且批准/拒绝后调用
    const val ACCESS_REQUEST_RECEIVED = "access_request_received" // server -> app 向房主发送某用户的查看请求
    const val ACCESS_RESULT = "access_result" // server -> app 向用户发送房主批复结果
    const val ERROR = "error" // server -> app 调用错误

    // 车内聊天相关
    const val SEND_CHAT_MESSAGE = "send_chat_message" // app -> server 用户发送消息到聊天群
    const val NEW_CHAT_MESSAGE = "new_chat_message" // server -> app 用户接收聊天群新消息
    const val USER_JOINED_CHAT = "user_joined_chat" // server -> app 用户接收新用户加入通知
    const val USER_LEFT_CHAT = "user_left_chat" // server -> app 用户接收其他用户离开通知
    const val USER_REMOVED_FROM_CHAT = "user_removed_from_chat" // server -> app 用户接收其他用户被移出通知
    const val CHAT_DISBANDED = "chat_disbanded" // server -> app 用户接收聊天室解散通知
    const val NEW_OWNER_ASSIGNED = "new_owner_assigned" // server -> app 用户被指派为新房主
    const val OWNER_CHANGED = "owner_changed" // server -> app 房主变更通知
    const val CHAT_STATE_SYNC = "chat_state_sync" // server -> app 用户重连后同步当前聊天室状态
    const val CHAT_GROUP_CHANGE = "new_chat_group" // server -> app 有聊天室状态发生改变时推送
}