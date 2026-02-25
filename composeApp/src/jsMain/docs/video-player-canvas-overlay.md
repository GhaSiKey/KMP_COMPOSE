# JS 平台视频播放白屏问题排查与修复

## 问题现象

点击视频进入播放页后：
- 页面一片纯白（或停留在视频列表页）
- 视频有声音正常播放
- 无法点击任何控件，无法回退

## 根因分析

### Compose JS 的渲染架构

Compose Multiplatform 在 JS 平台的渲染方式与 Android 完全不同：

```
Android:  Activity → Window → DecorView → View 树（可遍历、可操作）
JS/Web:   <body> → #shadow-root(open) → <canvas>（ShadowDOM 隔离）
```

`ComposeViewport` 无参数调用时，会将一个 **ShadowRoot** 直接附着到 `<body>` 上，
内部通过 `<canvas>` 元素以 Canvas2D/WebGL 方式绘制整个 Compose UI。

实际 DOM 结构：

```html
<body>
  ▸ #shadow-root (open)     ← Compose 宿主，内含 <canvas>
  ▸ <svg>                   ← 加载动画
  <script src="composeApp.js">
  <div id="video-overlay">  ← 我们的 video overlay
</body>
```

### 核心矛盾

HTML5 `<video>` 是原生 DOM 元素，Compose UI 则渲染在 ShadowDOM 内部的 `<canvas>` 上。
两者存在不可调和的层叠冲突：

1. **Canvas 始终遮挡 DOM 元素** — ShadowDOM 内的 Canvas 绘制层不受外部 `z-index` 控制
2. **ShadowDOM 样式隔离** — 外部 CSS 和 `element.style` 无法穿透 ShadowRoot 影响内部 Canvas
3. **`z-index` 方案失效** — 即使设置 Compose 宿主 `z-index: -1`，Canvas 的像素绘制仍覆盖上层

### 排查过程中走过的弯路

| 尝试方案 | 失败原因 |
|---------|---------|
| 遍历 `body.children` 设置 `z-index: -1` | ShadowRoot 不在 `body.children` 列表中，遍历找不到 Compose 宿主 |
| 遍历 `body.children` 设置 `visibility: hidden` | 同上，根本没找到目标元素 |
| `body.asDynamic().shadowRoot.querySelector("canvas")` 穿透 ShadowDOM | Kotlin/JS `dynamic` 链式调用不可靠：JS `undefined` 与 Kotlin `null` 不等价，`?:` 静默失败 |

## 最终解决方案

**思路转变**：不再尝试穿透 ShadowDOM 操作 Canvas，而是从外部收缩整个 `<body>`。

### 具体实现

```
进入视频播放时：
  1. 备份 body 原始样式到 data attribute
  2. 设置 body { width: 0; height: 0; overflow: hidden }
     → Canvas 随 body 收缩而消失
  3. 将 video overlay 挂载到 <html>（而非 <body>）
     → overlay 不受 body 收缩影响，position:fixed 正常全屏

退出视频播放时：
  1. 从 data attribute 恢复 body 原始样式
  2. 移除 video overlay
  → Compose Canvas 恢复渲染，回到正常页面
```

### DOM 结构对比

```
隐藏前：                              隐藏后：
<html>                                <html>
  <body>                                <body style="width:0;height:0;overflow:hidden">
    #shadow-root → canvas (可见)           #shadow-root → canvas (不可见)
    <div id="video-overlay">            <div id="video-overlay"> ← 挂在 <html> 上
  </body>                                </div>
</html>                                 <body>
                                      </html>
```

## 关键技术要点

### 1. ShadowDOM 是 Compose JS 的核心隔离机制

与 Android 的 View 树不同，ShadowDOM 提供了完整的样式和 DOM 隔离。
这意味着在 KMP Web 开发中，**任何需要混合原生 HTML 和 Compose UI 的场景都需要特殊处理**。

### 2. Kotlin/JS dynamic 类型的陷阱

```kotlin
// 看似安全，实际可能静默失败
val shadowRoot = body.asDynamic().shadowRoot ?: return  // JS undefined ≠ Kotlin null
```

`asDynamic()` 返回 `dynamic` 类型后，Kotlin 的空安全机制不再可靠。
JS 的 `undefined` 不会触发 `?:` 的 fallback 分支。

### 3. position:fixed 的挂载点选择

`position: fixed` 相对于 viewport 定位，理论上挂在任何元素下都能全屏。
但如果父元素（`<body>`）被收缩为 0 尺寸，浏览器实现可能导致子元素也受影响。
因此 video overlay 需要挂载到 `<html>`（`document.documentElement`）上。

## 文件变更

- `composeApp/src/jsMain/kotlin/com/gaoshiqi/kmp/video/PlatformVideoPlayer.js.kt`
  - `setComposeCanvasVisibility()`: body 收缩方案替代 ShadowDOM 穿透方案
  - `DisposableEffect`: overlay 挂载点从 `document.body` 改为 `document.documentElement`
