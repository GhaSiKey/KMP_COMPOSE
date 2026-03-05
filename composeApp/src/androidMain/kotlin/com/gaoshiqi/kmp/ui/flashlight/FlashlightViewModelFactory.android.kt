package com.gaoshiqi.kmp.ui.flashlight

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gaoshiqi.kmp.shared.flashlight.AndroidBrightnessPreferences
import com.gaoshiqi.kmp.shared.flashlight.FlashlightController

/**
 * Android 平台的 FlashlightViewModel 工厂实现
 * 
 * 创建 Android 特定的依赖：
 * - FlashlightController: 使用 Activity 的 Window 控制亮度
 * - BrightnessPreferences: 使用 SharedPreferences 存储偏好
 * 
 * 注意：需要在 Activity 上下文中调用
 */
actual fun createFlashlightViewModel(): FlashlightViewModel {
    // 这个函数不应该被直接调用，因为需要 Context
    // 使用 rememberFlashlightViewModel() 代替
    throw UnsupportedOperationException(
        "Android 平台请使用 rememberFlashlightViewModel() 在 Composable 中创建 ViewModel"
    )
}

/**
 * Android 平台的 Composable ViewModel 创建函数
 * 
 * 在 Composable 上下文中创建 FlashlightViewModel，自动获取 Activity Context。
 * 
 * @return FlashlightViewModel 实例
 */
@Composable
actual fun rememberFlashlightViewModel(): FlashlightViewModel {
    val context = LocalContext.current
    return remember {
        createFlashlightViewModel(context)
    }
}

/**
 * Android 平台的 FlashlightViewModel 创建辅助函数
 * 
 * @param context Android Context，必须是 Activity 实例
 */
private fun createFlashlightViewModel(context: Context): FlashlightViewModel {
    val activity = context as? android.app.Activity
        ?: throw IllegalStateException("FlashlightScreen 必须在 Activity 上下文中使用")
    
    return FlashlightViewModel(
        controller = FlashlightController(activity),
        preferences = AndroidBrightnessPreferences(context)
    )
}
