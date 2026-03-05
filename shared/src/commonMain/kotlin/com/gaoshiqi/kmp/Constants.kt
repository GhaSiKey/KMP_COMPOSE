package com.gaoshiqi.kmp

const val SERVER_PORT = 8080

// Dog CEO API 配置
const val DOG_API_BASE_URL = "https://dog.ceo/api"

// Bangumi Next API 配置
const val BANGUMI_NEXT_BASE_URL = "https://next.bgm.tv/"
const val BANGUMI_BEARER_TOKEN = "your_token_here"  // TODO: 从配置文件或环境变量读取
const val BANGUMI_USER_AGENT = "BANGUMI/1.0 (your@email.com)"

// 分页配置
const val TRENDING_PAGE_SIZE = 10
const val TRENDING_REQUEST_TIMEOUT_MS = 30_000L