package com.gaoshiqi.kmp.shared.lenticular

/**
 * 光栅引擎 - 负责角度到图片映射的核心计算
 *
 * 有状态设计：记住当前显示的图片索引，通过迟滞（hysteresis）防止边界抖动。
 * 只有角度明确越过切换阈值后才切到下一张，模拟物理光栅卡的干脆切换手感。
 */
class LenticularEngine {

    /** 当前显示的图片索引 */
    private var currentIndex: Int = 0

    /** 上一次切换时的角度锚点 */
    private var anchorAngle: Float = 0f

    /**
     * 对原始角度应用低通滤波，消除传感器抖动噪声
     *
     * 公式: filtered = previousAngle + alpha * (rawAngle - previousAngle)
     *
     * @param rawAngle 原始传感器角度
     * @param previousAngle 上一次滤波后的角度
     * @param alpha 滤波系数（默认 0.15），值越小越平滑
     * @return 滤波后的角度值
     */
    fun applyLowPassFilter(
        rawAngle: Float,
        previousAngle: Float,
        alpha: Float = 0.15f
    ): Float {
        return previousAngle + alpha * (rawAngle - previousAngle)
    }

    /**
     * 重置引擎状态（进入预览页面时调用）
     */
    fun reset() {
        currentIndex = 0
        anchorAngle = 0f
    }

    /**
     * 根据当前倾斜角度和配置，计算渲染状态（迟滞切换模式）
     *
     * 算法核心：
     * 记住上次切换时的角度（anchorAngle），只有当前角度相对锚点偏移超过
     * degreesPerImage 时才切换到下一张/上一张，并更新锚点。
     *
     * 迟滞机制：切换阈值设为 degreesPerImage 的完整宽度（从区间中心到下一个区间中心），
     * 一旦切换，锚点移动整整一个 degreesPerImage，相当于在边界处产生了天然的迟滞——
     * 要回到原来的图片，必须反方向走完整的 degreesPerImage，而不是刚过边界就弹回。
     *
     * @param angle 当前倾斜角度（已滤波）
     * @param config 光栅卡配置
     * @return RenderState 包含当前应显示的图片索引
     */
    fun computeRenderState(angle: Float, config: LenticularConfig): RenderState {
        val imageCount = config.imageCount
        val step = config.degreesPerImage
        // 正方向：角度相对锚点正向偏移 >= degreesPerImage
        if (angle - anchorAngle >= step) {
            val steps = ((angle - anchorAngle) / step).toInt()
            anchorAngle += steps * step
            currentIndex = (currentIndex + steps) % imageCount
        }

        // 负方向：角度相对锚点负向偏移 >= degreesPerImage
        if (anchorAngle - angle >= step) {
            val steps = ((anchorAngle - angle) / step).toInt()
            anchorAngle -= steps * step
            currentIndex = ((currentIndex - steps) % imageCount + imageCount) % imageCount
        }

        return RenderState(displayIndex = currentIndex)
    }
}
