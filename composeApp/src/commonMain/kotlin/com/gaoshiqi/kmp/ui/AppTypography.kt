package com.gaoshiqi.kmp.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.noto_sans_sc_regular
import org.jetbrains.compose.resources.Font

/**
 * 自定义 Typography，使用 NotoSansSC 字体
 *
 * Compose Multiplatform JS 平台使用 Canvas 渲染文本，不走浏览器的文本排版引擎，
 * 因此无法使用系统字体的 CJK fallback。必须显式加载包含中文字形的字体，
 * 否则中文字符会显示为 tofu（□）。
 *
 * 虽然 Android/iOS/Desktop 有系统中文字体可用，但统一使用自定义字体
 * 可以保证所有平台的文本渲染效果一致。
 */
@Composable
fun appTypography(): Typography {
    val notoSansSC = FontFamily(Font(Res.font.noto_sans_sc_regular))

    val defaults = Typography()
    return remember(notoSansSC) { Typography(
        displayLarge = defaults.displayLarge.copy(fontFamily = notoSansSC),
        displayMedium = defaults.displayMedium.copy(fontFamily = notoSansSC),
        displaySmall = defaults.displaySmall.copy(fontFamily = notoSansSC),
        headlineLarge = defaults.headlineLarge.copy(fontFamily = notoSansSC),
        headlineMedium = defaults.headlineMedium.copy(fontFamily = notoSansSC),
        headlineSmall = defaults.headlineSmall.copy(fontFamily = notoSansSC),
        titleLarge = defaults.titleLarge.copy(fontFamily = notoSansSC),
        titleMedium = defaults.titleMedium.copy(fontFamily = notoSansSC),
        titleSmall = defaults.titleSmall.copy(fontFamily = notoSansSC),
        bodyLarge = defaults.bodyLarge.copy(fontFamily = notoSansSC),
        bodyMedium = defaults.bodyMedium.copy(fontFamily = notoSansSC),
        bodySmall = defaults.bodySmall.copy(fontFamily = notoSansSC),
        labelLarge = defaults.labelLarge.copy(fontFamily = notoSansSC),
        labelMedium = defaults.labelMedium.copy(fontFamily = notoSansSC),
        labelSmall = defaults.labelSmall.copy(fontFamily = notoSansSC),
    ) }
}
