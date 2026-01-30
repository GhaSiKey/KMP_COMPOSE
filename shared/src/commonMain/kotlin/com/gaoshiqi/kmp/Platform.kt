package com.gaoshiqi.kmp

interface Platform {
    val name: String
    /** 是否支持下拉刷新（移动端支持，桌面/网页不支持） */
    val supportsPullToRefresh: Boolean
}

expect fun getPlatform(): Platform