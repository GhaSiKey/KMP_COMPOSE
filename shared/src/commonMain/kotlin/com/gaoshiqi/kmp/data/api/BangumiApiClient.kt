package com.gaoshiqi.kmp.data.api

import com.gaoshiqi.kmp.BANGUMI_BEARER_TOKEN
import com.gaoshiqi.kmp.BANGUMI_NEXT_BASE_URL
import com.gaoshiqi.kmp.BANGUMI_USER_AGENT
import com.gaoshiqi.kmp.TRENDING_REQUEST_TIMEOUT_MS
import com.gaoshiqi.kmp.data.model.TrendingResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Bangumi Next API 客户端
 * 
 * 提供对 Bangumi Next API 的访问能力，处理认证、序列化和错误转换
 */
class BangumiApiClient {
    
    private val httpClient = HttpClient {
        // 配置 JSON 序列化
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        // 配置请求超时
        install(HttpTimeout) {
            requestTimeoutMillis = TRENDING_REQUEST_TIMEOUT_MS
            connectTimeoutMillis = TRENDING_REQUEST_TIMEOUT_MS
            socketTimeoutMillis = TRENDING_REQUEST_TIMEOUT_MS
        }
    }
    
    /**
     * 获取热门趋势番剧列表
     * 
     * @param type 条目类型（2=动画）
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return Result<TrendingResponse> 成功返回数据，失败返回异常
     */
    suspend fun getTrendingSubjects(
        type: Int = 2,
        offset: Int = 0,
        limit: Int = 10
    ): Result<TrendingResponse> {
        return try {
            val response = httpClient.get("${BangumiApiConfig.baseUrl}trending/subjects") {
                // Web 平台通过代理，不需要 User-Agent
                // 原生平台直接访问，添加 User-Agent
                if (BangumiApiConfig.baseUrl.contains("next.bgm.tv")) {
                    header(HttpHeaders.UserAgent, BANGUMI_USER_AGENT)
                }
                // 添加查询参数
                parameter("type", type)
                parameter("offset", offset)
                parameter("limit", limit)
            }.body<TrendingResponse>()
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 关闭 HTTP 客户端，释放资源
     */
    fun close() {
        httpClient.close()
    }
}
