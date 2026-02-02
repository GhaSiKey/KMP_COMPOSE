package com.gaoshiqi.kmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gaoshiqi.kmp.db.DatabaseDriverFactory
import com.gaoshiqi.kmp.db.DatabaseProvider

fun main() {
    // 初始化数据库（在应用启动时）
    // Desktop 平台的数据库文件存储在用户主目录下
    DatabaseProvider.init(DatabaseDriverFactory())

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KMP",
        ) {
            App()
        }
    }
}