package com.gaoshiqi.kmp.shared.flashlight

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

/**
 * JVM (Desktop) 平台的亮度偏好设置实现
 * 
 * 使用 Java Preferences API 存储亮度偏好
 */
class JvmBrightnessPreferences : BrightnessPreferences {
    
    private val preferences: Preferences = Preferences.userNodeForPackage(
        JvmBrightnessPreferences::class.java
    )
    
    override suspend fun saveBrightness(level: Float) = withContext(Dispatchers.IO) {
        preferences.putFloat(KEY_BRIGHTNESS, level)
        preferences.flush()
    }
    
    override suspend fun loadBrightness(): Float? = withContext(Dispatchers.IO) {
        val defaultValue = -1f
        val value = preferences.getFloat(KEY_BRIGHTNESS, defaultValue)
        if (value != defaultValue) {
            value
        } else {
            null
        }
    }
    
    companion object {
        private const val KEY_BRIGHTNESS = "brightness_level"
    }
}
