package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gaoshiqi.kmp.shared.lenticular.LenticularEngine
import com.gaoshiqi.kmp.shared.lenticular.TiltSensor

/**
 * Android 平台的 LenticularViewModel 创建函数
 *
 * 在 Composable 上下文中创建 LenticularViewModel，自动获取 Context 来构造 TiltSensor。
 *
 * @return LenticularViewModel 实例
 */
@Composable
actual fun rememberLenticularViewModel(): LenticularViewModel {
    val context = LocalContext.current
    return remember {
        LenticularViewModel(
            tiltSensor = TiltSensor(context),
            engine = LenticularEngine()
        )
    }
}
