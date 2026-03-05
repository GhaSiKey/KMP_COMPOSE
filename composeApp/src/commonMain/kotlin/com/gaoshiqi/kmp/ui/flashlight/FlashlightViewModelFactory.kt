package com.gaoshiqi.kmp.ui.flashlight

import com.gaoshiqi.kmp.shared.flashlight.BrightnessPreferences
import com.gaoshiqi.kmp.shared.flashlight.FlashlightController

/**
 * 创建 FlashlightViewModel 实例
 * 
 * 这是一个 expect 函数，由各平台提供具体实现。
 * 各平台负责创建平台特定的 FlashlightController 和 BrightnessPreferences 实例。
 * 
 * 注意：这不是 Composable 函数，而是普通的工厂函数。
 * 在 Composable 中使用时，需要用 remember 包装以避免重复创建。
 * 
 * @return FlashlightViewModel 实例
 */
expect fun createFlashlightViewModel(): FlashlightViewModel
