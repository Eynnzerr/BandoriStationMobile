package com.eynnzerr.bandoristation.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 实际登录响应还有其他字段，但大多都跟GetUserInfo获取的信息重复，这里就不接收了
 * @param token 登录成功后返回的有效token或用于邮箱验证的临时token
 * @param userId 当前登录用户ID。-1表示当前登录需要验证邮箱
 */
@Serializable
data class LoginResult(
    @SerialName("token") val token: String = "",
    @SerialName("user_id") val userId: Long = -1,
)
