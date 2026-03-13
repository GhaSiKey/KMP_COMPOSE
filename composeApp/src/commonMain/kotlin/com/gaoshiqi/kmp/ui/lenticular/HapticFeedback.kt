package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable

/**
 * 跨平台触觉反馈
 *
 * 各平台通过 expect/actual 提供具体实现：
 * - Android: Vibrator 短振动
 * - iOS: UIImpactFeedbackGenerator
 * - JS/JVM: 空操作
 */
interface HapticFeedbackPlayer {
    /** 触发一次轻触觉反馈（用于光栅卡图片切换） */
    fun tick()
}

/**
 * 创建并记住一个触觉反馈播放器
 */
@Composable
expect fun rememberHapticFeedback(): HapticFeedbackPlayer
