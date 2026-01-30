package com.gaoshiqi.kmp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gaoshiqi.kmp.data.api.DogApi
import kotlinx.coroutines.launch

/** 每次加载的图片数量 */
private const val PAGE_SIZE = 20

/** 距离底部多少项时触发预加载 */
private const val LOAD_MORE_THRESHOLD = 3

/**
 * 狗狗图片画廊页 - 无限流展示
 *
 * 功能：
 * - 下拉刷新：清空列表并重新加载
 * - 上拉加载：滚动到底部自动追加更多图片
 * - 2 列网格布局
 *
 * @param onBack 返回上一页的回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogGalleryScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val dogApi = remember { DogApi() }

    // 图片列表状态
    var dogImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasMoreData by remember { mutableStateOf(true) }

    // Snackbar 状态（用于显示错误 Toast）
    val snackbarHostState = remember { SnackbarHostState() }

    // Grid 滚动状态
    val gridState = rememberLazyGridState()

    /**
     * 加载图片
     * @param isRefresh true = 刷新（清空后重新加载），false = 追加加载
     */
    suspend fun loadImages(isRefresh: Boolean) {
        if (isRefresh) {
            isRefreshing = true
            hasMoreData = true
        } else {
            if (isLoadingMore || !hasMoreData) return
            isLoadingMore = true
        }
        errorMessage = null

        dogApi.getRandomImages(count = PAGE_SIZE).fold(
            onSuccess = { newImages ->
                dogImages = if (isRefresh) {
                    newImages
                } else {
                    dogImages + newImages
                }
                // Dog CEO API 总是返回请求数量的图片，这里假设可以无限加载
                // 如果需要真实分页，应由服务端返回 hasMore 标志
            },
            onFailure = { error ->
                errorMessage = error.message ?: "加载失败"
            }
        )

        if (isRefresh) {
            isRefreshing = false
        } else {
            isLoadingMore = false
        }
    }

    // 初始加载
    LaunchedEffect(Unit) {
        loadImages(isRefresh = true)
    }

    // 错误时显示 Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            snackbarHostState.showSnackbar(message = "错误: $error")
            errorMessage = null // 显示后清除，避免重复弹出
        }
    }

    // 监听滚动位置，接近底部时自动加载更多
    // 使用 derivedStateOf 计算是否接近底部，避免每帧重组
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            if (totalItemsCount == 0) return@derivedStateOf false

            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            // 判断是否接近底部
            lastVisibleItemIndex >= totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }

    // 当 shouldLoadMore 变为 true 时触发加载
    LaunchedEffect(shouldLoadMore, dogImages.size) {
        if (shouldLoadMore && !isRefreshing && !isLoadingMore && dogImages.isNotEmpty()) {
            loadImages(isRefresh = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dog Gallery") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("< 返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch { loadImages(isRefresh = true) }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 图片网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = dogImages.size,
                    key = { index -> "$index-${dogImages[index]}" } // index + URL 避免重复
                ) { index ->
                    DogImageCard(dogImages[index])
                }

                // 底部加载指示器
                if (isLoadingMore) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 单张狗狗图片卡片
 */
@Composable
private fun DogImageCard(imageUrl: String) {
    val context = LocalPlatformContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Dog image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
