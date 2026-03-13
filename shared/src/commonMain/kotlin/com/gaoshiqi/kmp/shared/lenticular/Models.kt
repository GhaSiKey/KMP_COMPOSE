package com.gaoshiqi.kmp.shared.lenticular

/**
 * 传感器原始数据
 * @property pitch 垂直轴倾斜角度（前后倾斜），单位：度，范围 -90° 到 +90°
 * @property roll 水平轴倾斜角度（左右倾斜），单位：度，范围 -90° 到 +90°
 */
data class TiltData(
    val pitch: Float,
    val roll: Float
)

/**
 * 感应轴向
 */
enum class SensingAxis {
    /** 垂直轴 - 前后倾斜 */
    VERTICAL,
    /** 水平轴 - 左右倾斜 */
    HORIZONTAL
}

/**
 * 光栅卡配置
 * @property imageCount 图片数量（2~10）
 * @property sensingAxis 感应轴向
 * @property degreesPerImage 每倾斜多少度切换到下一张图片（5°~30°），到最后一张后循环回第一张
 */
data class LenticularConfig(
    val imageCount: Int,
    val sensingAxis: SensingAxis = SensingAxis.VERTICAL,
    val degreesPerImage: Float = 15f
) {
    init {
        require(imageCount in 2..10) { "图片数量必须在 2 到 10 之间" }
        require(degreesPerImage in MIN_DEGREES_PER_IMAGE..MAX_DEGREES_PER_IMAGE) {
            "每张图角度区间必须在 ${MIN_DEGREES_PER_IMAGE}° 到 ${MAX_DEGREES_PER_IMAGE}° 之间"
        }
    }

    /** 一个完整循环的角度宽度（所有图片轮播一遍） */
    val cycleWidth: Float get() = degreesPerImage * imageCount

    companion object {
        const val MIN_DEGREES_PER_IMAGE = 5f
        const val MAX_DEGREES_PER_IMAGE = 30f
    }
}

/**
 * 渲染状态 - LenticularEngine 的计算输出
 * @property displayIndex 当前应显示的图片索引
 */
data class RenderState(
    val displayIndex: Int
)
