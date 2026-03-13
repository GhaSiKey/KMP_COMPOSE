package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gaoshiqi.kmp.shared.lenticular.LenticularEngine
import com.gaoshiqi.kmp.shared.lenticular.TiltData
import com.gaoshiqi.kmp.ui.flashlight.HandleBackPress
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 重力感应测试页面
 *
 * 实时显示设备倾斜角度的原始值和低通滤波后的值，
 * 用于验证 Android/iOS 两端的 pitch/roll 数值、符号和范围是否一致。
 *
 * 包含：
 * - 数值面板：pitch（前后）/ roll（左右）的原始值和滤波值
 * - 可视化指示器：圆形区域内的圆点，随设备倾斜移动
 * - 传感器状态显示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiltTestScreen(onBack: () -> Unit) {
    val tiltSensor = rememberTiltSensor()
    val engine = remember { LenticularEngine() }

    var rawData by remember { mutableStateOf(TiltData(0f, 0f)) }
    var filteredPitch by remember { mutableStateOf(0f) }
    var filteredRoll by remember { mutableStateOf(0f) }
    val isSensorAvailable = remember { tiltSensor.isAvailable() }

    val scope = rememberCoroutineScope()
    var sensingJob by remember { mutableStateOf<Job?>(null) }

    // 生命周期感知：前台采集，后台暂停
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isSensorAvailable && sensingJob?.isActive != true) {
                        sensingJob = scope.launch {
                            tiltSensor.startObserving().collect { data ->
                                rawData = data
                                filteredPitch = engine.applyLowPassFilter(data.pitch, filteredPitch)
                                filteredRoll = engine.applyLowPassFilter(data.roll, filteredRoll)
                            }
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    sensingJob?.cancel()
                    sensingJob = null
                    tiltSensor.stopObserving()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensingJob?.cancel()
            tiltSensor.stopObserving()
        }
    }

    HandleBackPress(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("传感器测试") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSensorAvailable) {
                SensorUnavailableHint()
            } else {
                // 可视化指示器
                TiltVisualizer(
                    pitch = filteredPitch,
                    roll = filteredRoll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .aspectRatio(1f)
                )

                Spacer(Modifier.height(24.dp))

                // 数值面板
                DataPanel(
                    rawData = rawData,
                    filteredPitch = filteredPitch,
                    filteredRoll = filteredRoll
                )
            }
        }
    }
}

// ==================== 数值面板 ====================

@Composable
private fun DataPanel(
    rawData: TiltData,
    filteredPitch: Float,
    filteredRoll: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "传感器数据",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 表头
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DataLabel(text = "", modifier = Modifier.weight(1f))
            DataLabel(text = "原始值", modifier = Modifier.weight(1f))
            DataLabel(text = "滤波后", modifier = Modifier.weight(1f))
        }

        // Pitch 行
        DataRow(
            label = "Pitch（前后）",
            rawValue = rawData.pitch,
            filteredValue = filteredPitch
        )

        // Roll 行
        DataRow(
            label = "Roll（左右）",
            rawValue = rawData.roll,
            filteredValue = filteredRoll
        )
    }
}

@Composable
private fun DataRow(
    label: String,
    rawValue: Float,
    filteredValue: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        AngleText(
            value = rawValue,
            modifier = Modifier.weight(1f)
        )
        AngleText(
            value = filteredValue,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AngleText(
    value: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = formatAngle(value),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        ),
        color = color,
        modifier = modifier
    )
}

@Composable
private fun DataLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = modifier
    )
}

/**
 * 格式化角度值为 ±XX.X° 格式
 *
 * 使用 Kotlin Multiplatform 兼容的四舍五入方式，
 * 避免 String.format() 仅限 JVM 的问题。
 */
private fun formatAngle(value: Float): String {
    val rounded = (kotlin.math.abs(value) * 10).toInt() / 10f
    val intPart = rounded.toInt()
    val decPart = ((rounded - intPart) * 10).toInt()
    val absStr = "$intPart.$decPart"
    return if (value >= 0) "+$absStr°" else "-$absStr°"
}

// ==================== 传感器不可用提示 ====================

@Composable
private fun SensorUnavailableHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "当前设备不支持重力感应传感器",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}
