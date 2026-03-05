package com.gaoshiqi.kmp.shared.flashlight

import kotlinx.browser.window
import kotlin.js.Promise

/**
 * Web (JS) 平台的手电筒亮度控制器实现
 * 
 * Web 平台不支持系统级亮度控制，所有亮度调节将通过 UI 层的透明度模拟实现。
 * 此实现提供空操作，但尝试使用 Screen Wake Lock API 保持屏幕常亮（如果浏览器支持）。
 */
actual class FlashlightController {
    
    // 保存 Wake Lock 对象的引用
    private var wakeLock: dynamic = null
    
    /**
     * 设置屏幕亮度
     * 
     * Web 平台不支持系统级亮度控制，此方法返回成功但不执行实际操作。
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
        
        // Web 平台不支持系统级亮度控制，返回成功
        return Result.success(Unit)
    }
    
    /**
     * 恢复系统原始亮度设置
     * 
     * Web 平台不支持系统级亮度控制，此方法为空实现。
     */
    actual fun restoreSystemBrightness() {
        // 空实现 - Web 平台不需要恢复亮度
    }
    
    /**
     * 控制屏幕常亮状态
     * 
     * Web 平台尝试使用 Screen Wake Lock API 保持屏幕常亮。
     * 如果浏览器不支持此 API，则静默失败。
     * 
     * @param enabled true 表示保持屏幕常亮，false 表示允许屏幕休眠
     */
    actual fun keepScreenOn(enabled: Boolean) {
        try {
            if (enabled) {
                // 请求 Wake Lock
                val navigator = window.navigator.asDynamic()
                if (navigator.wakeLock != null && navigator.wakeLock != undefined) {
                    // 使用 Screen Wake Lock API
                    val wakeLockPromise = navigator.wakeLock.request("screen")
                    // 使用 dynamic 类型避免类型推断问题
                    wakeLockPromise.then(
                        { lock: dynamic ->
                            wakeLock = lock
                            console.log("Screen Wake Lock 已激活")
                        },
                        { error: dynamic ->
                            console.warn("无法获取 Screen Wake Lock: ", error)
                        }
                    )
                } else {
                    console.warn("浏览器不支持 Screen Wake Lock API")
                }
            } else {
                // 释放 Wake Lock
                if (wakeLock != null && wakeLock != undefined) {
                    wakeLock.release()
                    wakeLock = null
                    console.log("Screen Wake Lock 已释放")
                }
            }
        } catch (e: Throwable) {
            // 静默失败 - 不影响核心功能
            console.warn("Screen Wake Lock 操作失败: ", e.message)
        }
    }
    
    /**
     * 检查平台是否支持系统级亮度控制
     * 
     * Web 平台不支持系统级亮度控制。
     * UI 层将使用透明度模拟模式（BrightnessControlMode.OPACITY）。
     * 
     * @return false 表示不支持系统级亮度控制
     */
    actual fun supportSystemBrightnessControl(): Boolean = false
}
