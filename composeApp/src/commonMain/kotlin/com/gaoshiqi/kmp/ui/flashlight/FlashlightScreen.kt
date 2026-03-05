package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gaoshiqi.kmp.shared.flashlight.BrightnessControlMode
import kotlinx.coroutines.delay

/**
 * 处理返回键事件的 expect 函数
 * 
 * 各平台提供具体实现来处理返回键（Android）或等效的返回操作。
 * 
 * @param onBack 返回键被按下时的回调
 */
@Composable
expect fun HandleBackPress(onBack: () -> Unit)

/**
 * 手电筒主屏幕
 * 
 * 全屏显示纯白色背景，提供屏幕照明功能。
 * 
 * 功能特性：
 * - 全屏纯白色背景（RGB 255, 255, 255）
 * - 根据亮度控制模式显示半透明黑色覆盖层（OPACITY 模式）
 * - 底部显示控制面板（亮度滑块和退出按钮）
 * - 点击屏幕任意位置显示控制面板
 * - 3 秒无操作自动隐藏控制面板
 * - 组件销毁时自动清理资源
 * 
 * 需求: 1.1, 1.2, 1.3, 2.3, 3.3, 4.5, 6.3, 6.4
 * 
 * @param onExit 退出手电筒模式的回调
 * @param viewModel 手电筒 ViewModel，管理状态和业务逻辑（默认使用平台特定的工厂创建）
 */
@Composable
fun FlashlightScreen(
    onExit: () -> Unit,
    viewModel: FlashlightViewModel = rememberFlashlightViewModel()
) {
    // 收集 UI 状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 处理返回键事件（Android 和其他支持返回键的平台）
    HandleBackPress {
        // 调用 ViewModel 清理资源
        viewModel.exitFlashlight()
        // 触发导航返回
        onExit()
    }
    
    // 组件初始化和清理
    DisposableEffect(Unit) {
        // 初始化手电筒功能
        viewModel.initialize()
        
        onDispose {
            // 组件销毁时清理资源
            viewModel.exitFlashlight()
        }
    }
    
    // 自动隐藏控制面板逻辑（3 秒无操作）
    LaunchedEffect(uiState.isControlPanelVisible) {
        if (uiState.isControlPanelVisible) {
            // 等待 3 秒
            delay(3000)
            // 自动隐藏控制面板
            viewModel.hideControlPanel()
        }
    }
    
    // 全屏容器
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 纯白色背景 (RGB 255, 255, 255)
            .clickable(
                // 禁用点击涟漪效果
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // 点击屏幕任意位置显示控制面板
                if (!uiState.isControlPanelVisible) {
                    viewModel.showControlPanel()
                }
            }
    ) {
        // OPACITY 模式：添加半透明黑色覆盖层模拟亮度
        if (uiState.brightnessControlMode == BrightnessControlMode.OPACITY) {
            // 计算覆盖层透明度：亮度越低，覆盖层越不透明
            val overlayAlpha = 1.0f - uiState.brightness
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
            )
        }
        
        // 控制面板（底部对齐）
        if (uiState.isControlPanelVisible) {
            FlashlightControlPanel(
                brightness = uiState.brightness,
                onBrightnessChange = { newBrightness ->
                    viewModel.setBrightness(newBrightness)
                },
                onExit = {
                    // 退出手电筒模式
                    viewModel.exitFlashlight()
                    onExit()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
        
        // 错误信息显示（顶部对齐）
        uiState.errorMessage?.let { errorMessage ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                containerColor = Color.Black.copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                Text(text = errorMessage)
            }
        }
    }
}
