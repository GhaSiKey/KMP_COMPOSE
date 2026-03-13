package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gaoshiqi.kmp.shared.lenticular.TiltSensor

@Composable
actual fun rememberTiltSensor(): TiltSensor {
    return remember { TiltSensor() }
}
