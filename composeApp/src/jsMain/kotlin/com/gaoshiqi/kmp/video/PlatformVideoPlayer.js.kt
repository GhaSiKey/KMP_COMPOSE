package com.gaoshiqi.kmp.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLVideoElement

/**
 * Web (JS) 平台的视频播放页面
 *
 * 使用 HTML5 <video> 元素 + 浏览器原生 controls + HTML 返回按钮。
 *
 * 核心问题：Compose JS 使用 ShadowDOM 渲染 Canvas，Canvas 会遮挡同级的 DOM 元素。
 * 解决方案：进入播放页时隐藏 Compose Canvas，用纯 HTML 覆盖层展示视频和控件；
 * 退出时恢复 Canvas 可见性。这样完全绕过 Canvas 层叠问题。
 */
@Composable
actual fun PlatformVideoPlayerScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier
) {
    // 创建全屏视频覆盖层（video + 返回按钮），并隐藏 Compose Canvas
    val overlay = remember {
        createVideoOverlay(onBack)
    }

    LaunchedEffect(url) {
        val video = overlay.querySelector("video") as? HTMLVideoElement ?: return@LaunchedEffect
        video.src = url
        video.load()
        try { video.play() } catch (_: dynamic) { /* 自动播放被策略阻止 */ }
    }

    // 进入时隐藏 Canvas，退出时恢复
    // overlay 挂载到 <html> 而非 <body>，因为 body 会被收缩隐藏
    DisposableEffect(overlay) {
        setComposeCanvasVisibility(hidden = true)
        document.documentElement?.appendChild(overlay)

        onDispose {
            val video = overlay.querySelector("video") as? HTMLVideoElement
            video?.pause()
            video?.src = ""
            overlay.parentElement?.removeChild(overlay)
            setComposeCanvasVisibility(hidden = false)
        }
    }
}

/**
 * 创建纯 HTML 的视频播放覆盖层
 *
 * 结构：
 * <div id="video-overlay" style="position:fixed; inset:0; z-index:9999; background:black">
 *   <video style="width:100%; height:100%; object-fit:contain" controls playsinline />
 *   <button style="position:absolute; top:16px; left:16px; ..." onclick="onBack()">← 返回</button>
 * </div>
 */
private fun createVideoOverlay(onBack: () -> Unit): HTMLDivElement {
    val overlay = document.createElement("div") as HTMLDivElement
    overlay.id = VIDEO_OVERLAY_ID
    overlay.style.apply {
        position = "fixed"
        setProperty("inset", "0")
        setProperty("z-index", "9999")
        setProperty("background", "black")
    }

    val video = document.createElement("video") as HTMLVideoElement
    video.controls = true
    video.setAttribute("playsinline", "true")
    video.style.apply {
        width = "100%"
        height = "100%"
        setProperty("object-fit", "contain")
    }
    overlay.appendChild(video)

    val backBtn = document.createElement("button") as HTMLElement
    backBtn.textContent = "← 返回"
    backBtn.style.apply {
        position = "absolute"
        top = "16px"
        left = "16px"
        setProperty("z-index", "1")
        setProperty("padding", "8px 16px")
        setProperty("font-size", "14px")
        setProperty("color", "white")
        setProperty("background", "rgba(0,0,0,0.6)")
        setProperty("border", "none")
        setProperty("border-radius", "8px")
        setProperty("cursor", "pointer")
    }
    backBtn.addEventListener("click", {
        (overlay.querySelector("video") as? HTMLVideoElement)?.pause()
        onBack()
    })
    overlay.appendChild(backBtn)

    return overlay
}

/**
 * 隐藏/显示 Compose Canvas
 *
 * ComposeViewport 无参数调用时，ShadowRoot 直接附着在 body 上：
 *   body > #shadow-root(open) > canvas
 *
 * 难点：ShadowDOM 的样式隔离使得外部 CSS 和 JS style 操作都难以穿透。
 * 解决方案：直接在 body 上设置 overflow:hidden + 宽高为 0 来收缩整个宿主，
 * 同时把 video overlay 挂载到 documentElement（<html>）上，跳出 body 限制。
 * 退出时恢复 body 样式。
 */
private fun setComposeCanvasVisibility(hidden: Boolean) {
    val body = document.body ?: return

    if (hidden) {
        // 记录原始样式以便恢复
        body.setAttribute(BODY_ORIGINAL_STYLE_ATTR, body.style.cssText)
        // 将 body 收缩为不可见，Canvas 随之消失
        body.style.setProperty("width", "0")
        body.style.setProperty("height", "0")
        body.style.setProperty("overflow", "hidden")
    } else {
        // 恢复 body 原始样式
        val original = body.getAttribute(BODY_ORIGINAL_STYLE_ATTR) ?: ""
        body.style.cssText = original
        body.removeAttribute(BODY_ORIGINAL_STYLE_ATTR)
    }
}

private const val BODY_ORIGINAL_STYLE_ATTR = "data-original-style"

private const val VIDEO_OVERLAY_ID = "video-overlay"
