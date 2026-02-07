package com.gaoshiqi.kmp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import VideoPlayer
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource

/**
 * 视频播放页面
 *
 * 使用 MediaPlayer-KMP 库实现跨平台视频播放，
 * 支持 MP4 直链和 HLS (m3u8) 流媒体格式。
 *
 * 注意：MediaPlayer-KMP 的 VideoPlayer 不提供播放状态回调，
 * 因此使用其内置控件，仅添加返回按钮。
 *
 * @param videoUrl 视频播放地址
 * @param videoTitle 视频标题
 * @param onBack 返回按钮回调
 */
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    videoTitle: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 视频播放器（使用内置控件）
        VideoPlayer(
            modifier = Modifier.fillMaxSize(),
            url = videoUrl,
            autoPlay = true,
            showControls = true
        )

        // 返回按钮（覆盖在左上角）
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_back),
                contentDescription = "返回"
            )
        }
    }
}
