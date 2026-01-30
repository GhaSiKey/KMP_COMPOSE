package com.gaoshiqi.kmp.navigation

/**
 * 应用页面路由定义
 *
 * 使用 sealed class 实现编译期穷举检查：
 * - when 表达式必须处理所有页面，遗漏会编译报错
 * - 类型安全，无法创建未定义的页面
 */
sealed class Screen {

    /** 首页 - 展示问候和导航入口 */
    data object Home : Screen()

    /** 狗狗图片画廊 - 无限流展示 */
    data object DogGallery : Screen()
}
