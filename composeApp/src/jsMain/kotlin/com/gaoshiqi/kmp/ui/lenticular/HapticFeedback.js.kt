package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberHapticFeedback(): HapticFeedbackPlayer {
    return remember { NoOpHapticFeedbackPlayer }
}

private object NoOpHapticFeedbackPlayer : HapticFeedbackPlayer {
    override fun tick() {}
}
