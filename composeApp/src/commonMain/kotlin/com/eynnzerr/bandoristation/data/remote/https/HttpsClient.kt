package com.eynnzerr.bandoristation.data.remote.https

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.ApiResponse
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.AppLogger
import com.eynnzerr.bandoristation.utils.testToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HttpsClient(
    private val apiUrl: String,
    private val client: HttpClient, // Injected
    // private val dataStore: DataStore<Preferences>, // Injected
) {
    /**
     * 发送不需要认证的API请求
     * @param request 请求对象
     * @return 响应对象
     */
    suspend fun sendRequest(request: ApiRequest): ApiResponse {
        val response: HttpResponse = client.post(apiUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        AppLogger.d(TAG, "response status: ${response.status.value}; raw body: ${response.bodyAsText()}")
        return response.body()
    }

    /**
     * 发送需要认证的API请求
     * @param request 请求对象
     * @return 响应对象
     */
    suspend fun sendAuthenticatedRequest(request: ApiRequest, token: String): ApiResponse {
        // val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: testToken }.first()
        AppLogger.d(TAG, "use token: $token")

        val response: HttpResponse = client.post(apiUrl) {
            header("Auth-Token", token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        AppLogger.d(TAG, "response status: ${response.status.value}; raw body: ${response.bodyAsText()}")
        return response.body()
    }
}

private const val TAG = "HttpsClient"