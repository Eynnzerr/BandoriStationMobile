package com.eynnzerr.bandoristation.model

import com.eynnzerr.bandoristation.utils.ResponseContentSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Https请求消息
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
    class InitializeAccountSetting() : ApiRequest(
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
    data class ObjectContent(val data: JsonObject) : ApiResponseContent()
}