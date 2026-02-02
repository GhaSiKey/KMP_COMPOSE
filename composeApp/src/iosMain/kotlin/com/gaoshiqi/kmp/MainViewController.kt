package com.gaoshiqi.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.gaoshiqi.kmp.db.DatabaseDriverFactory
import com.gaoshiqi.kmp.db.DatabaseProvider

fun MainViewController() = ComposeUIViewController {
    // 初始化数据库
    // iOS 不需要额外参数，驱动会自动处理文件存储位置
    DatabaseProvider.init(DatabaseDriverFactory())
    App()
}