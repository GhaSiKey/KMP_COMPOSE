package com.gaoshiqi.kmp

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    override val supportsPullToRefresh: Boolean = false
}

actual fun getPlatform(): Platform = JsPlatform()