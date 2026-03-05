package com.gaoshiqi.kmp.shared.flashlight

/**
 * 亮度控制模式
 */
enum class BrightnessControlMode {
    /**
     * 系统级亮度控制
     * 使用平台原生 API 直接调节屏幕亮度
     * 支持平台：Android、iOS
     */
    SYSTEM,
    
    /**
     * 透明度模拟模式
     * 通过调整白色覆盖层的不透明度来模拟亮度变化
     * 支持平台：Desktop (JVM)、Web (JS)
     */
    OPACITY
}
