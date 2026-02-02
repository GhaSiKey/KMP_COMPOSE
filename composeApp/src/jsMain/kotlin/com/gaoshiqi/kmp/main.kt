package com.gaoshiqi.kmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.gaoshiqi.kmp.db.DatabaseDriverFactory
import com.gaoshiqi.kmp.db.DatabaseProvider

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 初始化数据库（JS 平台会回退到内存存储）
    DatabaseProvider.init(DatabaseDriverFactory())

    ComposeViewport {
        App()
    }
}
