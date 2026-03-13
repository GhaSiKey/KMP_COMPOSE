package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/**
 * iOS 触觉反馈实现
 *
 * 使用 UIImpactFeedbackGenerator（iOS 10+），Light 级别的触觉反馈。
 * prepare() 预热 Taptic Engine 以减少延迟。
 */
@Composable
actual fun rememberHapticFeedback(): HapticFeedbackPlayer {
    return remember { IOSHapticFeedbackPlayer() }
}

private class IOSHapticFeedbackPlayer : HapticFeedbackPlayer {

    private val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)

    init {
        generator.prepare()
    }

    override fun tick() {
        generator.impactOccurred()
        // 重新 prepare 以保持低延迟
        generator.prepare()
    }
}
