package com.gaoshiqi.kmp.ui.flashlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaoshiqi.kmp.shared.flashlight.BrightnessControlMode
import com.gaoshiqi.kmp.shared.flashlight.BrightnessPreferences
import com.gaoshiqi.kmp.shared.flashlight.FlashlightController
import com.gaoshiqi.kmp.shared.flashlight.FlashlightUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 手电筒功能 ViewModel
 * 
 * 负责管理手电筒功能的状态和业务逻辑，包括：
 * - 亮度级别管理
 * - 控制面板可见性控制
 * - 亮度偏好持久化
 * - 系统亮度控制和恢复
 * - 屏幕常亮状态管理
 * - 错误处理和降级策略
 * 
 * @param controller 手电筒亮度控制器，负责平台特定的亮度控制
 * @param preferences 亮度偏好设置，负责保存和加载用户的亮度偏好
 */
class FlashlightViewModel(
    private val controller: FlashlightController,
    private val preferences: BrightnessPreferences
) : ViewModel() {
    
    /**
     * UI 状态流
     * 
     * 包含手电筒界面的所有状态信息：
     * - brightness: 当前亮度级别（0.0 - 1.0）
     * - isControlPanelVisible: 控制面板是否可见
     * - brightnessControlMode: 亮度控制模式（SYSTEM 或 OPACITY）
     * - errorMessage: 错误信息（如权限被拒绝）
     */
    private val _uiState = MutableStateFlow(FlashlightUiState())
    val uiState: StateFlow<FlashlightUiState> = _uiState.asStateFlow()
    
    /**
     * 初始化手电筒功能
     * 
     * 执行以下操作：
     * 1. 检测平台是否支持系统级亮度控制
     * 2. 加载用户保存的亮度偏好（如果没有则使用默认值 0.8）
     * 3. 设置屏幕亮度
     * 4. 启用屏幕常亮
     * 
     * 如果平台不支持系统级亮度控制，自动切换到透明度模拟模式。
     */
    fun initialize() {
        viewModelScope.launch {
            try {
                // 检测平台是否支持系统级亮度控制
                val controlMode = if (controller.supportSystemBrightnessControl()) {
                    BrightnessControlMode.SYSTEM
                } else {
                    BrightnessControlMode.OPACITY
                }
                
                // 加载保存的亮度值，如果没有则使用默认值
                val savedBrightness = try {
                    preferences.loadBrightness() ?: BrightnessPreferences.DEFAULT_BRIGHTNESS
                } catch (e: Exception) {
                    // 加载失败时使用默认值
                    logError("加载亮度偏好失败", e)
                    BrightnessPreferences.DEFAULT_BRIGHTNESS
                }
                
                // 更新 UI 状态
                _uiState.update { 
                    it.copy(
                        brightness = savedBrightness,
                        brightnessControlMode = controlMode
                    )
                }
                
                // 如果支持系统级亮度控制，设置亮度
                if (controlMode == BrightnessControlMode.SYSTEM) {
                    val result = controller.setBrightness(savedBrightness)
                    result.onFailure { error ->
                        // 设置亮度失败，切换到透明度模拟模式
                        handleBrightnessControlError(error)
                    }
                }
                
                // 启用屏幕常亮
                controller.keepScreenOn(true)
                
                logInfo("手电筒已启动，亮度: $savedBrightness, 模式: $controlMode")
                
            } catch (e: Exception) {
                // 初始化失败，记录错误
                logError("初始化手电筒失败", e)
                _uiState.update { 
                    it.copy(errorMessage = "初始化失败: ${e.message}")
                }
            }
        }
    }
    
    /**
     * 设置亮度级别
     * 
     * 执行以下操作：
     * 1. 验证亮度值范围（0.0 - 1.0）
     * 2. 调用 Controller 设置系统亮度（如果支持）
     * 3. 保存亮度值到 Preferences
     * 4. 更新 UI 状态
     * 
     * 如果设置系统亮度失败（如权限被拒绝），自动切换到透明度模拟模式。
     * 
     * @param level 亮度级别，范围 0.0（最暗）到 1.0（最亮）
     */
    fun setBrightness(level: Float) {
        viewModelScope.launch {
            try {
                // 验证亮度范围
                if (level !in 0.0f..1.0f) {
                    logWarn("亮度级别超出范围: $level")
                    return@launch
                }
                
                // 如果当前使用系统级亮度控制，尝试设置系统亮度
                if (_uiState.value.brightnessControlMode == BrightnessControlMode.SYSTEM) {
                    val result = controller.setBrightness(level)
                    result.onFailure { error ->
                        // 设置失败，切换到透明度模拟模式
                        handleBrightnessControlError(error)
                    }
                }
                
                // 保存亮度值到 Preferences
                try {
                    preferences.saveBrightness(level)
                } catch (e: Exception) {
                    // 保存失败，记录错误但不影响功能
                    logError("保存亮度偏好失败", e)
                }
                
                // 更新 UI 状态
                _uiState.update { 
                    it.copy(brightness = level)
                }
                
                logInfo("亮度已更新: $level")
                
            } catch (e: Exception) {
                logError("设置亮度失败", e)
            }
        }
    }
    
    /**
     * 显示控制面板
     * 
     * 将控制面板设置为可见状态。
     * 通常在用户点击屏幕时调用。
     */
    fun showControlPanel() {
        _uiState.update { 
            it.copy(isControlPanelVisible = true)
        }
    }
    
    /**
     * 隐藏控制面板
     * 
     * 将控制面板设置为隐藏状态。
     * 通常在 3 秒无操作后自动调用。
     */
    fun hideControlPanel() {
        _uiState.update { 
            it.copy(isControlPanelVisible = false)
        }
    }
    
    /**
     * 退出手电筒模式
     * 
     * 执行清理操作：
     * 1. 恢复系统原始亮度设置
     * 2. 禁用屏幕常亮
     * 
     * 使用 try-finally 确保清理代码始终执行，即使恢复亮度失败。
     */
    fun exitFlashlight() {
        try {
            // 恢复系统原始亮度
            controller.restoreSystemBrightness()
            logInfo("系统亮度已恢复")
        } catch (e: Exception) {
            // 恢复亮度失败，记录错误但继续执行清理
            logError("恢复系统亮度失败", e)
        } finally {
            // 确保禁用屏幕常亮
            try {
                controller.keepScreenOn(false)
                logInfo("屏幕常亮已禁用")
            } catch (e: Exception) {
                logError("禁用屏幕常亮失败", e)
            }
        }
    }
    
    /**
     * 处理亮度控制错误
     * 
     * 当系统级亮度控制失败时（如权限被拒绝），切换到透明度模拟模式。
     * 
     * @param error 错误对象
     */
    private fun handleBrightnessControlError(error: Throwable) {
        // 检查错误消息中是否包含权限相关的关键词
        val errorMessage = if (error.message?.contains("permission", ignoreCase = true) == true ||
                              error.message?.contains("security", ignoreCase = true) == true) {
            "无法调节系统亮度，将使用模拟模式"
        } else {
            "亮度控制失败，将使用模拟模式"
        }
        
        logWarn("亮度控制错误，切换到透明度模拟模式: ${error.message}")
        
        _uiState.update { 
            it.copy(
                brightnessControlMode = BrightnessControlMode.OPACITY,
                errorMessage = errorMessage
            )
        }
    }
    
    /**
     * 日志记录辅助方法
     */
    private fun logInfo(message: String) {
        println("[INFO] FlashlightViewModel: $message")
    }
    
    private fun logWarn(message: String) {
        println("[WARN] FlashlightViewModel: $message")
    }
    
    private fun logError(message: String, error: Throwable? = null) {
        println("[ERROR] FlashlightViewModel: $message${error?.let { " - ${it.message}" } ?: ""}")
        error?.printStackTrace()
    }
    
    /**
     * 清理资源
     * 
     * 在 ViewModel 被销毁时调用，确保恢复系统状态。
     */
    override fun onCleared() {
        super.onCleared()
        exitFlashlight()
    }
}
