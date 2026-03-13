package com.gaoshiqi.kmp.shared.lenticular

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Web (JS) 平台倾斜传感器实现
 *
 * Web 平台不支持运动传感器，isAvailable() 始终返回 false。
 * startObserving() 返回空 Flow。
 */
actual class TiltSensor {

    /**
     * 开始采集倾斜数据
     *
     * Web 平台不支持传感器，返回空 Flow。
     */
    actual fun startObserving(): Flow<TiltData> = emptyFlow()

    /**
     * 停止采集（空实现）
     */
    actual fun stopObserving() {
        // 空实现 - Web 平台无传感器资源需要释放
    }

    /**
     * Web 平台不支持运动传感器
     */
    actual fun isAvailable(): Boolean = false
}
