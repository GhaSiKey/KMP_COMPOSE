package com.gaoshiqi.kmp.screen

import androidx.compose.runtime.Composable
import com.gaoshiqi.kmp.video.PlatformVideoPlayerScreen

/**
 * 视频播放页面
 *
 * 委托给各平台的 [PlatformVideoPlayerScreen] 实现。
 * 各平台自行决定视频渲染和控件交互方式。
 */
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    videoTitle: String,
    onBack: () -> Unit
) {
    PlatformVideoPlayerScreen(
        url = videoUrl,
        title = videoTitle,
        onBack = onBack
    )
}
