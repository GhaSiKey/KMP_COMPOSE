package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gaoshiqi.kmp.shared.flashlight.FlashlightController
import com.gaoshiqi.kmp.shared.flashlight.JvmBrightnessPreferences

/**
 * JVM (Desktop) 平台的 FlashlightViewModel 工厂实现
 * 
 * 创建 JVM 特定的依赖：
 * - FlashlightController: 不支持系统级亮度控制，使用透明度模拟
 * - BrightnessPreferences: 使用 Java Preferences API 存储偏好
 */
actual fun createFlashlightViewModel(): FlashlightViewModel {
    return FlashlightViewModel(
        controller = FlashlightController(),
        preferences = JvmBrightnessPreferences()
    )
}

/**
 * JVM 平台的 Composable ViewModel 创建函数
 */
@Composable
actual fun rememberFlashlightViewModel(): FlashlightViewModel {
    return remember { createFlashlightViewModel() }
}
