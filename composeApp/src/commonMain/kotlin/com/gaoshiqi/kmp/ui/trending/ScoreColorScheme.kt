package com.gaoshiqi.kmp.ui.trending

import androidx.compose.ui.graphics.Color

/**
 * 评分颜色方案
 * 根据评分高低使用不同颜色的视觉系统
 */
object ScoreColorScheme {
    /** 高分颜色 (>= 8.0) - 绿色 */
    val HighScore = Color(0xFF4CAF50)
    
    /** 中等分数颜色 (6.0 - 7.9) - 橙色 */
    val MediumScore = Color(0xFFFF9800)
    
    /** 低分颜色 (< 6.0) - 红色 */
    val LowScore = Color(0xFFF44336)
    
    /** 星形图标颜色 - 金色 */
    val StarIcon = Color(0xFFFFB300)
    
    /**
     * 根据评分获取颜色
     * @param score 评分值
     * @return 对应的颜色
     */
    fun getScoreColor(score: Double): Color {
        return when {
            score >= 8.0 -> HighScore
            score >= 6.0 -> MediumScore
            else -> LowScore
        }
    }
}
