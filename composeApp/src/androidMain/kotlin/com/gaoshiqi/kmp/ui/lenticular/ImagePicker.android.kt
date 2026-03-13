package com.gaoshiqi.kmp.ui.lenticular

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Android 平台图片选择器实现
 *
 * 使用 Android Photo Picker（PickMultipleVisualMedia），这是 Android 推荐的图片选择方式：
 * - 无需 READ_EXTERNAL_STORAGE 权限
 * - 系统级 UI，体验统一
 * - 支持 Android 11+ 原生，低版本通过 Google Play Services 兼容
 *
 * 返回的 content:// URI 由系统授予临时读取权限，Coil 可直接加载。
 */
@Composable
actual fun rememberImagePickerLauncher(
    maxItems: Int,
    onResult: (List<String>) -> Unit
): ImagePickerLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems)
    ) { uris ->
        onResult(uris.map { it.toString() })
    }

    return remember(launcher) {
        object : ImagePickerLauncher {
            override fun launch() {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }
}
