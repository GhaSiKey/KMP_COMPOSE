package com.gaoshiqi.kmp.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.ic_pause
import kmp.composeapp.generated.resources.ic_play

/**
 * 视频播放器自定义控制面板
 *
 * @param isPlaying 当前播放状态
 * @param currentTime 当前播放位置（秒）
 * @param duration 视频总时长（秒）
 * @param onPlayPause 播放/暂停切换回调
 * @param onSeek 拖动进度条后的 seek 回调
 * @param onBack 返回按钮回调
 * @param title 视频标题（显示在顶部）
 * @param modifier Modifier
 */
@Composable
fun VideoPlayerControls(
    isPlaying: Boolean,
    currentTime: Int,
    duration: Int,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onBack: () -> Unit,
    title: String = "",
    modifier: Modifier = Modifier
) {
    // 控制面板可见性
    var isVisible by remember { mutableStateOf(true) }
    // 是否正在拖动进度条
    var isDragging by remember { mutableStateOf(false) }
    // 拖动时的临时位置
    var dragPosition by remember { mutableStateOf(0f) }

    // 3秒后自动隐藏控制面板（播放中且未拖动时）
    LaunchedEffect(isPlaying, isVisible, isDragging) {
        if (isPlaying && isVisible && !isDragging) {
            delay(3000)
            isVisible = false
        }
    }

    // 点击区域：点击显示/隐藏控制面板
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isVisible = !isVisible
            }
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                // 顶部栏：返回按钮 + 标题
                TopBar(
                    title = title,
                    onBack = onBack,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                // 中央播放/暂停按钮
                CenterPlayButton(
                    isPlaying = isPlaying,
                    onPlayPause = onPlayPause,
                    modifier = Modifier.align(Alignment.Center)
                )

                // 底部控制栏：进度条 + 时间
                BottomControls(
                    currentTime = if (isDragging) dragPosition.toInt() else currentTime,
                    duration = duration,
                    onSeekStart = { isDragging = true },
                    onSeeking = { dragPosition = it },
                    onSeekEnd = { position ->
                        isDragging = false
                        onSeek(position.toInt())
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

/**
 * 顶部栏
 */
@Composable
private fun TopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Text(
                text = "←",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1
        )
    }
}

/**
 * 中央播放/暂停按钮
 */
@Composable
private fun CenterPlayButton(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onPlayPause,
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        Icon(
            painter = painterResource(
                if (isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play
            ),
            contentDescription = if (isPlaying) "暂停" else "播放",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * 底部控制栏
 */
@Composable
private fun BottomControls(
    currentTime: Int,
    duration: Int,
    onSeekStart: () -> Unit,
    onSeeking: (Float) -> Unit,
    onSeekEnd: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // 进度条
        Slider(
            value = currentTime.toFloat(),
            onValueChange = {
                onSeekStart()
                onSeeking(it)
            },
            onValueChangeFinished = {
                onSeekEnd(currentTime.toFloat())
            },
            valueRange = 0f..duration.coerceAtLeast(1).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // 时间显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDurationSeconds(currentTime),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatDurationSeconds(duration),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
