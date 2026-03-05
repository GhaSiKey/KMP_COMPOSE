package com.gaoshiqi.kmp.ui.trending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 热度指示器组件
 * 显示番剧的热度信息，包括火焰图标、"热度"标签和格式化的热度数值
 * 
 * @param count 热度计数
 * @param modifier Modifier
 */
@Composable
fun PopularityIndicator(
    count: Int,
    modifier: Modifier = Modifier
) {
    val formattedCount = NumberFormatter.format(count)
    val popularityColor = Color(0xFFFF5722)  // 橙红色
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Whatshot,  // 火焰图标
            contentDescription = "热度",
            tint = popularityColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "热度",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formattedCount,
            style = MaterialTheme.typography.bodySmall,
            color = popularityColor,
            fontWeight = FontWeight.Medium
        )
    }
}
