package com.gaoshiqi.kmp.ui.trending

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gaoshiqi.kmp.data.model.TrendingSubjectItem
import com.gaoshiqi.kmp.data.model.displayName

/**
 * 纵向番剧卡片组件（用于平板/桌面端网格布局）
 * 
 * 设计特性：
 * - 卡片宽度：200dp
 * - 封面图片高度：280dp
 * - 圆角：16dp
 * - 浅色模式：2dp 阴影
 * - 深色模式：1dp 边框，移除阴影
 * - 布局：封面在上，信息在下垂直排列
 * - 排名徽章显示在封面左上角（8dp 内边距）
 * - 标题最多显示 2 行，超出显示省略号
 * - 条件显示 info 字段（非空时显示）
 * - 支持点击交互（涟漪效果）
 * 
 * @param item 番剧数据
 * @param onClick 点击回调
 * @param modifier Modifier
 */
@Composable
fun VerticalTrendingCard(
    item: TrendingSubjectItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Card(
        modifier = modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 2.dp
        ),
        border = if (isDarkTheme) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 封面图片容器（带排名徽章）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                CoverImage(
                    imageUrl = ImageUrlSelector.selectBestUrl(item.subject.images),
                    contentDescription = item.subject.displayName,
                    modifier = Modifier.fillMaxSize()
                )
                
                // 排名徽章（左上角）
                RankBadge(
                    rank = item.subject.rating.rank,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
            
            // 番剧信息
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
