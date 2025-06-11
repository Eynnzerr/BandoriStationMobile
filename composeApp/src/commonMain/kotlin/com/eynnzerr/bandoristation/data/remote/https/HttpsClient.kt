package com.eynnzerr.bandoristation.data.remote.https

import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.ApiResponse
import com.eynnzerr.bandoristation.model.ApiResponseContent
import com.eynnzerr.bandoristation.utils.AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import com.eynnzerr.bandoristation.model.GithubRelease
import com.eynnzerr.bandoristation.utils.isNetworkAvailable

class HttpsClient(
    private val httpsUrl: String,
    private val apiUrl: String,
    private val client: HttpClient, // Injected
    private val json: Json,
    // private val dataStore: DataStore<Preferences>, // Injected
) {
    /**
     * 发送不需要认证的API请求
     * @param request 请求对象
     * @return 响应对象
     */
    suspend fun sendRequest(request: ApiRequest): ApiResponse {
        if (!isNetworkAvailable()) {
            return ApiResponse(
                status = "failure",
                response = ApiResponseContent.StringContent("No Internet")
            )
        }
        AppLogger.d(TAG, "Send https request: ${json.encodeToString(request)}.")

        val response: HttpResponse = client.post(httpsUrl) {
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
        if (!isNetworkAvailable()) {
            return ApiResponse(
                status = "failure",
                response = ApiResponseContent.StringContent("No Internet")
            )
        }
        // val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: testToken }.first()
        AppLogger.d(TAG, "Send https request: ${json.encodeToString(request)}; using token: $token.")

        val response: HttpResponse = client.post(httpsUrl) {
            header("Auth-Token", token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        AppLogger.d(TAG, "response status: ${response.status.value}; raw body: ${response.bodyAsText()}")
        return response.body()
    }

    suspend fun sendApiRequest(request: ApiRequest): ApiResponse {
        if (!isNetworkAvailable()) {
            return ApiResponse(
                status = "failure",
                response = ApiResponseContent.StringContent("No Internet")
            )
        }
        AppLogger.d(TAG, "Send https request: ${json.encodeToString(request)}.")

        val response: HttpResponse = client.post(apiUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        AppLogger.d(TAG, "response status: ${response.status.value}; raw body: ${response.bodyAsText()}")
        try {
            val apiResponse: ApiResponse = response.body()
            return apiResponse
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResponse(
                status = "failure",
                response = ApiResponseContent.StringContent("ktor client json parse error")
            )
        }
    }

    suspend fun fetchLatestRelease(owner: String, repo: String): GithubRelease {
        if (!isNetworkAvailable()) {
            return GithubRelease()
        }
        val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
        val response: HttpResponse = client.get(url)
        AppLogger.d(TAG, "fetchLatestRelease response: ${response.bodyAsText()}")
        return response.body()
    }
}

private const val TAG = "HttpsClient"