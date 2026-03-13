package com.gaoshiqi.kmp.shared.lenticular

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Android 平台倾斜传感器实现
 *
 * 使用 SensorManager 注册 TYPE_GRAVITY 传感器，以 SENSOR_DELAY_GAME 采样率采集数据。
 * 从重力加速度值计算 pitch（前后倾斜）和 roll（左右倾斜）角度。
 *
 * @param context Android Context，用于获取 SensorManager
 */
actual class TiltSensor(private val context: Context) {

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var listener: SensorEventListener? = null

    /**
     * 开始采集倾斜数据
     *
     * 注册 TYPE_GRAVITY 传感器监听器，从重力分量计算 pitch/roll 角度。
     *
     * Android 传感器坐标系（手机竖持正面朝用户时）：
     * - X 轴：向右为正
     * - Y 轴：向上为正
     * - Z 轴：朝向用户为正
     *
     * 因此：
     * - pitch（前后倾斜）= atan2(gy, sqrt(gx² + gz²))，gy 反映纵向重力分量
     * - roll（左右倾斜）= atan2(gx, sqrt(gy² + gz²))，gx 反映横向重力分量
     */
    actual fun startObserving(): Flow<TiltData> = callbackFlow {
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        val eventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val gx = event.values[0]  // 横向（左右）
                val gy = event.values[1]  // 纵向（前后）
                val gz = event.values[2]  // 垂直（朝向用户）

                // pitch：前后倾斜，由 gy 决定
                val pitch = (atan2(
                    gy.toDouble(),
                    sqrt((gx * gx + gz * gz).toDouble())
                ) * 180.0 / PI).toFloat()

                // roll：左右倾斜，由 gx 决定
                val roll = (atan2(
                    gx.toDouble(),
                    sqrt((gy * gy + gz * gz).toDouble())
                ) * 180.0 / PI).toFloat()

                // 钳制到 [-90, +90] 范围
                trySend(
                    TiltData(
                        pitch = pitch.coerceIn(-90f, 90f),
                        roll = roll.coerceIn(-90f, 90f)
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // 精度变化不影响功能
            }
        }

        listener = eventListener
        sensorManager.registerListener(
            eventListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        awaitClose {
            sensorManager.unregisterListener(eventListener)
            listener = null
        }
    }

    /**
     * 停止采集并释放传感器资源
     */
    actual fun stopObserving() {
        listener?.let {
            sensorManager.unregisterListener(it)
            listener = null
        }
    }

    /**
     * 检查设备是否支持重力传感器
     */
    actual fun isAvailable(): Boolean {
        return sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null
    }
}
