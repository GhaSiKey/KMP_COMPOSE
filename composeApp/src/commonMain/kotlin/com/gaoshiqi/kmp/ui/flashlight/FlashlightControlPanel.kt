package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 手电筒控制面板
 * 
 * 显示在屏幕底部的半透明控制面板，包含：
 * - 亮度滑块：用于调节屏幕亮度（范围 0.0 - 1.0）
 * - 退出按钮：用于退出手电筒模式
 * 
 * 需求: 2.3, 3.1, 6.1, 6.2, 6.5
 * 
 * @param brightness 当前亮度级别，范围 0.0（最暗）到 1.0（最亮）
 * @param onBrightnessChange 亮度变化回调，参数为新的亮度级别
 * @param onExit 退出按钮点击回调
 * @param modifier Modifier
 */
@Composable
fun FlashlightControlPanel(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 半透明黑色背景（alpha 0.5）
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 标题和退出按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "亮度",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            
            // 退出按钮
            IconButton(
                onClick = onExit
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "退出",
                    tint = Color.White
                )
            }
        }
        
        // 亮度滑块
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = 0.0f..1.0f,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 亮度百分比显示
        Text(
            text = "${(brightness * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.End)
        )
    }
}
