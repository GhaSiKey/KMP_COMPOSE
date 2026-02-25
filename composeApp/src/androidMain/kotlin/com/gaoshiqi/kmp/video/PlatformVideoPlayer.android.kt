package com.gaoshiqi.kmp.video

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

/**
 * Android 平台的视频播放页面
 *
 * ExoPlayer 渲染视频 + Compose VideoPlayerControls 叠加控件。
 * Compose 的 AndroidView 允许在上方正常叠加 Compose 内容并接收事件。
 */
@OptIn(UnstableApi::class)
@Composable
actual fun PlatformVideoPlayerScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    var playerState by remember { mutableStateOf(VideoPlayerState()) }

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // 设置媒体源
    LaunchedEffect(url) {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaItem = MediaItem.fromUri(url)

        val mediaSource = if (url.contains(".m3u8", ignoreCase = true)) {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // 从 ExoPlayer 当前状态构建 VideoPlayerState
    fun syncPlayerState() {
        playerState = VideoPlayerState(
            isPlaying = exoPlayer.isPlaying,
            currentTime = (exoPlayer.currentPosition / 1000).toInt(),
            duration = (exoPlayer.duration.coerceAtLeast(0) / 1000).toInt(),
            isBuffering = exoPlayer.playbackState == Player.STATE_BUFFERING
        )
    }

    // 监听播放状态变化
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = syncPlayerState()
            override fun onPlaybackStateChanged(playbackState: Int) = syncPlayerState()
        })
    }

    // 轮询进度更新（Slider 需要持续的 currentTime 更新）
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(500)
            if (exoPlayer.isPlaying) syncPlayerState()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    // UI：视频画面 + 控制面板叠加
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        VideoPlayerControls(
            isPlaying = playerState.isPlaying,
            currentTime = playerState.currentTime,
            duration = playerState.duration,
            onPlayPause = {
                exoPlayer.playWhenReady = !exoPlayer.isPlaying
            },
            onSeek = { exoPlayer.seekTo(it * 1000L) },
            onBack = onBack,
            title = title
        )
    }
}
