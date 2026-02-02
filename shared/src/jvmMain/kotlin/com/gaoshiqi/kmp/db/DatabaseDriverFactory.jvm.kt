package com.gaoshiqi.kmp.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

/**
 * JVM (Desktop) 平台数据库驱动实现
 *
 * 使用 JdbcSqliteDriver，通过 JDBC 连接 SQLite
 * 数据库文件存储在用户目录下的 .kmp-anime 文件夹中
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // 获取用户主目录下的应用数据目录
        val databasePath = File(System.getProperty("user.home"), ".kmp-anime")
        databasePath.mkdirs()
        val databaseFile = File(databasePath, "anime.db")

        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        // 如果是新数据库，需要创建表结构
        if (!databaseFile.exists() || databaseFile.length() == 0L) {
            AppDatabase.Schema.create(driver)
        }

        return driver
    }
}
