package com.gaoshiqi.kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import com.gaoshiqi.kmp.data.model.VideoData
import com.gaoshiqi.kmp.navigation.Route
import com.gaoshiqi.kmp.navigation.SubjectDetailRoute
import com.gaoshiqi.kmp.navigation.VideoPlayerRoute
import com.gaoshiqi.kmp.screen.AnimeListScreen
import com.gaoshiqi.kmp.screen.DogGalleryScreen
import com.gaoshiqi.kmp.screen.HomeScreen
import com.gaoshiqi.kmp.screen.TrendingListScreen
import com.gaoshiqi.kmp.screen.VideoListScreen
import com.gaoshiqi.kmp.screen.VideoPlayerScreen
import com.gaoshiqi.kmp.ui.appTypography
import com.gaoshiqi.kmp.ui.flashlight.FlashlightScreen
import com.gaoshiqi.kmp.ui.lenticular.LenticularEditScreen
import com.gaoshiqi.kmp.ui.lenticular.LenticularPreviewScreen
import com.gaoshiqi.kmp.ui.lenticular.TiltTestScreen
import com.gaoshiqi.kmp.ui.lenticular.rememberLenticularViewModel
import com.gaoshiqi.kmp.util.createImageLoader

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
    // 配置全局 ImageLoader
    setSingletonImageLoaderFactory { context ->
        createImageLoader(context)
    }
    
    MaterialTheme(typography = appTypography()) {
        val navController = rememberNavController()

        // 光栅卡 ViewModel 在 NavHost 外创建，编辑和预览界面共享同一实例
        val lenticularViewModel = rememberLenticularViewModel()

        NavHost(
            navController = navController,
            startDestination = Route.HOME
        ) {
            composable(Route.HOME) {
                HomeScreen(
                    onNavigateToDogGallery = { navController.navigate(Route.DOG_GALLERY) },
                    onNavigateToAnimeList = { navController.navigate(Route.ANIME_LIST) },
                    onNavigateToVideoList = { navController.navigate(Route.VIDEO_LIST) },
                    onNavigateToTrendingList = { navController.navigate(Route.TRENDING_LIST) },
                    onNavigateToFlashlight = { navController.navigate(Route.FLASHLIGHT) },
                    onNavigateToLenticular = { navController.navigate(Route.LENTICULAR_EDIT) },
                    onNavigateToTiltTest = { navController.navigate(Route.TILT_TEST) }
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

            composable(Route.TRENDING_LIST) {
                TrendingListScreen(
                    onBack = { navController.popBackStack() },
                    onSubjectClick = { subjectId ->
                        // TODO: 导航到番剧详情页（待实现）
                        // navController.navigate(SubjectDetailRoute(subjectId))
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

            composable(Route.FLASHLIGHT) {
                FlashlightScreen(
                    onExit = { navController.popBackStack() }
                )
            }

            // 光栅卡编辑界面
            composable(Route.LENTICULAR_EDIT) {
                LenticularEditScreen(
                    viewModel = lenticularViewModel,
                    onPreview = {
                        navController.navigate(Route.LENTICULAR_PREVIEW)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 重力感应测试页面
            composable(Route.TILT_TEST) {
                TiltTestScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // 光栅卡全屏预览界面
            composable(Route.LENTICULAR_PREVIEW) {
                LenticularPreviewScreen(
                    viewModel = lenticularViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
