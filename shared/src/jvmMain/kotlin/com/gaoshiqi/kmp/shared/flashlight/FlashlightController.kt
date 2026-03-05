package com.gaoshiqi.kmp.shared.flashlight

/**
 * Desktop (JVM) 平台的手电筒亮度控制器实现
 * 
 * Desktop 平台不支持系统级亮度控制，所有亮度调节将通过 UI 层的透明度模拟实现。
 * 此实现提供空操作，确保跨平台代码能够正常运行。
 */
actual class FlashlightController {
    
    /**
     * 设置屏幕亮度
     * 
     * Desktop 平台不支持系统级亮度控制，此方法返回成功但不执行实际操作。
     * UI 层将通过调整白色覆盖层的透明度来模拟亮度变化。
     * 
     * @param level 亮度级别，范围 0.0（最暗）到 1.0（最亮）
     * @return Result<Unit> 始终返回 Result.success
     */
    actual fun setBrightness(level: Float): Result<Unit> {
        // 验证亮度范围
        if (level !in 0.0f..1.0f) {
            return Result.failure(IllegalArgumentException("亮度级别必须在 0.0 到 1.0 之间，当前值: $level"))
        }
        
        // Desktop 平台不支持系统级亮度控制，返回成功
        return Result.success(Unit)
    }
    
    /**
     * 恢复系统原始亮度设置
     * 
     * Desktop 平台不支持系统级亮度控制，此方法为空实现。
     */
    actual fun restoreSystemBrightness() {
        // 空实现 - Desktop 平台不需要恢复亮度
    }
    
    /**
     * 控制屏幕常亮状态
     * 
     * Desktop 平台的屏幕常亮由操作系统的电源管理控制。
     * 此方法为空实现，应用无法直接控制屏幕休眠。
     * 
     * @param enabled true 表示保持屏幕常亮，false 表示允许屏幕休眠
     */
    actual fun keepScreenOn(enabled: Boolean) {
        // 空实现 - Desktop 平台由操作系统控制屏幕休眠
        // 可以考虑使用 JVM 特定 API（如 java.awt.Robot）模拟用户活动，
        // 但这超出了当前需求范围
    }
    
    /**
     * 检查平台是否支持系统级亮度控制
     * 
     * Desktop 平台不支持系统级亮度控制。
     * UI 层将使用透明度模拟模式（BrightnessControlMode.OPACITY）。
     * 
     * @return false 表示不支持系统级亮度控制
     */
    actual fun supportSystemBrightnessControl(): Boolean = false
}
