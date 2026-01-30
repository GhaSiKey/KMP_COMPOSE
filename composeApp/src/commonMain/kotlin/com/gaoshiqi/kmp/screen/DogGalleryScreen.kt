package com.gaoshiqi.kmp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gaoshiqi.kmp.data.api.DogApi
import com.gaoshiqi.kmp.getPlatform
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.ic_arrow_back
import kmp.composeapp.generated.resources.ic_refresh
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** 每次加载的图片数量 */
private const val PAGE_SIZE = 20

/** 距离底部多少项时触发预加载 */
private const val LOAD_MORE_THRESHOLD = 3

/** 图片卡片最小宽度（用于自适应列数计算） */
private val CARD_MIN_WIDTH = 160.dp

/** 内容区域最大宽度（避免超宽屏上图片过大） */
private val CONTENT_MAX_WIDTH = 1200.dp

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
    val platform = remember { getPlatform() }

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
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Desktop/Web 友好的刷新按钮
                    IconButton(
                        onClick = { scope.launch { loadImages(isRefresh = true) } },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_refresh),
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // 图片网格内容
        val gridContent: @Composable () -> Unit = {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = CARD_MIN_WIDTH),
                    state = gridState,
                    modifier = Modifier
                        .widthIn(max = CONTENT_MAX_WIDTH)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        count = dogImages.size,
                        key = { index -> "$index-${dogImages[index]}" }
                    ) { index ->
                        DogImageCard(dogImages[index])
                    }

                    if (isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
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

        // 根据平台决定是否使用下拉刷新
        if (platform.supportsPullToRefresh) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { scope.launch { loadImages(isRefresh = true) } },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                gridContent()
            }
        } else {
            // 桌面/网页：不使用下拉刷新
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                gridContent()
            }
        }
    }
}

/**
 * 单张狗狗图片卡片
 *
 * 使用 SubcomposeAsyncImage 支持自定义 loading/error 状态
 */
@Composable
private fun DogImageCard(imageUrl: String) {
    val context = LocalPlatformContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Dog image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            loading = {
                // 加载中：显示带背景色的 loading 指示器
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                // 加载失败：显示错误占位符
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        )
    }
}
