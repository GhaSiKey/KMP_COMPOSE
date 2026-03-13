package com.gaoshiqi.kmp.ui.lenticular

import com.gaoshiqi.kmp.shared.lenticular.RenderState
import com.gaoshiqi.kmp.shared.lenticular.SensingAxis

/**
 * 光栅卡 UI 状态
 *
 * 包含编辑界面和预览界面所需的全部状态信息。
 */
data class LenticularUiState(
    /** 用户添加的图片 URI 列表 */
    val images: List<String> = emptyList(),
    /** 当前感应轴向 */
    val sensingAxis: SensingAxis = SensingAxis.VERTICAL,
    /** 每张图片对应的角度区间宽度 */
    val degreesPerImage: Float = 15f,
    /** 当前滤波后的倾斜角度（用于光栅引擎计算的单轴值） */
    val currentAngle: Float = 0f,
    /** 滤波后的 pitch 值，用于调试可视化 */
    val filteredPitch: Float = 0f,
    /** 滤波后的 roll 值，用于调试可视化 */
    val filteredRoll: Float = 0f,
    /** 当前渲染状态 */
    val renderState: RenderState = RenderState(displayIndex = 0),
    /** 传感器是否可用 */
    val isSensorAvailable: Boolean = true,
    /** 是否在预览页显示重力感应调试小球 */
    val showTiltDebug: Boolean = false
) {
    /** 图片数量 */
    val imageCount: Int get() = images.size

    /** 是否满足预览条件（至少 2 张图片） */
    val canPreview: Boolean get() = images.size >= 2

    /** 模式标签 */
    val modeLabel: String get() = when (images.size) {
        2 -> "双变"
        3 -> "三变"
        else -> "${images.size}变"
    }
}
