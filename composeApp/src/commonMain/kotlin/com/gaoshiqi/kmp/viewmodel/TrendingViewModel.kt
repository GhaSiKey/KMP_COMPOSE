package com.gaoshiqi.kmp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaoshiqi.kmp.PlatformInfo
import com.gaoshiqi.kmp.data.model.TrendingSubjectItem
import com.gaoshiqi.kmp.data.repository.TrendingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 排行榜 UI 状态
 */
sealed class TrendingUiState {
    /** 初始状态 */
    data object Initial : TrendingUiState()
    
    /** 首次加载中 */
    data object Loading : TrendingUiState()
    
    /** 加载成功 */
    data class Success(
        val items: List<TrendingSubjectItem>,
        val total: Int,
        val hasMore: Boolean
    ) : TrendingUiState()
    
    /** 首次加载失败 */
    data class Error(val message: String) : TrendingUiState()
    
    /** 加载更多中 */
    data class LoadingMore(
        val items: List<TrendingSubjectItem>,
        val total: Int
    ) : TrendingUiState()
    
    /** 加载更多失败 */
    data class LoadMoreError(
        val items: List<TrendingSubjectItem>,
        val total: Int,
        val message: String
    ) : TrendingUiState()
}

/**
 * 热门番剧排行榜 ViewModel
 */
class TrendingViewModel(
    private val repository: TrendingRepository = TrendingRepository()
) : ViewModel() {
    
    /** UI 状态流 */
    private val _uiState = MutableStateFlow<TrendingUiState>(TrendingUiState.Initial)
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()
    
    /** 当前偏移量 */
    private var currentOffset: Int = 0
    
    /** 总数量 */
    private var totalCount: Int = 0
    
    /** 每页数量 */
    private val pageSize: Int = 10
    
    init {
        // 检查是否为 Web 平台
        if (PlatformInfo.isWeb) {
            // Web 平台由于 CORS 限制无法使用此功能
            _uiState.value = TrendingUiState.Error(
                message = "番剧排行榜功能暂不支持 Web 平台\n\n" +
                        "由于浏览器的跨域资源共享（CORS）限制，Web 版本无法直接访问 Bangumi API。\n\n" +
                        "请使用以下平台体验完整功能：\n" +
                        "• Android 应用\n" +
                        "• iOS 应用\n" +
                        "• Desktop 桌面应用"
            )
        } else {
            // 其他平台正常加载
            loadFirstPage()
        }
    }
    
    /**
     * 加载首页数据
     */
    fun loadFirstPage() {
        viewModelScope.launch {
            _uiState.value = TrendingUiState.Loading
            
            repository.getTrendingSubjects(offset = 0, limit = pageSize)
                .onSuccess { response ->
                    currentOffset = response.data.size
                    totalCount = response.total
                    
                    _uiState.value = TrendingUiState.Success(
                        items = response.data,
                        total = response.total,
                        hasMore = response.data.size < response.total
                    )
                }
                .onFailure { error ->
                    _uiState.value = TrendingUiState.Error(
                        message = error.message ?: "加载失败"
                    )
                }
        }
    }
    
    /**
     * 加载更多数据
     */
    fun loadMore() {
        // 检查是否还有更多数据
        if (currentOffset >= totalCount) {
            return
        }
        
        // 获取当前列表
        val currentState = _uiState.value
        val currentItems = when (currentState) {
            is TrendingUiState.Success -> currentState.items
            is TrendingUiState.LoadMoreError -> currentState.items
            else -> return
        }
        
        viewModelScope.launch {
            _uiState.value = TrendingUiState.LoadingMore(
                items = currentItems,
                total = totalCount
            )
            
            repository.getTrendingSubjects(offset = currentOffset, limit = pageSize)
                .onSuccess { response ->
                    currentOffset += response.data.size
                    
                    _uiState.value = TrendingUiState.Success(
                        items = currentItems + response.data,
                        total = response.total,
                        hasMore = currentOffset < response.total
                    )
                }
                .onFailure { error ->
                    _uiState.value = TrendingUiState.LoadMoreError(
                        items = currentItems,
                        total = totalCount,
                        message = error.message ?: "加载更多失败"
                    )
                }
        }
    }
    
    /**
     * 重试加载
     */
    fun retry() {
        loadFirstPage()
    }
    
    /**
     * 清理资源
     */
    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}
