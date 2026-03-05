package com.gaoshiqi.kmp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gaoshiqi.kmp.data.model.TrendingSubjectItem
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.placeholder_anime
import org.jetbrains.compose.resources.painterResource
import com.gaoshiqi.kmp.data.model.bestUrl
import com.gaoshiqi.kmp.data.model.displayName
import com.gaoshiqi.kmp.data.model.formattedScore
import com.gaoshiqi.kmp.viewmodel.TrendingUiState
import com.gaoshiqi.kmp.viewmodel.TrendingViewModel

/**
 * 番剧列表项组件
 * 
 * @param item 番剧数据
 * @param onClick 点击回调
 */
@Composable
private fun TrendingSubjectCard(
    item: TrendingSubjectItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 悬停效果支持（桌面/Web 平台）
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val context = LocalPlatformContext.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 封面图片
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.subject.images.bestUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.subject.displayName,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.placeholder_anime),
                error = painterResource(Res.drawable.placeholder_anime)
            )
            
            // 番剧信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 名称
                Text(
                    text = item.subject.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 评分
                Text(
                    text = "评分: ${item.subject.rating.formattedScore}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // 排名
                Text(
                    text = "排名: #${item.subject.rating.rank}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // 热度
                Text(
                    text = "热度: ${item.count}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 加载指示器（首次加载）
 */
@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 错误视图（首次加载失败）
 * 
 * @param message 错误消息
 * @param onRetry 重试回调
 */
@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("重试")
            }
        }
    }
}

/**
 * 加载更多指示器（列表底部）
 */
@Composable
private fun LoadMoreIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * 加载更多错误提示（列表底部）
 * 
 * @param message 错误消息
 * @param onRetry 重试回调
 */
@Composable
private fun LoadMoreErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            TextButton(onClick = onRetry) {
                Text("重试")
            }
        }
    }
}

/**
 * 番剧排行榜页面
 * 
 * @param onBack 返回回调
 * @param onSubjectClick 番剧点击回调，传递番剧 ID
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingListScreen(
    onBack: () -> Unit,
    onSubjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrendingViewModel = viewModel { TrendingViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("番剧排行榜") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← 返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is TrendingUiState.Initial -> {
                // 初始状态，不显示任何内容
            }
            
            is TrendingUiState.Loading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            
            is TrendingUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is TrendingUiState.Success,
            is TrendingUiState.LoadingMore,
            is TrendingUiState.LoadMoreError -> {
                // 提取列表数据和状态信息
                val items = when (state) {
                    is TrendingUiState.Success -> state.items
                    is TrendingUiState.LoadingMore -> state.items
                    is TrendingUiState.LoadMoreError -> state.items
                    else -> emptyList()
                }
                
                val hasMore = (state as? TrendingUiState.Success)?.hasMore ?: false
                val isLoadingMore = state is TrendingUiState.LoadingMore
                val loadMoreError = (state as? TrendingUiState.LoadMoreError)?.message
                
                // 渲染列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 渲染番剧列表项
                    items(
                        items = items,
                        key = { it.subject.id },
                        contentType = { "TrendingSubject" }
                    ) { item ->
                        TrendingSubjectCard(
                            item = item,
                            onClick = { onSubjectClick(item.subject.id) }
                        )
                    }
                    
                    // 加载更多触发器
                    if (hasMore && !isLoadingMore && loadMoreError == null) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.loadMore()
                            }
                            LoadMoreIndicator()
                        }
                    }
                    
                    // 加载更多中指示器
                    if (isLoadingMore) {
                        item {
                            LoadMoreIndicator()
                        }
                    }
                    
                    // 加载更多错误提示
                    if (loadMoreError != null) {
                        item {
                            LoadMoreErrorView(
                                message = loadMoreError,
                                onRetry = { viewModel.loadMore() }
                            )
                        }
                    }
                }
            }
        }
    }
}
