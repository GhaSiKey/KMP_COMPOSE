package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable

/**
 * 在 Composable 中创建并记住 LenticularViewModel 实例
 *
 * 这是一个 expect 函数，各平台提供具体实现。
 * Android 平台需要获取 Context 来创建 TiltSensor，其他平台可以直接创建。
 *
 * @return LenticularViewModel 实例
 */
@Composable
expect fun rememberLenticularViewModel(): LenticularViewModel
