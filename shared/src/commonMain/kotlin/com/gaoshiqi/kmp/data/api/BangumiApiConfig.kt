package com.gaoshiqi.kmp.data.api

/**
 * Bangumi API 配置
 * 
 * 使用 expect/actual 机制为不同平台提供不同的 API 地址
 */
expect object BangumiApiConfig {
    /**
     * API 基础 URL
     * - Web 平台：使用本地代理服务器
     * - 其他平台：直接访问 Bangumi API
     */
    val baseUrl: String
}
