package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.darwin.NSObject

/**
 * iOS 平台图片选择器实现
 *
 * 使用 PHPickerViewController（iOS 14+），这是 Apple 推荐的图片选择方式：
 * - 无需请求相册权限（系统级沙盒选择器）
 * - 用户可多选图片
 * - 返回的 NSItemProvider 需要异步加载图片数据
 *
 * 注意：PHPicker 返回的是临时文件 URL，需要在回调中及时读取。
 * 这里通过 loadFileRepresentationForTypeIdentifier 获取临时文件路径，
 * Coil 可以通过 file:// URI 直接加载。
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(
    maxItems: Int,
    onResult: (List<String>) -> Unit
): ImagePickerLauncher {
    return remember(maxItems, onResult) {
        IOSImagePickerLauncher(maxItems, onResult)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class IOSImagePickerLauncher(
    private val maxItems: Int,
    private val onResult: (List<String>) -> Unit
) : ImagePickerLauncher {

    override fun launch() {
        val configuration = PHPickerConfiguration().apply {
            selectionLimit = maxItems.toLong()
            filter = PHPickerFilter.imagesFilter
            selection = PHPickerConfigurationSelectionOrdered
        }

        val picker = PHPickerViewController(configuration = configuration)

        val delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, completion = null)

                val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                if (results.isEmpty()) {
                    onResult(emptyList())
                    return
                }

                val uris = mutableListOf<String>()
                var remaining = results.size

                results.forEach { result ->
                    val provider = result.itemProvider
                    // 加载图片文件到临时路径
                    provider.loadFileRepresentationForTypeIdentifier(
                        typeIdentifier = "public.image"
                    ) { url: NSURL?, _: platform.Foundation.NSError? ->
                        url?.absoluteString?.let { uris.add(it) }
                        remaining--
                        if (remaining == 0) {
                            // 所有图片加载完成，回调到主线程
                            platform.Foundation.NSOperationQueue.mainQueue.addOperationWithBlock {
                                onResult(uris.toList())
                            }
                        }
                    }
                }
            }
        }

        // 保持 delegate 强引用，避免被 ARC 回收
        picker.delegate = delegate

        // 获取当前可见的 ViewController 并 present picker
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, animated = true, completion = null)
    }
}
