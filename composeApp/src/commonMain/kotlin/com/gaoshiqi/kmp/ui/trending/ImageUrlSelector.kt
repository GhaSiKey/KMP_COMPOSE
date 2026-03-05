package com.gaoshiqi.kmp.ui.trending

import com.gaoshiqi.kmp.data.model.Images

/**
 * 图片 URL 选择器
 * 根据优先级选择最佳的封面图片 URL
 */
object ImageUrlSelector {
    /**
     * 选择最佳的封面图片 URL
     * 优先级：common > medium > large > small > grid
     * 
     * @param images 图片对象
     * @return 选中的 URL，如果都为空则返回空字符串
     */
    fun selectBestUrl(images: Images): String {
        return images.common?.takeIf { it.isNotEmpty() }
            ?: images.medium?.takeIf { it.isNotEmpty() }
            ?: images.large?.takeIf { it.isNotEmpty() }
            ?: images.small?.takeIf { it.isNotEmpty() }
            ?: images.grid?.takeIf { it.isNotEmpty() }
            ?: ""
    }
}
