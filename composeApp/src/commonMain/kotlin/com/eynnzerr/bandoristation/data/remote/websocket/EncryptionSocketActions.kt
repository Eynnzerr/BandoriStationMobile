package com.eynnzerr.bandoristation.data.remote.websocket

object EncryptionSocketActions {
    const val REQUEST_ACCESS = "request_access" // app -> server 用户发起车牌查看请求时调用
    const val RESPOND_ACCESS = "respond_access" // app -> server 房主收到查看请求且批准/拒绝后调用
    const val ACCESS_REQUEST_RECEIVED = "access_request_received" // server -> app 向房主发送某用户的查看请求
    const val ACCESS_RESULT = "access_result" // server -> app 向用户发送房主批复结果
    const val ERROR = "error" // server -> app 调用错误
}