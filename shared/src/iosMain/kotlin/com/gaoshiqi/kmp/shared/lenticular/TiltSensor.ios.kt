package com.gaoshiqi.kmp.shared.lenticular

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import kotlin.math.PI

/**
 * iOS 平台倾斜传感器实现
 *
 * 使用 CMMotionManager 的 deviceMotion 获取设备姿态数据。
 * 更新频率设置为 1/30 秒（约 30Hz）。
 * 从 attitude.pitch 和 attitude.roll 获取倾斜角度。
 */
actual class TiltSensor {

    private val motionManager = CMMotionManager()

    /**
     * 开始采集倾斜数据
     *
     * 使用 startDeviceMotionUpdatesToQueue 获取设备姿态，
     * 从 attitude.pitch（前后倾斜）和 attitude.roll（左右倾斜）提取角度。
     * CoreMotion 返回弧度值，需转换为角度。
     */
    actual fun startObserving(): Flow<TiltData> = callbackFlow {
        motionManager.deviceMotionUpdateInterval = 1.0 / 30.0

        motionManager.startDeviceMotionUpdatesToQueue(
            NSOperationQueue.mainQueue
        ) { motion, _ ->
            motion?.let {
                // CoreMotion attitude 返回弧度，转换为角度
                val pitch = (it.attitude.pitch * 180.0 / PI).toFloat()
                val roll = (it.attitude.roll * 180.0 / PI).toFloat()

                trySend(
                    TiltData(
                        pitch = pitch.coerceIn(-90f, 90f),
                        roll = roll.coerceIn(-90f, 90f)
                    )
                )
            }
        }

        awaitClose {
            motionManager.stopDeviceMotionUpdates()
        }
    }

    /**
     * 停止采集并释放传感器资源
     */
    actual fun stopObserving() {
        motionManager.stopDeviceMotionUpdates()
    }

    /**
     * 检查设备是否支持 deviceMotion
     */
    actual fun isAvailable(): Boolean {
        return motionManager.isDeviceMotionAvailable()
    }
}
