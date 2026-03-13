package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gaoshiqi.kmp.shared.lenticular.LenticularEngine
import com.gaoshiqi.kmp.shared.lenticular.TiltSensor

/**
 * JS (Web) 平台的 LenticularViewModel 创建函数
 *
 * Web 平台不支持传感器，isAvailable() 返回 false。
 *
 * @return LenticularViewModel 实例
 */
@Composable
actual fun rememberLenticularViewModel(): LenticularViewModel {
    return remember {
        LenticularViewModel(
            tiltSensor = TiltSensor(),
            engine = LenticularEngine()
        )
    }
}
