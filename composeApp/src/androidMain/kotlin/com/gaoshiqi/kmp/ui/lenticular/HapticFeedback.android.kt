package com.gaoshiqi.kmp.ui.lenticular

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android 触觉反馈实现
 *
 * 使用系统 Vibrator 服务触发短振动。
 * Android 8.0+ 使用 VibrationEffect.createOneShot，低版本使用 vibrate(long)。
 * Android 12+ 使用 VibratorManager 获取默认振动器。
 */
@Composable
actual fun rememberHapticFeedback(): HapticFeedbackPlayer {
    val context = LocalContext.current
    return remember {
        AndroidHapticFeedbackPlayer(context)
    }
}

private class AndroidHapticFeedbackPlayer(context: Context) : HapticFeedbackPlayer {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun tick() {
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 10ms 轻振动，EFFECT_TICK 级别的振幅
            vibrator.vibrate(
                VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }
}
