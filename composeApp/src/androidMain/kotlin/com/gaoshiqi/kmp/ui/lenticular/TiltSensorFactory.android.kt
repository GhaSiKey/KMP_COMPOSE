package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gaoshiqi.kmp.shared.lenticular.TiltSensor

@Composable
actual fun rememberTiltSensor(): TiltSensor {
    val context = LocalContext.current
    return remember { TiltSensor(context) }
}
