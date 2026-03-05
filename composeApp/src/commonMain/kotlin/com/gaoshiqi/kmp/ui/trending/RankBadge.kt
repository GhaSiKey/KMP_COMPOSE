package com.gaoshiqi.kmp.ui.trending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 排名徽章组件
 * 显示番剧的排名，前三名使用特殊的金银铜牌样式
 * 
 * @param rank 排名
 * @param modifier Modifier
 */
@Composable
fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier
) {
    val style = RankBadgeStyleProvider.getStyle(rank)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = style.backgroundColor,
        shadowElevation = style.elevation
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (style.icon.isNotEmpty()) {
                Text(
                    text = style.icon,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelMedium,
                color = style.textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
