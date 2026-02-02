package com.gaoshiqi.kmp.data.repository

import kotlin.js.Date

internal actual fun currentTimeMillis(): Long = Date.now().toLong()
