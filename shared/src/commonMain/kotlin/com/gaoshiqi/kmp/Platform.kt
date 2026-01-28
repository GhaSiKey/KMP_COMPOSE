package com.gaoshiqi.kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform