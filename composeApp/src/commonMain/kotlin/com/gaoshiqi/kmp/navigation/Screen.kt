package com.gaoshiqi.kmp.navigation

import kotlinx.serialization.Serializable

/**
 * 应用页面路由定义
 *
 * Navigation 库使用字符串作为路由标识，
 * 集中定义为常量避免硬编码。
 */
object Route {

    /** 首页 */
    const val HOME = "home"

    /** 狗狗图片画廊 */
    const val DOG_GALLERY = "dogGallery"

    /** 追番列表 */
    const val ANIME_LIST = "animeList"

    /** 视频列表 */
    const val VIDEO_LIST = "videoList"
}

/**
 * 视频播放页面路由（类型安全）
 *
 * KMP Navigation 2.8+ 支持使用 @Serializable data class 定义带参数的路由，
 * 这比字符串拼接更安全，编译期即可检查类型错误。
 *
 * @param videoId 视频 ID，用于从 VideoData 中查找视频信息
 */
@Serializable
data class VideoPlayerRoute(val videoId: String)
