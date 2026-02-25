package com.gaoshiqi.kmp.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text as ComposeText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button as FxButton
import javafx.scene.control.Label as FxLabel
import javafx.scene.control.Slider as FxSlider
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region as FxRegion
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.util.Duration
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource
import javax.swing.SwingUtilities

/**
 * Desktop (JVM) 平台的视频播放页面
 *
 * JavaFX MediaView + JavaFX 控件，全部在 SwingPanel 内部完成。
 * Swing heavyweight 组件会拦截所有鼠标事件，因此控件必须在 JavaFX 层内实现。
 * 返回也在 JavaFX 内实现，通过回调通知 Compose 导航。
 */
@Composable
actual fun PlatformVideoPlayerScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier
) {
    if (url.contains(".m3u8", ignoreCase = true)) {
        HlsFallbackView(title, onBack, modifier)
        return
    }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val jfxPanel = remember {
        JFXPanel().apply {
            Platform.setImplicitExit(false)
        }
    }

    // 创建 JavaFX 播放器 + 控件
    LaunchedEffect(url) {
        Platform.runLater {
            mediaPlayer?.dispose()

            val media = Media(url)
            val player = MediaPlayer(media)
            val mediaView = MediaView(player)
            mediaView.isPreserveRatio = true

            // --- 控件层 ---
            val playPauseBtn = FxButton("⏸").apply {
                style = "-fx-font-size: 16px; -fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-cursor: hand;"
                setOnAction {
                    when (player.status) {
                        MediaPlayer.Status.PLAYING -> { player.pause(); text = "▶" }
                        else -> { player.play(); text = "⏸" }
                    }
                }
            }

            val timeLabel = FxLabel("00:00 / 00:00").apply {
                style = "-fx-text-fill: white; -fx-font-size: 12px;"
            }

            val seekSlider = FxSlider(0.0, 1.0, 0.0).apply {
                style = "-fx-control-inner-background: rgba(255,255,255,0.3);"
                HBox.setHgrow(this, Priority.ALWAYS)
                maxWidth = Double.MAX_VALUE
            }

            val backBtn = FxButton("← 返回").apply {
                style = "-fx-font-size: 13px; -fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-cursor: hand;"
                setOnAction { SwingUtilities.invokeLater { onBack() } }
            }

            val titleLabel = FxLabel(title).apply {
                style = "-fx-text-fill: white; -fx-font-size: 14px;"
            }

            // 顶部栏
            val topBar = HBox(8.0, backBtn, titleLabel).apply {
                alignment = Pos.CENTER_LEFT
                padding = Insets(12.0)
            }

            // 底部控件栏
            val bottomBar = HBox(10.0, playPauseBtn, seekSlider, timeLabel).apply {
                alignment = Pos.CENTER_LEFT
                padding = Insets(12.0)
            }

            // 控件容器
            val controlsOverlay = VBox().apply {
                children.addAll(topBar, createVBoxSpacer(), bottomBar)
                style = "-fx-background-color: transparent;"
                isMouseTransparent = false
            }

            // 点击视频区域切换控件可见性
            var controlsVisible = true

            // 视频区域（接收点击）
            val videoContainer = StackPane(mediaView).apply {
                style = "-fx-background-color: black;"
                setOnMouseClicked {
                    controlsVisible = !controlsVisible
                    controlsOverlay.isVisible = controlsVisible
                }
            }

            mediaView.fitWidthProperty().bind(videoContainer.widthProperty())
            mediaView.fitHeightProperty().bind(videoContainer.heightProperty())

            // 根布局
            val root = StackPane(videoContainer, controlsOverlay).apply {
                style = "-fx-background-color: black;"
            }

            // Slider 拖动处理
            var isSliderDragging = false
            seekSlider.setOnMousePressed { isSliderDragging = true }
            seekSlider.setOnMouseReleased {
                isSliderDragging = false
                val totalDuration = player.totalDuration
                if (totalDuration != null && !totalDuration.isUnknown && !totalDuration.isIndefinite) {
                    player.seek(Duration.seconds(seekSlider.value * totalDuration.toSeconds()))
                }
            }

            // 播放状态监听
            player.statusProperty().addListener { _, _, newStatus ->
                playPauseBtn.text = if (newStatus == MediaPlayer.Status.PLAYING) "⏸" else "▶"
            }

            player.currentTimeProperty().addListener { _, _, newTime ->
                if (!isSliderDragging && newTime != null) {
                    val totalDuration = player.totalDuration
                    if (totalDuration != null && !totalDuration.isUnknown && !totalDuration.isIndefinite && totalDuration.toSeconds() > 0) {
                        seekSlider.value = newTime.toSeconds() / totalDuration.toSeconds()
                        val cur = formatTime(newTime.toSeconds().toInt())
                        val total = formatTime(totalDuration.toSeconds().toInt())
                        timeLabel.text = "$cur / $total"
                    }
                }
            }

            val scene = Scene(root)
            jfxPanel.scene = scene

            player.play()
            mediaPlayer = player
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Platform.runLater { mediaPlayer?.dispose() }
        }
    }

    SwingPanel(
        factory = { jfxPanel },
        modifier = modifier.fillMaxSize()
    )
}

/** 格式化秒数为 mm:ss */
private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

/** JavaFX 弹性 spacer，填充 VBox 中间空间 */
private fun createVBoxSpacer(): FxRegion {
    return FxRegion().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
    }
}

@Composable
private fun HlsFallbackView(title: String, onBack: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        ComposeText(
            text = "Desktop 不支持 HLS (m3u8) 流媒体播放\n请使用 MP4 格式的视频链接",
            color = Color.White
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_back),
                contentDescription = "返回"
            )
        }
    }
}
