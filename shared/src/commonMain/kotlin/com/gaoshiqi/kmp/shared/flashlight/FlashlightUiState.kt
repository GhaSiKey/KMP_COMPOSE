package com.gaoshiqi.kmp.shared.flashlight

/**
 * 手电筒 UI 状态
 * 
 * 包含手电筒界面的所有状态信息
 */
data class FlashlightUiState(
    /**
     * 当前亮度级别，范围 0.0 - 1.0
     */
    val brightness: Float = 0.8f,
    
    /**
     * 控制面板是否可见
     */
    val isControlPanelVisible: Boolean = true,
    
    /**
     * 亮度控制模式
     * - SYSTEM: 使用系统 API 控制真实亮度
     * - OPACITY: 通过调整白色透明度模拟亮度
     */
    val brightnessControlMode: BrightnessControlMode = BrightnessControlMode.SYSTEM,
    
    /**
     * 错误信息（如权限被拒绝）
     * null 表示没有错误
     */
    val errorMessage: String? = null
)
