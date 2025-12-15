package com.eynnzerr.bandoristation.data.remote

object NetworkUrl {
    const val AVATAR = "https://asset.bandoristation.com/images/user-avatar/"
    const val BANNER = "https://asset.bandoristation.com/images/user-banner/"
    const val WS_SERVER = "wss://api.bandoristation.com"
    const val HTTPS_SERVER = "https://server.bandoristation.com"
    const val API_SERVER = "https://api.bandoristation.com"

    const val ENCRYPTION_WS = "wss://eynnzerr.top/bandori/ws/connect"
    const val ENCRYPTION_SERVER = "https://eynnzerr.top/bandori/api/"
//    const val ENCRYPTION_WS = "ws://192.168.120.66:18080/bandori/ws/connect"
//    const val ENCRYPTION_SERVER = "http://192.168.120.66:18080/bandori/api/"
    const val REGISTER = "register"
    const val ROOM_UPLOAD = "room/upload"
    const val VERIFY_INVITE_CODE = "room/verify-invite-code"
    const val INVITE_CODE = "room/invite-code"
    const val ADD_WHITELIST = "whitelist/add"
    const val REMOVE_WHITELIST = "whitelist/remove"
    const val ADD_BLACKLIST = "blacklist/add"
    const val REMOVE_BLACKLIST = "blacklist/remove"
    const val GET_LISTS = "lists"
    const val CREATE_CHAT = "chat/create"
    const val JOIN_CHAT = "chat/join"
    const val GET_ALL_GROUPS = "chat/all_groups"
}