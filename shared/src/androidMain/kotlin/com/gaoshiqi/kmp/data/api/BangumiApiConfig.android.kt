package com.gaoshiqi.kmp.data.api

/**
 * Android 平台的 Bangumi API 配置
 * 
 * 直接访问 Bangumi API（无 CORS 限制）
 */
actual object BangumiApiConfig {
    actual val baseUrl: String = "https://next.bgm.tv/p1/"
}
