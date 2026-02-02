package com.gaoshiqi.kmp.db

import app.cash.sqldelight.db.SqlDriver

/**
 * JS (Web) 平台数据库驱动实现
 *
 * JS 平台 SQLDelight 需要使用 web-worker-driver，配置较复杂。
 * 这里暂时抛出异常，让 Repository 回退到内存存储。
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException("SQLite not configured for JS platform. Using in-memory storage.")
    }
}
