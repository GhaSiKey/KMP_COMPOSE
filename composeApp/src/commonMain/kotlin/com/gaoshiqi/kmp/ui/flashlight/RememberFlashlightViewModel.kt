package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.runtime.Composable

/**
 * 在 Composable 中创建并记住 FlashlightViewModel 实例
 * 
 * 这是一个 expect 函数，各平台提供具体实现。
 * Android 平台需要获取 Context，其他平台可以直接创建。
 * 
 * @return FlashlightViewModel 实例
 */
@Composable
expect fun rememberFlashlightViewModel(): FlashlightViewModel
