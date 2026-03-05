package com.gaoshiqi.kmp.shared.flashlight

import android.app.Activity
import android.view.WindowManager
import java.lang.ref.WeakReference

/**
 * Android 平台的手电筒亮度控制器实现
 * 
 * 使用 WindowManager.LayoutParams.screenBrightness 控制屏幕亮度。
 * 注意：此实现仅影响应用窗口的亮度，不会改变系统全局亮度设置。
 * 
 * @param activity Activity 的弱引用，用于访问 Window 对象
 */
actual class FlashlightController(activity: Activity) {
    
    private val activityRef = WeakReference(activity)
    private var originalBrightness: Float = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    
    init {
        // 保存原始亮度值
        activityRef.get()?.window?.attributes?.let { params ->
            originalBrightness = params.screenBrightness
        }
    }
    
    /**
     * 设置屏幕亮度
     * 
     * 通过修改 Window 的 LayoutParams.screenBrightness 属性来调节亮度。
     * 
     * @param level 亮度级别，范围 0.0（最暗）到 1.0（最亮）
     * @return Result<Unit> 成功返回 Result.success，失败返回 Result.failure
     */
    actual fun setBrightness(level: Float): Result<Unit> {
        return try {
            val activity = activityRef.get()
                ?: return Result.failure(IllegalStateException("Activity 已被回收"))
            
            // 验证亮度范围
            if (level !in 0.0f..1.0f) {
                return Result.failure(IllegalArgumentException("亮度级别必须在 0.0 到 1.0 之间，当前值: $level"))
            }
            
            // 设置窗口亮度
            activity.runOnUiThread {
                val window = activity.window
                val params = window.attributes
                params.screenBrightness = level
                window.attributes = params
            }
            
            Result.success(Unit)
        } catch (e: SecurityException) {
            // 权限被拒绝
            Result.failure(e)
        } catch (e: Exception) {
            // 其他异常
            Result.failure(e)
        }
    }
    
    /**
     * 恢复系统原始亮度设置
     * 
     * 将窗口亮度恢复到启动手电筒前的状态。
     * 如果原始亮度为 BRIGHTNESS_OVERRIDE_NONE，则恢复为系统自动亮度。
     */
    actual fun restoreSystemBrightness() {
        try {
            val activity = activityRef.get() ?: return
            
            activity.runOnUiThread {
                val window = activity.window
                val params = window.attributes
                params.screenBrightness = originalBrightness
                window.attributes = params
            }
        } catch (e: Exception) {
            // 记录错误但不抛出异常，确保清理流程继续执行
            e.printStackTrace()
        }
    }
    
    /**
     * 控制屏幕常亮状态
     * 
     * 使用 FLAG_KEEP_SCREEN_ON 标志防止屏幕自动休眠。
     * 
     * @param enabled true 表示保持屏幕常亮，false 表示允许屏幕休眠
     */
    actual fun keepScreenOn(enabled: Boolean) {
        try {
            val activity = activityRef.get() ?: return
            
            activity.runOnUiThread {
                val window = activity.window
                if (enabled) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        } catch (e: Exception) {
            // 记录错误但不抛出异常
            e.printStackTrace()
        }
    }
    
    /**
     * 检查平台是否支持系统级亮度控制
     * 
     * Android 平台支持通过 WindowManager.LayoutParams 控制窗口亮度。
     * 
     * @return true 表示支持系统级亮度控制
     */
    actual fun supportSystemBrightnessControl(): Boolean = true
}
