package com.gaoshiqi.kmp.db

import app.cash.sqldelight.db.SqlDriver

/**
 * 数据库驱动工厂 - expect 声明
 *
 * KMP 核心模式：expect/actual
 * - expect: 在 commonMain 中声明接口/类的签名
 * - actual: 在各平台 (androidMain, iosMain 等) 中提供具体实现
 *
 * 这样共享代码可以使用统一的 API，而各平台使用不同的 SQLite 实现
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
