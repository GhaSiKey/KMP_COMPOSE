package com.gaoshiqi.kmp.video

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
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView

/**
 * iOS 平台的视频播放页面
 *
 * AVPlayer 渲染视频 + Compose VideoPlayerControls 叠加控件。
 * UIKitView 嵌入的 UIView 允许 Compose 在上方正常叠加内容。
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformVideoPlayerScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier
) {
    var playerState by remember { mutableStateOf(VideoPlayerState()) }

    val player = remember { AVPlayer() }
    val playerLayer = remember { AVPlayerLayer() }

    // 设置视频源
    LaunchedEffect(url) {
        val nsUrl = NSURL.URLWithString(url) ?: return@LaunchedEffect
        val playerItem = AVPlayerItem(uRL = nsUrl)
        player.replaceCurrentItemWithPlayerItem(playerItem)
        playerLayer.player = player
        player.play()
    }

    // 进度监听
    DisposableEffect(player) {
        val interval = CMTimeMake(value = 1, timescale = 2)
        val observer = player.addPeriodicTimeObserverForInterval(interval, queue = null) { time ->
            val currentSeconds = CMTimeGetSeconds(time).toInt()
            val durationSeconds = player.currentItem?.duration?.let {
                val d = CMTimeGetSeconds(it)
                if (d.isNaN() || d.isInfinite()) 0 else d.toInt()
            } ?: 0
            val isPlaying = player.timeControlStatus == AVPlayerTimeControlStatusPlaying

            playerState = VideoPlayerState(
                isPlaying = isPlaying,
                currentTime = currentSeconds,
                duration = durationSeconds,
                isBuffering = false
            )
        }

        onDispose {
            player.removeTimeObserver(observer)
            player.pause()
        }
    }

    // UI
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        UIKitView(
            factory = {
                UIView().apply {
                    layer.addSublayer(playerLayer)
                    playerLayer.videoGravity = AVLayerVideoGravityResizeAspect
                }
            },
            update = { view ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                playerLayer.frame = view.bounds
                CATransaction.commit()
            },
            modifier = Modifier.fillMaxSize()
        )

        VideoPlayerControls(
            isPlaying = playerState.isPlaying,
            currentTime = playerState.currentTime,
            duration = playerState.duration,
            onPlayPause = {
                if (playerState.isPlaying) player.pause() else player.play()
            },
            onSeek = { player.seekToTime(CMTimeMake(value = it.toLong(), timescale = 1)) },
            onBack = onBack,
            title = title
        )
    }
}
