package com.gaoshiqi.kmp.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 视频播放器状态，供 Android/iOS 平台的 Compose 控件层使用
 *
 * @property isPlaying 当前是否正在播放
 * @property currentTime 当前播放位置（秒）
 * @property duration 视频总时长（秒）
 * @property isBuffering 是否正在缓冲
 */
data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val currentTime: Int = 0,
    val duration: Int = 0,
    val isBuffering: Boolean = false
)

/**
 * 跨平台视频播放页面 (expect 声明)
 *
 * 各平台通过 actual 提供完整的视频播放体验（视频画面 + 控制 UI）：
 * - Android/iOS: 原生播放器 + Compose VideoPlayerControls 叠加
 * - Desktop: JavaFX MediaView + SwingPanel（JavaFX 自带控件或全屏播放）
 * - Web: HTML5 <video> 元素（浏览器原生控件）
 *
 * @param url 视频播放地址
 * @param title 视频标题
 * @param onBack 返回按钮回调
 * @param modifier Compose Modifier
 */
@Composable
expect fun PlatformVideoPlayerScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
)
