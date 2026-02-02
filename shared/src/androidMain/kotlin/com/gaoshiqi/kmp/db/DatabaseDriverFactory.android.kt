package com.gaoshiqi.kmp.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android 平台数据库驱动实现
 *
 * 使用 AndroidSqliteDriver，底层是 Android 原生的 SQLite
 * 需要传入 Context 来访问应用的数据库目录
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "anime.db")
    }
}
