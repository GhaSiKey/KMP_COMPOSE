package com.gaoshiqi.kmp.shared.flashlight

import kotlinx.browser.localStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * JS (Web) 平台的亮度偏好设置实现
 * 
 * 使用 localStorage 存储亮度偏好
 */
class JsBrightnessPreferences : BrightnessPreferences {
    
    override suspend fun saveBrightness(level: Float) = withContext(Dispatchers.Default) {
        localStorage.setItem(KEY_BRIGHTNESS, level.toString())
    }
    
    override suspend fun loadBrightness(): Float? = withContext(Dispatchers.Default) {
        val value = localStorage.getItem(KEY_BRIGHTNESS)
        value?.toFloatOrNull()
    }
    
    companion object {
        private const val KEY_BRIGHTNESS = "flashlight_brightness_level"
    }
}
