package com.gaoshiqi.kmp.data.model

/**
 * 番剧数据模型
 *
 * 用于 UI 层展示，与数据库表结构对应
 */
data class AnimeItem(
    val id: Long,
    val name: String,
    val isWatched: Boolean,
    val createdAt: Long
)
