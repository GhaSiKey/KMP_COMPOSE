package com.gaoshiqi.kmp.data.repository

import com.gaoshiqi.kmp.data.api.BangumiApiClient
import com.gaoshiqi.kmp.data.model.TrendingResponse
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException

/**
 * 热门番剧数据仓库
 * 
 * 提供业务层友好的数据访问接口，处理错误转换和数据映射
 */
class TrendingRepository(
    private val apiClient: BangumiApiClient = BangumiApiClient()
) {
    
    /**
     * 获取热门番剧列表
     * 
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return Result<TrendingResponse> 成功返回数据，失败返回业务异常
     */
    suspend fun getTrendingSubjects(
        offset: Int = 0,
        limit: Int = 10
    ): Result<TrendingResponse> {
        return try {
            val result = apiClient.getTrendingSubjects(type = 2, offset = offset, limit = limit)
            
            result.fold(
                onSuccess = { response ->
                    Result.success(response)
                },
                onFailure = { exception ->
                    Result.failure(convertToUserFriendlyException(exception))
                }
            )
        } catch (e: Exception) {
            Result.failure(convertToUserFriendlyException(e))
        }
    }
    
    /**
     * 将异常转换为用户友好的错误消息
     * 
     * @param exception 原始异常
     * @return 包含用户友好消息的异常
     */
    private fun convertToUserFriendlyException(exception: Throwable): Exception {
        val message = when (exception) {
            // 网络连接错误
            is IOException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> "网络连接失败，请检查网络设置"
            
            // 请求超时
            is HttpRequestTimeoutException -> "请求超时，请稍后重试"
            
            // 序列化错误
            is SerializationException -> "数据解析失败，请稍后重试"
            
            // 其他错误
            else -> {
                // 检查是否是 HTTP 错误（通过异常消息判断）
                val exceptionMessage = exception.message ?: ""
                when {
                    exceptionMessage.contains("401") -> "认证失败，请重新登录"
                    exceptionMessage.contains("500") -> "服务器错误，请稍后重试"
                    exceptionMessage.contains("HTTP") -> {
                        // 尝试提取状态码
                        val statusCode = extractHttpStatusCode(exceptionMessage)
                        if (statusCode != null) {
                            "请求失败（状态码：$statusCode）"
                        } else {
                            "请求失败，请稍后重试"
                        }
                    }
                    else -> "未知错误：${exception.message}"
                }
            }
        }
        
        return Exception(message, exception)
    }
    
    /**
     * 从异常消息中提取 HTTP 状态码
     * 
     * @param message 异常消息
     * @return HTTP 状态码，如果无法提取则返回 null
     */
    private fun extractHttpStatusCode(message: String): Int? {
        // 尝试匹配常见的状态码格式
        val regex = Regex("""(\d{3})""")
        val matchResult = regex.find(message)
        return matchResult?.groupValues?.get(1)?.toIntOrNull()
    }
    
    /**
     * 关闭资源
     */
    fun close() {
        apiClient.close()
    }
}
