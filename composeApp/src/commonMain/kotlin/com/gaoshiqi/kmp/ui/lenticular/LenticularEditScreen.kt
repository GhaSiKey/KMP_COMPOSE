package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.gaoshiqi.kmp.shared.lenticular.SensingAxis
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.placeholder_anime
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

/**
 * 光栅卡编辑界面
 *
 * 提供图片管理（添加、删除）、感应轴向切换、灵敏度滑块、模式标签显示和预览入口。
 * 内部集成跨平台图片选择器，通过 expect/actual 机制在各平台调用原生图片选择 UI。
 *
 * @param viewModel 光栅卡 ViewModel
 * @param onPreview 点击预览按钮的回调
 * @param onBack 点击返回按钮的回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LenticularEditScreen(
    viewModel: LenticularViewModel,
    onPreview: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 计算当前还能选择多少张图片
    val remainingSlots = LenticularViewModel.MAX_IMAGE_COUNT - uiState.imageCount

    // 图片选择器：选择完成后自动添加到 ViewModel
    val imagePickerLauncher = rememberImagePickerLauncher(
        maxItems = remainingSlots.coerceAtLeast(1)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("光栅卡")
                        if (uiState.imageCount >= 2) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = uiState.modeLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // 图片网格区域
            ImageGridSection(
                images = uiState.images,
                showAddButton = uiState.imageCount < LenticularViewModel.MAX_IMAGE_COUNT,
                onAddClick = { imagePickerLauncher.launch() },
                onRemoveClick = { index -> viewModel.removeImage(index) }
            )

            Spacer(Modifier.height(24.dp))

            // 感应轴向切换
            AxisSelectionSection(
                selectedAxis = uiState.sensingAxis,
                onAxisChange = { viewModel.setSensingAxis(it) }
            )

            Spacer(Modifier.height(20.dp))

            // 切换间隔滑块
            DegreesPerImageSliderSection(
                degreesPerImage = uiState.degreesPerImage,
                onDegreesPerImageChange = { viewModel.setDegreesPerImage(it) }
            )

            Spacer(Modifier.height(20.dp))

            // 调试小球开关
            TiltDebugToggleSection(
                checked = uiState.showTiltDebug,
                onToggle = { viewModel.toggleTiltDebug() }
            )

            Spacer(Modifier.height(32.dp))

            // 预览按钮
            PreviewButtonSection(
                canPreview = uiState.canPreview,
                isSensorAvailable = uiState.isSensorAvailable,
                onPreview = onPreview
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ==================== 图片网格区域 ====================

/**
 * 图片网格区域
 *
 * 以网格形式显示已添加的图片缩略图，支持删除和添加操作。
 * 图片数量达到 10 张时隐藏添加按钮。
 */
@Composable
private fun ImageGridSection(
    images: List<String>,
    showAddButton: Boolean,
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    Text(
        text = "图片 (${images.size}/10)",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(Modifier.height(8.dp))

    // 计算网格项数量（图片 + 可选的添加按钮）
    val itemCount = images.size + if (showAddButton) 1 else 0

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        // 固定高度避免嵌套滚动冲突
        modifier = Modifier
            .fillMaxWidth()
            .height(((itemCount + 3) / 4 * 100).dp)
    ) {
        // 已添加的图片
        itemsIndexed(images) { index, uri ->
            ImageThumbnail(
                uri = uri,
                onRemove = { onRemoveClick(index) }
            )
        }

        // 添加图片按钮
        if (showAddButton) {
            item {
                AddImageButton(onClick = onAddClick)
            }
        }
    }
}

/**
 * 图片缩略图
 *
 * 显示图片缩略图，右上角有删除按钮。
 */
@Composable
private fun ImageThumbnail(
    uri: String,
    onRemove: () -> Unit
) {
    val context = LocalPlatformContext.current
    val errorPlaceholder = painterResource(Res.drawable.placeholder_anime)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .build(),
            contentDescription = "光栅卡图片",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = errorPlaceholder
        )

        // 删除按钮（右上角）
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "删除图片",
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = CircleShape
                    ),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 添加图片按钮
 *
 * 显示加号图标的占位格子，点击后触发图片选择。
 */
@Composable
private fun AddImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "添加图片",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
    }
}

// ==================== 感应轴向切换 ====================

/**
 * 感应轴向选择区域
 *
 * 使用 SegmentedButton 在水平轴和垂直轴之间切换。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AxisSelectionSection(
    selectedAxis: SensingAxis,
    onAxisChange: (SensingAxis) -> Unit
) {
    Text(
        text = "感应轴向",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(Modifier.height(8.dp))

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SensingAxis.entries.forEachIndexed { index, axis ->
            SegmentedButton(
                selected = selectedAxis == axis,
                onClick = { onAxisChange(axis) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = SensingAxis.entries.size
                )
            ) {
                Text(
                    text = when (axis) {
                        SensingAxis.VERTICAL -> "垂直（前后倾斜）"
                        SensingAxis.HORIZONTAL -> "水平（左右倾斜）"
                    }
                )
            }
        }
    }
}

// ==================== 切换间隔滑块 ====================

/**
 * 每张图角度间隔滑块
 *
 * 控制设备每倾斜多少度切换到下一张图片（5°~30°）。
 * 值越小切换越灵敏，值越大需要更大倾斜才切换。
 */
@Composable
private fun DegreesPerImageSliderSection(
    degreesPerImage: Float,
    onDegreesPerImageChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "切换间隔",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${degreesPerImage.roundToInt()}°/张",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    Slider(
        value = degreesPerImage,
        onValueChange = onDegreesPerImageChange,
        valueRange = LenticularViewModel.MIN_DEGREES_PER_IMAGE..LenticularViewModel.MAX_DEGREES_PER_IMAGE,
        modifier = Modifier.fillMaxWidth()
    )

    // 滑块两端标签
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "灵敏",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "平缓",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ==================== 调试小球开关 ====================

/**
 * 重力感应调试小球开关
 *
 * 控制预览页面是否显示重力感应可视化调试小球。
 */
@Composable
private fun TiltDebugToggleSection(
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "重力感应调试",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "预览页显示倾斜方向指示球",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() }
        )
    }
}

// ==================== 预览按钮 ====================

/**
 * 预览按钮区域
 *
 * 图片不足 2 张时禁用按钮并显示提示文字。
 * 传感器不可用时也显示提示。
 */
@Composable
private fun PreviewButtonSection(
    canPreview: Boolean,
    isSensorAvailable: Boolean,
    onPreview: () -> Unit
) {
    Button(
        onClick = onPreview,
        enabled = canPreview && isSensorAvailable,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("预览光栅效果")
    }

    if (!canPreview) {
        Text(
            text = "至少需要 2 张图片",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    if (!isSensorAvailable) {
        Text(
            text = "当前设备不支持重力感应",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
