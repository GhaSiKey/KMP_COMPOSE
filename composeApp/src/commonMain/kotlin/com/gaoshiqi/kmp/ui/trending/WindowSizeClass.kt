package com.gaoshiqi.kmp.ui.trending

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp

/**
 * 窗口尺寸分类
 */
enum class WindowSizeClass {
    /** 紧凑型 (< 600dp) - 手机竖屏 */
    COMPACT,
    
    /** 中等型 (600dp - 839dp) - 手机横屏、小平板 */
    MEDIUM,
    
    /** 扩展型 (>= 840dp) - 大平板、桌面 */
    EXPANDED
}

/**
 * 根据屏幕宽度计算窗口尺寸分类
 * @param widthDp 屏幕宽度（dp）
 * @return 窗口尺寸分类
 */
fun calculateWindowSizeClass(widthDp: Int): WindowSizeClass {
    return when {
        widthDp < 600 -> WindowSizeClass.COMPACT
        widthDp < 840 -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

/**
 * 根据屏幕宽度计算网格列数
 * @param widthDp 屏幕宽度（dp）
 * @return 网格列数（1-4）
 */
fun getGridColumns(widthDp: Int): Int {
    return when {
        widthDp < 600 -> 1
        widthDp < 840 -> 2
        widthDp < 1200 -> 3
        else -> 4
    }
}

/**
 * 获取当前窗口宽度（跨平台）
 * 平台特定实现
 */
@Composable
expect fun rememberWindowWidth(): Dp

/**
 * 记住当前窗口尺寸分类
 * 当配置变化时自动重新计算
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val widthDp = rememberWindowWidth()
    return remember(widthDp) {
        calculateWindowSizeClass(widthDp.value.toInt())
    }
}
