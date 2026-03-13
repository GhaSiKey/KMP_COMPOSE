package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.gaoshiqi.kmp.shared.lenticular.RenderState
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.placeholder_anime
import org.jetbrains.compose.resources.painterResource

/**
 * 光栅卡渲染组件
 *
 * 根据 [RenderState] 显示当前图片，硬切无过渡，模拟物理光栅卡效果。
 *
 * @param renderState 当前渲染状态，包含要显示的图片索引
 * @param images 图片源列表（URI 字符串）
 * @param modifier Modifier
 */
@Composable
fun LenticularRenderer(
    renderState: RenderState,
    images: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalPlatformContext.current
    val errorPlaceholder = painterResource(Res.drawable.placeholder_anime)

    Box(modifier = modifier) {
        if (renderState.displayIndex in images.indices) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(images[renderState.displayIndex])
                    .build(),
                contentDescription = "光栅卡图片 ${renderState.displayIndex + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = errorPlaceholder
            )
        }
    }
}
