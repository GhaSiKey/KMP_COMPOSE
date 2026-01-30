package com.gaoshiqi.kmp.data.api

import com.gaoshiqi.kmp.DOG_API_BASE_URL
import com.gaoshiqi.kmp.data.model.DogBreedsResponse
import com.gaoshiqi.kmp.data.model.DogImageResponse
import com.gaoshiqi.kmp.data.model.DogImagesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Dog CEO API 客户端
 *
 * 文档: https://dog.ceo/dog-api/
 */
class DogApi {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /**
     * 获取一张随机狗狗图片
     */
    suspend fun getRandomImage(): Result<String> {
        return try {
            val response: DogImageResponse = httpClient.get(
                "$DOG_API_BASE_URL/breeds/image/random"
            ).body()
            if (response.status == "success") {
                Result.success(response.message)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取多张随机狗狗图片
     *
     * @param count 图片数量 (1-50)
     */
    suspend fun getRandomImages(count: Int = 20): Result<List<String>> {
        return try {
            val response: DogImagesResponse = httpClient.get(
                "$DOG_API_BASE_URL/breeds/image/random/$count"
            ).body()
            if (response.status == "success") {
                Result.success(response.message)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取所有狗狗品种列表
     */
    suspend fun getAllBreeds(): Result<Map<String, List<String>>> {
        return try {
            val response: DogBreedsResponse = httpClient.get(
                "$DOG_API_BASE_URL/breeds/list/all"
            ).body()
            if (response.status == "success") {
                Result.success(response.message)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取指定品种的随机图片
     *
     * @param breed 品种名称，如 "hound", "poodle"
     */
    suspend fun getBreedImage(breed: String): Result<String> {
        return try {
            val response: DogImageResponse = httpClient.get(
                "$DOG_API_BASE_URL/breed/$breed/images/random"
            ).body()
            if (response.status == "success") {
                Result.success(response.message)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        httpClient.close()
    }
}
