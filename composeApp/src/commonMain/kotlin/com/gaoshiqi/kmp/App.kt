package com.gaoshiqi.kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gaoshiqi.kmp.navigation.Route
import com.gaoshiqi.kmp.screen.AnimeListScreen
import com.gaoshiqi.kmp.screen.DogGalleryScreen
import com.gaoshiqi.kmp.screen.HomeScreen

/**
 * 应用入口 - Navigation 路由容器
 *
 * NavHost 管理页面栈：
 * - navController.navigate() 跳转（类似 startActivity）
 * - navController.popBackStack() 返回（类似 finish）
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Route.HOME
        ) {
            composable(Route.HOME) {
                HomeScreen(
                    onNavigateToDogGallery = { navController.navigate(Route.DOG_GALLERY) },
                    onNavigateToAnimeList = { navController.navigate(Route.ANIME_LIST) }
                )
            }

            composable(Route.DOG_GALLERY) {
                DogGalleryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Route.ANIME_LIST) {
                AnimeListScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
