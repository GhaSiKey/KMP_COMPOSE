package com.gaoshiqi.kmp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gaoshiqi.kmp.data.model.AnimeItem
import com.gaoshiqi.kmp.data.repository.AnimeRepository
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.ic_arrow_back
import kmp.composeapp.generated.resources.ic_delete
import org.jetbrains.compose.resources.painterResource

/**
 * 追番列表页面
 *
 * 功能：
 * - 顶部输入框添加新番剧
 * - 列表展示所有番剧（Checkbox 标记是否看完）
 * - 点击删除按钮移除番剧
 *
 * KMP 知识点：
 * - 数据持久化使用 SQLDelight，Android/iOS/Desktop 支持 SQLite
 * - JS (Web) 平台使用内存存储（刷新页面后数据丢失）
 *
 * @param onBack 返回上一页的回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    onBack: () -> Unit
) {
    // 创建 Repository 实例
    val repository = remember { AnimeRepository() }

    // 收集番剧列表（响应式）
    val animeList by repository.getAllAnime().collectAsState(initial = emptyList())

    // 输入框状态
    var inputText by remember { mutableStateOf("") }

    // 检查是否支持持久化存储
    val isPersistent = remember { repository.isPersistent() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("追番列表") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 非持久化存储提示
            if (!isPersistent) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "Web 平台暂不支持持久化存储，刷新页面后数据会丢失",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // 输入区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("番剧名称") },
                    singleLine = true,
                    placeholder = { Text("输入番剧名称...") }
                )

                OutlinedButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            repository.addAnime(inputText.trim())
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank()
                ) {
                    Text("添加")
                }
            }

            // 番剧列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = animeList,
                    key = { it.id }
                ) { anime ->
                    AnimeListItem(
                        anime = anime,
                        onWatchedChanged = { isWatched ->
                            repository.updateWatchedStatus(anime.id, isWatched)
                        },
                        onDelete = {
                            repository.deleteAnime(anime.id)
                        }
                    )
                }

                // 空状态提示
                if (animeList.isEmpty()) {
                    item {
                        Text(
                            text = "还没有添加任何番剧，快来添加吧！",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 单个番剧列表项
 */
@Composable
private fun AnimeListItem(
    anime: AnimeItem,
    onWatchedChanged: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 完成状态 Checkbox
            Checkbox(
                checked = anime.isWatched,
                onCheckedChange = onWatchedChanged
            )

            // 番剧名称（已看完的显示删除线）
            Text(
                text = anime.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (anime.isWatched) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (anime.isWatched) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 删除按钮
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
