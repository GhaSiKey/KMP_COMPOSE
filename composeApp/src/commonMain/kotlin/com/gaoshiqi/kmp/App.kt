package com.gaoshiqi.kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.gaoshiqi.kmp.navigation.Screen
import com.gaoshiqi.kmp.screen.DogGalleryScreen
import com.gaoshiqi.kmp.screen.HomeScreen

/**
 * 应用入口 - 路由容器
 *
 * 使用 Compose 状态驱动路由：
 * - currentScreen 状态变化 → 触发重组 → 渲染对应页面
 * - 无需引入第三方路由库，适合简单导航场景
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        when (currentScreen) {
            Screen.Home -> HomeScreen(
                onNavigateToDogGallery = { currentScreen = Screen.DogGallery }
            )
            Screen.DogGallery -> DogGalleryScreen(
                onBack = { currentScreen = Screen.Home }
            )
        }
    }
}
