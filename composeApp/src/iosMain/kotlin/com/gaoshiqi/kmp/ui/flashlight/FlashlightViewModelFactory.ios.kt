package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gaoshiqi.kmp.shared.flashlight.FlashlightController
import com.gaoshiqi.kmp.shared.flashlight.IOSBrightnessPreferences

/**
 * iOS 平台的 FlashlightViewModel 工厂实现
 * 
 * 创建 iOS 特定的依赖：
 * - FlashlightController: 使用 UIScreen.main.brightness 控制亮度
 * - BrightnessPreferences: 使用 UserDefaults 存储偏好
 */
actual fun createFlashlightViewModel(): FlashlightViewModel {
    return FlashlightViewModel(
        controller = FlashlightController(),
        preferences = IOSBrightnessPreferences()
    )
}

/**
 * iOS 平台的 Composable ViewModel 创建函数
 */
@Composable
actual fun rememberFlashlightViewModel(): FlashlightViewModel {
    return remember { createFlashlightViewModel() }
}
