# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Developer Profile

**背景：** 用户是一名经验丰富的 Android 开发工程师，但是 KMP（Kotlin Multiplatform）项目新手。

**协作方式：**
- 在完成需求的同时，主动解释 KMP 特有的概念和模式
- 当涉及 `expect/actual`、跨平台源集（source sets）、平台特定代码等 KMP 核心概念时，提供简明扼要的说明
- 对比 KMP 做法与纯 Android 开发的差异，帮助理解
- 遇到常见的 KMP 陷阱或最佳实践时，及时提醒

## Project Overview

Kotlin Multiplatform (KMP) project with Compose Multiplatform UI targeting Android, iOS, Web (Wasm/JS), Desktop (JVM), and Server (Ktor).

**Key versions:** Kotlin 2.3.0, Compose Multiplatform 1.10.0, Ktor 3.3.3, Gradle 8.14.3

## Build Commands (Windows)

```shell
# Android - build debug APK
.\gradlew.bat :composeApp:assembleDebug

# Desktop (JVM) - run application
.\gradlew.bat :composeApp:run

# Server - run Ktor backend (port 8080)
.\gradlew.bat :server:run

# Web (Wasm) - modern browsers, faster
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun

# Web (JS) - older browser support
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun

# Tests
.\gradlew.bat :composeApp:allTests
.\gradlew.bat :server:test
.\gradlew.bat :shared:allTests
```

**iOS:** Open `iosApp/` in Xcode and run from there.

## Module Architecture

```
├── composeApp/     # Compose Multiplatform UI (all platforms)
│   └── src/
│       ├── commonMain/    # Shared UI code
│       ├── androidMain/   # Android entry (MainActivity)
│       ├── iosMain/       # iOS entry (MainViewController)
│       ├── jvmMain/       # Desktop entry
│       └── webMain/       # Web (JS/Wasm)
│
├── shared/         # Shared Kotlin library (business logic)
│   └── src/
│       ├── commonMain/    # Platform interface (expect)
│       └── [platform]Main/# Platform implementations (actual)
│
├── server/         # Ktor backend server
│   └── src/main/kotlin/   # Application.kt (port 8080)
│
└── iosApp/         # Native iOS Xcode project wrapper
```

## Multiplatform Pattern

Uses Kotlin's **expect/actual** mechanism for platform-specific code:

- `shared/src/commonMain/kotlin/Platform.kt` - declares `expect fun getPlatform(): Platform`
- `shared/src/[platform]Main/kotlin/Platform.[platform].kt` - provides `actual` implementations

Platform implementations return device-specific info (Android SDK version, iOS version, Java version, etc.).

## Key Configuration Files

- `gradle/libs.versions.toml` - Centralized dependency version catalog
- `composeApp/build.gradle.kts` - Multiplatform targets and compose configuration
- `shared/build.gradle.kts` - Shared library targets
- `server/build.gradle.kts` - Ktor server configuration

## Android Configuration

- compileSdk/targetSdk: 36, minSdk: 24
- Package: `com.gaoshiqi.kmp`
- Entry: `MainActivity` → `App()` composable
