package com.gaoshiqi.kmp.shared.lenticular

import kotlinx.coroutines.flow.Flow

/**
 * 倾斜传感器
 *
 * 通过 expect/actual 机制实现跨平台传感器数据采集。
 * Android 使用 SensorManager，iOS 使用 CMMotionManager，
 * JVM 和 JS 平台不支持传感器，isAvailable() 返回 false。
 */
expect class TiltSensor {

    /**
     * 开始采集倾斜数据
     *
     * 以约 30Hz 频率发射倾斜角度值。
     * @return Flow<TiltData> 包含水平轴和垂直轴的倾斜角度
     */
    fun startObserving(): Flow<TiltData>

    /**
     * 停止采集并释放传感器资源
     */
    fun stopObserving()

    /**
     * 检查设备是否支持运动传感器
     *
     * @return true 表示设备支持重力感应，false 表示不支持
     */
    fun isAvailable(): Boolean
}
