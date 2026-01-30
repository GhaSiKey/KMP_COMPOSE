package com.gaoshiqi.kmp

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val supportsPullToRefresh: Boolean = false
}

actual fun getPlatform(): Platform = JVMPlatform()