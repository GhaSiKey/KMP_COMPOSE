package com.gaoshiqi.kmp.util

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.FileSystem

/**
 * 创建优化的 ImageLoader 配置
 * 
 * 配置说明：
 * - 内存缓存：使用 25% 的可用内存
 * - 磁盘缓存：最大 50 MB
 * - 网络层：使用 Ktor3 网络客户端
 * - 交叉淡入动画：300ms
 * 
 * @param context 平台上下文
 * @return 配置好的 ImageLoader 实例
 */
fun createImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            // 使用 Ktor3 作为网络层
            add(KtorNetworkFetcherFactory())
        }
        .memoryCache {
            MemoryCache.Builder()
                // 使用 25% 的可用内存作为缓存
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCache {
            newDiskCache()
        }
        // 启用交叉淡入动画
        .crossfade(true)
        .crossfade(300)
        // Debug 模式下启用日志
        .apply {
            // logger(DebugLogger())
        }
        .build()
}

/**
 * 创建磁盘缓存配置
 * 
 * @return 配置好的 DiskCache 实例
 */
private fun newDiskCache(): DiskCache {
    return DiskCache.Builder()
        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        // 最大 50 MB 磁盘缓存
        .maxSizeBytes(50 * 1024 * 1024)
        .build()
}
