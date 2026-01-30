package com.gaoshiqi.kmp.data.model

import kotlinx.serialization.Serializable

/**
 * Dog CEO API 单张图片响应
 * GET https://dog.ceo/api/breeds/image/random
 */
@Serializable
data class DogImageResponse(
    val message: String,  // 图片 URL
    val status: String    // "success" or "error"
)

/**
 * Dog CEO API 多张图片响应
 * GET https://dog.ceo/api/breeds/image/random/{count}
 */
@Serializable
data class DogImagesResponse(
    val message: List<String>,  // 图片 URL 列表
    val status: String
)

/**
 * Dog CEO API 品种列表响应
 * GET https://dog.ceo/api/breeds/list/all
 */
@Serializable
data class DogBreedsResponse(
    val message: Map<String, List<String>>,  // 品种 -> 子品种列表
    val status: String
)
