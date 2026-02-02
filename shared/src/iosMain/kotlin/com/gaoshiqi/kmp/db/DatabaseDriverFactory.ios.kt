package com.gaoshiqi.kmp.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS 平台数据库驱动实现
 *
 * 使用 NativeSqliteDriver，通过 Kotlin/Native 调用 iOS 原生 SQLite
 * iOS 不需要额外的 Context，驱动会自动处理文件存储位置
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "anime.db")
    }
}
