package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import com.gaoshiqi.kmp.shared.lenticular.TiltSensor

/**
 * 在 Composable 中创建并记住 TiltSensor 实例
 *
 * Android 平台需要 Context 构造 TiltSensor，其他平台无参构造。
 * 通过 expect/actual 机制隐藏平台差异。
 */
@Composable
expect fun rememberTiltSensor(): TiltSensor
