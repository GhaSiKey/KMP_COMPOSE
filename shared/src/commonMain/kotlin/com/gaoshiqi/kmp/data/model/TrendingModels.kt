package com.gaoshiqi.kmp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 趋势接口响应
 */
@Serializable
data class TrendingResponse(
    val data: List<TrendingSubjectItem>,
    val total: Int
)

/**
 * 趋势番剧条目
 */
@Serializable
data class TrendingSubjectItem(
    val subject: TrendingSubject,
    val count: Int  // 热度计数
)

/**
 * 番剧详细信息
 */
@Serializable
data class TrendingSubject(
    val id: Int,
    val name: String,
    @SerialName("nameCN")
    val nameCN: String,
    val type: Int,
    val info: String,
    val rating: TrendingRate,
    val locked: Boolean,
    val nsfw: Boolean,
    val images: Images
)

/**
 * 评分信息
 */
@Serializable
data class TrendingRate(
    val rank: Int,
    val count: List<Int>,  // 各评分段人数分布（1-10分）
    val score: Double,
    val total: Int  // 评分总人数
)

/**
 * 封面图片集合
 */
@Serializable
data class Images(
    val large: String? = null,
    val common: String? = null,
    val medium: String? = null,
    val small: String? = null,
    val grid: String? = null
)

/**
 * 获取显示名称（优先中文名）
 */
val TrendingSubject.displayName: String
    get() = nameCN.ifBlank { name }

/**
 * 获取最佳封面图片 URL
 * 优先级：common > medium > large > small > grid
 */
val Images.bestUrl: String?
    get() = common ?: medium ?: large ?: small ?: grid

/**
 * 格式化评分（保留一位小数）
 */
val TrendingRate.formattedScore: String
    get() = (kotlin.math.round(score * 10) / 10).toString()
