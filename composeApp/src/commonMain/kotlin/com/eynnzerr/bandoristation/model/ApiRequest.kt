package com.eynnzerr.bandoristation.model

import com.eynnzerr.bandoristation.model.room.RoomFilter
import com.eynnzerr.bandoristation.utils.ResponseContentSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Https请求消息，API/HTTPS通用
 * @param group 请求方法组
 * @param function 请求方法名
 * 不同请求方法可能需要额外参数，通过新类继承ApiRequest自己添加字段携带
 */
@Serializable
sealed class ApiRequest(
    @SerialName("function_group") val group: String,
    @SerialName("function") val function: String,
) {
    @Serializable
    class InitializeAccountSetting : ApiRequest(
        group = "MainAction",
        function = "initializeAccountSetting"
    )

    @Serializable
    data class GetUserInfo(
        @SerialName("user_id") val id: Long
    ) : ApiRequest(
        group = "MainAction",
        function = "getUserInfo"
    )

    @Serializable
    data class Login(
        val username: String,
        val password: String,
    ) : ApiRequest(
        group = "UserLogin",
        function = "login",
    )

    @Serializable
    class Logout : ApiRequest(
        group = "UserLogin",
        function = "logout",
    )

    @Serializable
    data class Signup(
        val username: String,
        val password: String,
        val email: String,
    ) : ApiRequest(
        group = "UserLogin",
        function = "signup",
    )

    @Serializable
    class SendEmailVerificationCode : ApiRequest(
        group = "UserLogin",
        function = "sendEmailVerificationCode",
    )

    @Serializable
    data class VerifyEmail(
        @SerialName("verification_code") val code: String,
    ) : ApiRequest(
        group = "UserLogin",
        function = "verifyEmail",
    )

    @Serializable
    data class FollowUser(
        @SerialName("user_id") val id: Long,
        @SerialName("is_following") val follow: Boolean,
    ) : ApiRequest(
        group = "MainAction",
        function = "followUser",
    )

    @Serializable
    data class GetFollowerList(
        @SerialName("user_id") val id: Long,
    ) : ApiRequest(
        group = "MainAction",
        function = "getFollowerList",
    )

    @Serializable
    data class GetUserBriefInfo(
        @SerialName("user_id") val ids: List<Long>,
    ) : ApiRequest(
        group = "",
        function = "getUserBriefInfo",
    )

    @Serializable
    data class InformUser(
        @SerialName("type") val type: String,
        @SerialName("user_id") val userId: Long,
        @SerialName("raw_message") val rawMessage: String,
        @SerialName("reason") val reason: String,
    ) : ApiRequest(
        group = "MainAction",
        function = "informUser",
    )

    @Serializable
    class GetRoomFilter : ApiRequest(
        group = "MainAction",
        function = "getRoomNumberFilter",
    )

    @Serializable
    data class UpdateRoomFilter(
        @SerialName("room_number_filter")
        val filter: RoomFilter,
    ) : ApiRequest(
        group = "MainAction",
        function = "updateRoomNumberFilter",
    )

    @Serializable
    class GetInitialData : ApiRequest(
        group = "AccountManage",
        function = "getInitialData",
    )

    @Serializable
    data class UpdateImage(
        val image: String,
        val type: String, // avatar or banner
    ) : ApiRequest(
        group = "AccountManage",
        function = "updateImage",
    )

    @Serializable
    data class UpdateUserName(
        val username: String,
    ) : ApiRequest(
        group = "AccountManage",
        function = "updateUserName",
    )

    @Serializable
    data class UpdateIntroduction(
        val introduction: String,
    ) : ApiRequest(
        group = "AccountManage",
        function = "updateIntroduction",
    )

    @Serializable
    data class UpdatePassword(
        @SerialName("new_password")
        val newPassword: String,
        val password: String,
    ) : ApiRequest(
        group = "AccountManage",
        function = "updatePassword",
    )

    @Serializable
    data class UpdateEmailSendVCode(
        val email: String,
    ) : ApiRequest(
        group = "AccountManage",
        function = "updateEmailSendVerificationCode"
    )

    @Serializable
    data class UpdateEmailVerifyEmail(
        @SerialName("verification_code")
        val code: String,
    ) : ApiRequest(
        group = "AccountManage",
        function = "updateEmailVerifyEmail"
    )

    @Serializable
    data class BindQQ(
        val qq: Long,
    ) : ApiRequest(
        group = "AccountManage",
        function = "bindQQ",
    )

    @Serializable
    data class ResetPasswordSendVCode(
        val email: String,
    ) : ApiRequest(
        group = "UserLogin",
        function = "resetPasswordSendEmailVerificationCode",
    )

    @Serializable
    data class ResetPasswordVerifyEmail(
        val email: String,
        @SerialName("verification_code") val code: String,
    ) : ApiRequest(
        group = "UserLogin",
        function = "resetPasswordVerifyEmail",
    )

    @Serializable
    data class ResetPassword(
        val password: String,
    ) : ApiRequest(
        group = "UserLogin",
        function = "resetPassword",
    )

    @Serializable
    data class RegisterEncryptionRequest(
        val userId: String,
        val originalToken: String
    ) : ApiRequest(
        group = "User",
        function = "register"
    )

    @Serializable
    data class UpdateInviteCodeRequest(
        val inviteCode: String
    ) : ApiRequest(
        group = "User",
        function = "updateInviteCode"
    )

    @Serializable
    data class VerifyInviteCodeRequest(
        val targetUserId: String,
        val inviteCode: String
    ) : ApiRequest(
        group = "Room",
        function = "verifyInviteCodeRequest"
    )

    @Serializable
    data class UploadEncryptedRoom(
        val roomNumber: String,
    ) : ApiRequest(
        group = "Room",
        function = "uploadRoom"
    )

    @Serializable
    data class BlacklistRequest(
        val blockedUserId: String
    ) : ApiRequest(
        group = "Room",
        function = "blacklist"
    )

    @Serializable
    data class WhitelistRequest(
        val allowedUserId: String
    ) : ApiRequest(
        group = "Room",
        function = "whitelist"
    )

    @Serializable
    data class CreateChatRequest(
        val roomName: String,
        val ownerName: String,
        val ownerAvatar: String,
    ) : ApiRequest(
        group = "Chat",
        function = "create"
    )

    @Serializable
    data class JoinChatRequest(
        val ownerId: String,
        val username: String,
        val avatar: String,
    ) : ApiRequest(
        group = "Chat",
        function = "join"
    )

    @Serializable
    class Empty : ApiRequest(
        group = "",
        function = ""
    )
}

/**
 * Https响应消息
 * @param status string "success"/"failure
 * @param response string/object 响应数据
 */
@Serializable
data class ApiResponse(
    val status: String,
    @Serializable(with = ResponseContentSerializer::class)
    val response: ApiResponseContent
) {
    inline fun <T> handle(
        onSuccess: (ApiResponseContent) -> T,
        onFailure: (String) -> T,
    ) : T = when (status) {
        "success" -> onSuccess(response)
        "failure" -> {
            val errorMessage = when (response) {
                is ApiResponseContent.ObjectContent -> response.data.toString()
                is ApiResponseContent.StringContent -> response.text
            }
            onFailure(errorMessage)
        }
        else -> onFailure("Unknown response status: $status")
    }
}

@Serializable
sealed class ApiResponseContent {
    @Serializable
    data class StringContent(val text: String) : ApiResponseContent()

    @Serializable
    data class ObjectContent(val data: JsonElement) : ApiResponseContent()
}