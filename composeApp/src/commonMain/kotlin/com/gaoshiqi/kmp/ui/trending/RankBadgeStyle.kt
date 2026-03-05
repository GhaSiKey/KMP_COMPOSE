package com.gaoshiqi.kmp.ui.trending

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 排名徽章样式
 * 
 * @property backgroundColor 背景颜色
 * @property textColor 文字颜色
 * @property icon 图标（emoji）
 * @property elevation 阴影高度
 */
data class RankBadgeStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val icon: String,
    val elevation: Dp
)

/**
 * 排名徽章样式提供者
 * 根据排名返回对应的徽章样式
 */
object RankBadgeStyleProvider {
    /** 金牌样式 (第 1 名) */
    private val GoldMedal = RankBadgeStyle(
        backgroundColor = Color(0xFFFFD700),  // 金色
        textColor = Color.Black,
        icon = "🥇",
        elevation = 2.dp
    )
    
    /** 银牌样式 (第 2 名) */
    private val SilverMedal = RankBadgeStyle(
        backgroundColor = Color(0xFFC0C0C0),  // 银色
        textColor = Color.Black,
        icon = "🥈",
        elevation = 2.dp
    )
    
    /** 铜牌样式 (第 3 名) */
    private val BronzeMedal = RankBadgeStyle(
        backgroundColor = Color(0xFFCD7F32),  // 铜色
        textColor = Color.White,
        icon = "🥉",
        elevation = 2.dp
    )
    
    /**
     * 根据排名获取徽章样式
     * 
     * @param rank 排名
     * @param colorScheme Material3 颜色方案
     * @return 徽章样式
     */
    @Composable
    fun getStyle(rank: Int, colorScheme: ColorScheme = MaterialTheme.colorScheme): RankBadgeStyle {
        return when (rank) {
            1 -> GoldMedal
            2 -> SilverMedal
            3 -> BronzeMedal
            else -> RankBadgeStyle(
                backgroundColor = colorScheme.secondaryContainer,
                textColor = colorScheme.onSecondaryContainer,
                icon = "",
                elevation = 0.dp
            )
        }
    }
}
