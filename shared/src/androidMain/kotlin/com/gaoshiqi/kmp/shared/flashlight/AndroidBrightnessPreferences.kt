package com.gaoshiqi.kmp.shared.flashlight

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android 平台的亮度偏好设置实现
 * 
 * 使用 SharedPreferences 存储亮度偏好
 */
class AndroidBrightnessPreferences(context: Context) : BrightnessPreferences {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    override suspend fun saveBrightness(level: Float) = withContext(Dispatchers.IO) {
        preferences.edit()
            .putFloat(KEY_BRIGHTNESS, level)
            .apply()
    }
    
    override suspend fun loadBrightness(): Float? = withContext(Dispatchers.IO) {
        if (preferences.contains(KEY_BRIGHTNESS)) {
            preferences.getFloat(KEY_BRIGHTNESS, BrightnessPreferences.DEFAULT_BRIGHTNESS)
        } else {
            null
        }
    }
    
    companion object {
        private const val PREFS_NAME = "flashlight_preferences"
        private const val KEY_BRIGHTNESS = "brightness_level"
    }
}
