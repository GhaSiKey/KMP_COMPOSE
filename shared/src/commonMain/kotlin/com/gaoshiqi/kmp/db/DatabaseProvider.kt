package com.gaoshiqi.kmp.db

/**
 * 数据库提供者 - 单例管理
 *
 * 在应用启动时由各平台初始化，提供全局数据库访问
 * 使用单例模式确保整个应用使用同一个数据库实例
 */
object DatabaseProvider {
    private var database: AppDatabase? = null

    /**
     * 初始化数据库
     * 应在应用启动时调用（如 MainActivity.onCreate, main() 等）
     */
    fun init(factory: DatabaseDriverFactory) {
        if (database == null) {
            try {
                val driver = factory.createDriver()
                database = AppDatabase(driver)
            } catch (e: UnsupportedOperationException) {
                // Web 平台不支持 SQLite，database 保持为 null
            }
        }
    }

    /**
     * 获取数据库实例
     * 如果返回 null，表示当前平台不支持 SQLite（如 Web）
     */
    fun getDatabase(): AppDatabase? = database

    /**
     * 检查是否支持 SQLite 持久化
     */
    fun isSqliteSupported(): Boolean = database != null
}
