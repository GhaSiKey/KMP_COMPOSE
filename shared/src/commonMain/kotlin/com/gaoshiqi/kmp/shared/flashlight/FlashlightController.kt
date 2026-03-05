package com.gaoshiqi.kmp.shared.flashlight

/**
 * 手电筒亮度控制器
 * 
 * 负责控制屏幕亮度、屏幕常亮状态以及系统亮度恢复。
 * 使用 expect/actual 模式实现跨平台的亮度控制。
 */
expect class FlashlightController {
    
    /**
     * 设置屏幕亮度
     * 
     * @param level 亮度级别，范围 0.0（最暗）到 1.0（最亮）
     * @return Result<Unit> 成功返回 Result.success，失败返回 Result.failure（如权限被拒绝）
     */
    fun setBrightness(level: Float): Result<Unit>
    
    /**
     * 恢复系统原始亮度设置
     * 
     * 在退出手电筒模式时调用，将屏幕亮度恢复到启动手电筒前的状态。
     */
    fun restoreSystemBrightness()
    
    /**
     * 控制屏幕常亮状态
     * 
     * @param enabled true 表示保持屏幕常亮，false 表示允许屏幕休眠
     */
    fun keepScreenOn(enabled: Boolean)
    
    /**
     * 检查平台是否支持系统级亮度控制
     * 
     * @return true 表示支持系统级亮度控制（Android、iOS），
     *         false 表示不支持（Desktop、Web），需要使用透明度模拟
     */
    fun supportSystemBrightnessControl(): Boolean
}
