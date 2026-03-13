package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable

/**
 * 跨平台图片选择器
 *
 * 各平台通过 expect/actual 机制提供具体实现：
 * - Android: ActivityResultContracts.PickMultipleVisualMedia
 * - iOS: PHPickerViewController
 * - JS/JVM: 不支持（onPickImages 不会被调用）
 *
 * 使用方式：
 * ```
 * val launcher = rememberImagePickerLauncher(maxItems = 5) { uris ->
 *     viewModel.addImages(uris)
 * }
 * Button(onClick = { launcher.launch() }) { Text("选择图片") }
 * ```
 */
interface ImagePickerLauncher {
    /** 启动图片选择器 */
    fun launch()
}

/**
 * 创建并记住一个图片选择器 Launcher
 *
 * @param maxItems 最大可选图片数量
 * @param onResult 选择完成后的回调，参数为图片 URI 字符串列表（可能为空，表示用户取消选择）
 * @return ImagePickerLauncher 实例，调用 launch() 启动选择器
 */
@Composable
expect fun rememberImagePickerLauncher(
    maxItems: Int,
    onResult: (List<String>) -> Unit
): ImagePickerLauncher
