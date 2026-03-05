package com.gaoshiqi.kmp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gaoshiqi.kmp.ui.trending.*
import com.gaoshiqi.kmp.viewmodel.TrendingUiState
import com.gaoshiqi.kmp.viewmodel.TrendingViewModel

/**
 * 加载指示器（首次加载）
 * 
 * 设计规范：
 * - 使用 48dp 尺寸的圆形进度指示器
 * - 使用 Material3 主要颜色
 * - 在进度指示器下方显示"加载中..."文本
 * - 居中显示
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
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
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
 * 设计规范：
 * - 使用 64dp 尺寸的错误图标
 * - 使用 Material3 错误颜色
 * - 显示错误消息文本
 * - 显示"重试"按钮
 * - 居中显示
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
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "错误",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
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
 * 
 * 设计规范：
 * - 使用 24dp 尺寸和 2dp 线宽的圆形进度指示器
 * - 显示"加载更多..."文本
 * - 在列表底部显示
 */
@Composable
private fun LoadMoreIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            Text(
                text = "加载更多...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
 * 响应式布局特性：
 * - COMPACT (< 600dp): LazyColumn + HorizontalTrendingCard
 * - MEDIUM/EXPANDED (>= 600dp): LazyVerticalGrid + VerticalTrendingCard
 * - 网格列数：1-4 列（根据屏幕宽度自动计算）
 * - 统计信息栏显示总数
 * - 列表项使用稳定的 key（番剧 ID）
 * - 列表项指定 contentType 优化性能
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
    val windowSizeClass = rememberWindowSizeClass()
    val screenWidthDp = rememberWindowWidth().value.toInt()
    val gridColumns = getGridColumns(screenWidthDp)
    
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
                
                // 根据窗口尺寸选择布局
                if (windowSizeClass == WindowSizeClass.COMPACT) {
                    // 手机端：单列列表布局
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // 统计信息栏
                        item(key = "statistics_bar", contentType = "StatisticsBar") {
                            StatisticsBar(total = items.size)
                        }
                        
                        // 渲染番剧列表项
                        items(
                            items = items,
                            key = { it.subject.id },
                            contentType = { "HorizontalTrendingCard" }
                        ) { item ->
                            HorizontalTrendingCard(
                                item = item,
                                onClick = { onSubjectClick(item.subject.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        // 加载更多触发器
                        if (hasMore && !isLoadingMore && loadMoreError == null) {
                            item(key = "load_more_trigger", contentType = "LoadMoreTrigger") {
                                LaunchedEffect(Unit) {
                                    viewModel.loadMore()
                                }
                                LoadMoreIndicator()
                            }
                        }
                        
                        // 加载更多中指示器
                        if (isLoadingMore) {
                            item(key = "loading_more", contentType = "LoadMoreIndicator") {
                                LoadMoreIndicator()
                            }
                        }
                        
                        // 加载更多错误提示
                        if (loadMoreError != null) {
                            item(key = "load_more_error", contentType = "LoadMoreError") {
                                LoadMoreErrorView(
                                    message = loadMoreError,
                                    onRetry = { viewModel.loadMore() }
                                )
                            }
                        }
                    }
                } else {
                    // 平板/桌面端：多列网格布局
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridColumns),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 统计信息栏（跨所有列）
                        item(
                            key = "statistics_bar",
                            span = { androidx.compose.foundation.lazy.grid.GridItemSpan(gridColumns) },
                            contentType = "StatisticsBar"
                        ) {
                            StatisticsBar(total = items.size)
                        }
                        
                        // 渲染番剧列表项
                        items(
                            items = items,
                            key = { it.subject.id },
                            contentType = { "VerticalTrendingCard" }
                        ) { item ->
                            VerticalTrendingCard(
                                item = item,
                                onClick = { onSubjectClick(item.subject.id) }
                            )
                        }
                        
                        // 加载更多触发器（跨所有列）
                        if (hasMore && !isLoadingMore && loadMoreError == null) {
                            item(
                                key = "load_more_trigger",
                                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(gridColumns) },
                                contentType = "LoadMoreTrigger"
                            ) {
                                LaunchedEffect(Unit) {
                                    viewModel.loadMore()
                                }
                                LoadMoreIndicator()
                            }
                        }
                        
                        // 加载更多中指示器（跨所有列）
                        if (isLoadingMore) {
                            item(
                                key = "loading_more",
                                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(gridColumns) },
                                contentType = "LoadMoreIndicator"
                            ) {
                                LoadMoreIndicator()
                            }
                        }
                        
                        // 加载更多错误提示（跨所有列）
                        if (loadMoreError != null) {
                            item(
                                key = "load_more_error",
                                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(gridColumns) },
                                contentType = "LoadMoreError"
                            ) {
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
}
