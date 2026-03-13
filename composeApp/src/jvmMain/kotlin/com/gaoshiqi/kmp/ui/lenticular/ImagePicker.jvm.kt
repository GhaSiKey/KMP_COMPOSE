package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * JVM (Desktop) 平台图片选择器实现
 *
 * Desktop 平台暂不支持图片选择功能。
 * launch() 为空操作，不会触发回调。
 */
@Composable
actual fun rememberImagePickerLauncher(
    maxItems: Int,
    onResult: (List<String>) -> Unit
): ImagePickerLauncher {
    return remember { NoOpImagePickerLauncher }
}

private object NoOpImagePickerLauncher : ImagePickerLauncher {
    override fun launch() {
        // Desktop 平台不支持本地图片选择
    }
}
