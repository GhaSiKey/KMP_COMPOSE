package com.gaoshiqi.kmp.ui.flashlight

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Android 平台的返回键处理实现
 * 
 * 使用 AndroidX 的 BackHandler 来拦截系统返回键事件。
 * 
 * @param onBack 返回键被按下时的回调
 */
@Composable
actual fun HandleBackPress(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}
