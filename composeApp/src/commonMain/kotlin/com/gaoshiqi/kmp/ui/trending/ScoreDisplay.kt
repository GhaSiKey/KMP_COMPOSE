package com.gaoshiqi.kmp.ui.trending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gaoshiqi.kmp.video.format

/**
 * 评分显示组件
 * 显示番剧的评分和评分人数，使用颜色编码表示评分高低
 * 
 * @param score 评分
 * @param total 评分人数
 * @param modifier Modifier
 */
@Composable
fun ScoreDisplay(
    score: Double,
    total: Int,
    modifier: Modifier = Modifier
) {
    val scoreColor = ScoreColorScheme.getScoreColor(score)
    val formattedScore = String.format("%.1f", score)
    val formattedTotal = NumberFormatter.format(total)
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "评分",
            tint = ScoreColorScheme.StarIcon,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = formattedScore,
            style = MaterialTheme.typography.bodyMedium,
            color = scoreColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "($formattedTotal)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
