package com.gaoshiqi.kmp.ui.trending

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gaoshiqi.kmp.data.model.TrendingSubjectItem
import com.gaoshiqi.kmp.data.model.displayName

/**
 * 横向番剧卡片组件（用于手机端单列布局）
 * 
 * 设计特性：
 * - 封面图片尺寸：90dp x 120dp
 * - 圆角：12dp
 * - 浅色模式：2dp 阴影
 * - 深色模式：1dp 边框，移除阴影
 * - 布局：封面在左，信息在右垂直排列
 * - 标题最多显示 2 行，超出显示省略号
 * - 条件显示 info 字段（非空时显示）
 * - 支持点击交互（涟漪效果）
 * 
 * @param item 番剧数据
 * @param onClick 点击回调
 * @param modifier Modifier
 */
@Composable
fun HorizontalTrendingCard(
    item: TrendingSubjectItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 2.dp
        ),
        border = if (isDarkTheme) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 封面图片
            CoverImage(
                imageUrl = ImageUrlSelector.selectBestUrl(item.subject.images),
                contentDescription = item.subject.displayName,
                modifier = Modifier
                    .width(90.dp)
                    .height(120.dp)
            )
            
            // 番剧信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 排名徽章
                RankBadge(rank = item.subject.rating.rank)
                
                // 标题
                Text(
                    text = item.subject.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 评分
                ScoreDisplay(
                    score = item.subject.rating.score,
                    total = item.subject.rating.total
                )
                
                // 热度
                PopularityIndicator(count = item.count)
                
                // 简短信息（条件显示）
                if (item.subject.info.isNotEmpty()) {
                    Text(
                        text = item.subject.info,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
