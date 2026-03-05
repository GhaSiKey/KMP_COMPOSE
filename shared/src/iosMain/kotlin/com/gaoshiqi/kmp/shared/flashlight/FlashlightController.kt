package com.gaoshiqi.kmp.shared.flashlight

import platform.UIKit.UIApplication
import platform.UIKit.UIScreen

/**
 * iOS 平台的手电筒亮度控制器实现
 * 
 * 使用 UIScreen.main.brightness 控制屏幕亮度。
 * 注意：此实现会影响整个设备的屏幕亮度，但会在退出时恢复原始值。
 */
actual class FlashlightController {
    
    private var originalBrightness: Double = 0.0
    
    init {
        // 保存原始亮度值
        originalBrightness = UIScreen.mainScreen.brightness
    }
    
    /**
     * 设置屏幕亮度
     * 
     * 通过修改 UIScreen.main.brightness 属性来调节亮度。
     * 
     * @param level 亮度级别，范围 0.0（最暗）到 1.0（最亮）
     * @return Result<Unit> 成功返回 Result.success，失败返回 Result.failure
     */
    actual fun setBrightness(level: Float): Result<Unit> {
        return try {
            // 验证亮度范围
            if (level !in 0.0f..1.0f) {
                return Result.failure(IllegalArgumentException("亮度级别必须在 0.0 到 1.0 之间，当前值: $level"))
            }
            
            // 设置屏幕亮度
            UIScreen.mainScreen.brightness = level.toDouble()
            
            Result.success(Unit)
        } catch (e: Exception) {
            // 捕获任何异常
            Result.failure(e)
        }
    }
    
    /**
     * 恢复系统原始亮度设置
     * 
     * 将屏幕亮度恢复到启动手电筒前的状态。
     */
    actual fun restoreSystemBrightness() {
        try {
            UIScreen.mainScreen.brightness = originalBrightness
        } catch (e: Exception) {
            // 记录错误但不抛出异常，确保清理流程继续执行
            e.printStackTrace()
        }
    }
    
    /**
     * 控制屏幕常亮状态
     * 
     * 使用 UIApplication.shared.isIdleTimerDisabled 防止屏幕自动休眠。
     * 
     * @param enabled true 表示保持屏幕常亮，false 表示允许屏幕休眠
     */
    actual fun keepScreenOn(enabled: Boolean) {
        try {
            UIApplication.sharedApplication.idleTimerDisabled = enabled
        } catch (e: Exception) {
            // 记录错误但不抛出异常
            e.printStackTrace()
        }
    }
    
    /**
     * 检查平台是否支持系统级亮度控制
     * 
     * iOS 平台支持通过 UIScreen.main.brightness 控制屏幕亮度。
     * 
     * @return true 表示支持系统级亮度控制
     */
    actual fun supportSystemBrightnessControl(): Boolean = true
}
