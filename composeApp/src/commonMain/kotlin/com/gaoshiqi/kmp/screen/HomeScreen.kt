package com.gaoshiqi.kmp.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gaoshiqi.kmp.Greeting
import com.gaoshiqi.kmp.getCurrentDateTime
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.compose_multiplatform

/**
 * 首页 - 展示问候信息和导航入口
 *
 * @param onNavigateToDogGallery 点击 "Random Dogs" 时的回调
 * @param onNavigateToAnimeList 点击 "追番列表" 时的回调
 * @param onNavigateToVideoList 点击 "视频播放" 时的回调
 */
@Composable
fun HomeScreen(
    onNavigateToDogGallery: () -> Unit,
    onNavigateToAnimeList: () -> Unit = {},
    onNavigateToVideoList: () -> Unit = {}
) {
    var showContent by remember { mutableStateOf(false) }
    var currentDateTime by remember { mutableStateOf(getCurrentDateTime()) }

    // 每秒更新一次时间
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = getCurrentDateTime()
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = currentDateTime,
            style = MaterialTheme.typography.headlineSmall
        )

        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }

            Button(onClick = onNavigateToDogGallery) {
                Text("Random Dogs")
            }

            Button(onClick = onNavigateToAnimeList) {
                Text("追番列表")
            }

            Button(onClick = onNavigateToVideoList) {
                Text("视频播放")
            }
        }

        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
