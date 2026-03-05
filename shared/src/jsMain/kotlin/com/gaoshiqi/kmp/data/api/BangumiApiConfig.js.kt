package com.gaoshiqi.kmp.data.api

/**
 * Web 平台的 Bangumi API 配置
 * 
 * 注意：由于 CORS 限制，Web 平台无法直接访问 Bangumi API
 * 此配置保留用于未来可能的代理服务器支持
 */
actual object BangumiApiConfig {
    actual val baseUrl: String = "https://next.bgm.tv/p1/"
}
