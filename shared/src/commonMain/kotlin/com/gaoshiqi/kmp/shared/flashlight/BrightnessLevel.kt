package com.gaoshiqi.kmp.shared.flashlight

/**
 * 亮度级别值对象，封装亮度值验证逻辑
 * 
 * 亮度值范围为 0.0（最暗）到 1.0（最亮）
 */
data class BrightnessLevel(val value: Float) {
    init {
        require(value in 0.0f..1.0f) {
            "Brightness level must be between 0.0 and 1.0, got $value"
        }
    }
    
    companion object {
        /** 最小亮度（完全暗） */
        val MIN = BrightnessLevel(0.0f)
        
        /** 最大亮度（完全亮） */
        val MAX = BrightnessLevel(1.0f)
        
        /** 默认亮度（80%） */
        val DEFAULT = BrightnessLevel(0.8f)
    }
}
