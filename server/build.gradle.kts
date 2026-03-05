plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.gaoshiqi.kmp"
version = "1.0.0"
application {
    mainClass.set("com.gaoshiqi.kmp.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.cors) // CORS 支持
    implementation(libs.ktor.server.contentNegotiation) // 内容协商
    implementation(libs.ktor.client.core) // HTTP 客户端
    implementation(libs.ktor.client.cio) // CIO 引擎
    implementation(libs.ktor.client.contentNegotiation) // 客户端内容协商
    implementation(libs.ktor.serialization.json) // JSON 序列化
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}