package com.gaoshiqi.kmp.ui.trending

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.placeholder_anime
import org.jetbrains.compose.resources.painterResource

/**
 * 封面图片组件
 * 使用 Coil 加载图片，支持占位符、错误处理和缓存
 * 
 * 功能特性：
 * - 使用 Coil 3 的 AsyncImage 进行图片加载
 * - 支持渐变动画（crossfade）
 * - 启用内存缓存和磁盘缓存
 * - 加载中显示占位符图片
 * - 加载失败显示错误占位符图片
 * - 使用 ContentScale.Crop 裁剪图片
 * - 8dp 圆角
 * - 提供无障碍内容描述
 * 
 * @param imageUrl 图片 URL
 * @param contentDescription 内容描述（用于无障碍）
 * @param modifier Modifier
 */
@Composable
fun CoverImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalPlatformContext.current
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)  // 启用渐变动画
            .memoryCacheKey(imageUrl)  // 设置内存缓存键
            .diskCacheKey(imageUrl)  // 设置磁盘缓存键
            .build(),
        contentDescription = contentDescription,
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(Res.drawable.placeholder_anime),
        error = painterResource(Res.drawable.placeholder_anime)
    )
}
