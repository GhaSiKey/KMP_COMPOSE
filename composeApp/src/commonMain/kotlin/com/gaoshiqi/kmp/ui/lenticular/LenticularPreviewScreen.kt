package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gaoshiqi.kmp.ui.flashlight.HandleBackPress

/**
 * 光栅卡全屏预览界面
 *
 * 沉浸式全屏显示光栅卡效果，进入时自动启动传感器采集，退出时停止。
 * 顶部显示半透明返回按钮，支持点击返回和设备返回键退出。
 *
 * 需求: 4.1, 4.2, 4.3, 4.4, 4.5
 *
 * @param viewModel 光栅卡 ViewModel（与编辑界面共享同一实例）
 * @param onBack 退出预览的回调
 */
@Composable
fun LenticularPreviewScreen(
    viewModel: LenticularViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    // 图片切换时触发触觉反馈
    var previousDisplayIndex by remember { mutableIntStateOf(uiState.renderState.displayIndex) }
    LaunchedEffect(uiState.renderState.displayIndex) {
        val current = uiState.renderState.displayIndex
        if (current != previousDisplayIndex) {
            haptic.tick()
            previousDisplayIndex = current
        }
    }

    // 生命周期感知的传感器管理
    // 进入时启动传感器，退出时停止；应用进入后台时暂停，恢复前台时重新开始
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startSensing()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopSensing()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopSensing()
        }
    }

    // 设备返回键支持
    HandleBackPress(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 光栅渲染器 - 图片以保持宽高比方式填充屏幕
        LenticularRenderer(
            renderState = uiState.renderState,
            images = uiState.images,
            modifier = Modifier.fillMaxSize()
        )

        // 半透明返回按钮（顶部左侧）
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "退出预览",
                tint = Color.White
            )
        }

        // 调试用：重力感应小球（右下角半透明覆盖，通过编辑页开关控制显隐）
        if (uiState.showTiltDebug) {
            TiltVisualizer(
                pitch = uiState.filteredPitch,
                roll = uiState.filteredRoll,
                dotColor = Color.Cyan,
                guideColor = Color.White.copy(alpha = 0.3f),
                labelColor = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(140.dp)
            )
        }
    }
}
