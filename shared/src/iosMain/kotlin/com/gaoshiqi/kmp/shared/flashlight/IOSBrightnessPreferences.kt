package com.gaoshiqi.kmp.shared.flashlight

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

/**
 * iOS 平台的亮度偏好设置实现
 * 
 * 使用 UserDefaults 存储亮度偏好
 */
class IOSBrightnessPreferences : BrightnessPreferences {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    override suspend fun saveBrightness(level: Float) {
        withContext(Dispatchers.Default) {
            userDefaults.setFloat(level, KEY_BRIGHTNESS)
            userDefaults.synchronize()
        }
    }
    
    override suspend fun loadBrightness(): Float? = withContext(Dispatchers.Default) {
        if (userDefaults.objectForKey(KEY_BRIGHTNESS) != null) {
            userDefaults.floatForKey(KEY_BRIGHTNESS)
        } else {
            null
        }
    }
    
    companion object {
        private const val KEY_BRIGHTNESS = "brightness_level"
    }
}
