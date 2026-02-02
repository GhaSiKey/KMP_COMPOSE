package com.gaoshiqi.kmp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.gaoshiqi.kmp.data.model.AnimeItem
import com.gaoshiqi.kmp.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * 番剧数据仓库
 *
 * 提供番剧的 CRUD 操作，自动处理平台差异：
 * - Android/iOS/Desktop: 使用 SQLite 持久化存储
 * - JS (Web): 使用内存存储（仅当前会话有效）
 */
class AnimeRepository {
    // 内存存储，用于不支持 SQLite 的平台（Web）
    private val memoryStorage = MutableStateFlow<List<AnimeItem>>(emptyList())
    private var nextId = 1L

    /**
     * 获取所有番剧列表（响应式）
     */
    fun getAllAnime(): Flow<List<AnimeItem>> {
        val database = DatabaseProvider.getDatabase()

        return if (database != null) {
            // 使用 SQLDelight 查询（Android/iOS/Desktop）
            database.animeQueries.selectAll()
                .asFlow()
                .mapToList(Dispatchers.Default)
                .map { list ->
                    list.map { anime ->
                        AnimeItem(
                            id = anime.id,
                            name = anime.name,
                            isWatched = anime.isWatched,
                            createdAt = anime.createdAt
                        )
                    }
                }
        } else {
            // 使用内存存储（Web）
            memoryStorage
        }
    }

    /**
     * 添加新番剧
     */
    fun addAnime(name: String) {
        val database = DatabaseProvider.getDatabase()
        val currentTime = currentTimeMillis()

        if (database != null) {
            database.animeQueries.insert(name, false, currentTime)
        } else {
            // 内存存储
            val newItem = AnimeItem(
                id = nextId++,
                name = name,
                isWatched = false,
                createdAt = currentTime
            )
            memoryStorage.value = listOf(newItem) + memoryStorage.value
        }
    }

    /**
     * 更新番剧的观看状态
     */
    fun updateWatchedStatus(id: Long, isWatched: Boolean) {
        val database = DatabaseProvider.getDatabase()

        if (database != null) {
            database.animeQueries.updateWatchedStatus(isWatched, id)
        } else {
            // 内存存储
            memoryStorage.value = memoryStorage.value.map { item ->
                if (item.id == id) item.copy(isWatched = isWatched) else item
            }
        }
    }

    /**
     * 删除番剧
     */
    fun deleteAnime(id: Long) {
        val database = DatabaseProvider.getDatabase()

        if (database != null) {
            database.animeQueries.deleteById(id)
        } else {
            // 内存存储
            memoryStorage.value = memoryStorage.value.filter { it.id != id }
        }
    }

    /**
     * 是否支持持久化存储
     */
    fun isPersistent(): Boolean = DatabaseProvider.isSqliteSupported()
}

/**
 * 获取当前时间戳
 * 使用 expect/actual 处理不同平台的时间 API
 */
internal expect fun currentTimeMillis(): Long
