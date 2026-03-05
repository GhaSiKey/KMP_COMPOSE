package com.gaoshiqi.kmp.shared.flashlight

/**
 * 亮度偏好设置接口
 * 
 * 负责保存和加载用户的亮度偏好设置，确保用户下次启动手电筒时能恢复上次的亮度级别。
 */
interface BrightnessPreferences {
    
    /**
     * 保存亮度级别到本地存储
     * 
     * @param level 亮度级别，范围 0.0 到 1.0
     */
    suspend fun saveBrightness(level: Float)
    
    /**
     * 从本地存储加载亮度级别
     * 
     * @return 保存的亮度级别，如果没有保存过则返回 null
     */
    suspend fun loadBrightness(): Float?
    
    companion object {
        /**
         * 默认亮度级别
         * 
         * 当本地存储中没有保存的亮度值时使用此默认值
         */
        const val DEFAULT_BRIGHTNESS = 0.8f
    }
}
