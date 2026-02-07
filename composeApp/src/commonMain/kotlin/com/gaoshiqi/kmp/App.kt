package com.gaoshiqi.kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gaoshiqi.kmp.data.model.VideoData
import com.gaoshiqi.kmp.navigation.Route
import com.gaoshiqi.kmp.navigation.VideoPlayerRoute
import com.gaoshiqi.kmp.screen.AnimeListScreen
import com.gaoshiqi.kmp.screen.DogGalleryScreen
import com.gaoshiqi.kmp.screen.HomeScreen
import com.gaoshiqi.kmp.screen.VideoListScreen
import com.gaoshiqi.kmp.screen.VideoPlayerScreen

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
                    onNavigateToAnimeList = { navController.navigate(Route.ANIME_LIST) },
                    onNavigateToVideoList = { navController.navigate(Route.VIDEO_LIST) }
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

            composable(Route.VIDEO_LIST) {
                VideoListScreen(
                    onBack = { navController.popBackStack() },
                    onVideoClick = { video ->
                        navController.navigate(VideoPlayerRoute(video.id))
                    }
                )
            }

            composable<VideoPlayerRoute> { backStackEntry ->
                val route: VideoPlayerRoute = backStackEntry.toRoute()
                val video = VideoData.findById(route.videoId)
                if (video != null) {
                    VideoPlayerScreen(
                        videoUrl = video.url,
                        videoTitle = video.title,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
