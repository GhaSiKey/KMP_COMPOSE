package com.gaoshiqi.kmp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 配置 CORS - 允许所有来源访问
    install(CORS) {
        anyHost() // 允许任何域名访问
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
    }
    
    // 创建 HTTP 客户端用于代理请求
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        
        // Bangumi API 代理端点
        get("/api/bangumi/trending/subjects") {
            try {
                // 获取查询参数
                val type = call.request.queryParameters["type"]?.toIntOrNull() ?: 2
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                
                // 转发请求到 Bangumi API
                val response = httpClient.get("${BANGUMI_NEXT_BASE_URL}p1/trending/subjects") {
                    parameter("type", type)
                    parameter("offset", offset)
                    parameter("limit", limit)
                    header(HttpHeaders.UserAgent, BANGUMI_USER_AGENT)
                }
                
                // 返回响应（CORS 头已自动添加）
                call.respondText(
                    text = response.body<String>(),
                    contentType = ContentType.Application.Json,
                    status = response.status
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (e.message ?: "Unknown error"))
                )
            }
        }
    }
}