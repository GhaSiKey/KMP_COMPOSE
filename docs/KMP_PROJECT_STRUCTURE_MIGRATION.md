# KMP 项目结构迁移指南

> **适用于**: Android Gradle Plugin 9.0.0+ 的项目结构迁移
>
> **状态**: 📋 待执行（建议等 AGP 9.0 正式发布后再迁移）
>
> **官方文档**:
> - https://kotl.in/kmp-project-structure-migration
> - https://kotl.in/gradle/agp-new-kmp

---

## 📋 目录

1. [背景说明](#背景说明)
2. [当前项目结构](#当前项目结构)
3. [目标项目结构](#目标项目结构)
4. [迁移步骤](#迁移步骤)
5. [文件变更清单](#文件变更清单)
6. [验证检查清单](#验证检查清单)
7. [回滚方案](#回滚方案)
8. [常见问题](#常见问题)

---

## 背景说明

### 为什么需要迁移？

从 **Android Gradle Plugin (AGP) 9.0.0** 开始：

- `org.jetbrains.kotlin.multiplatform` 插件将**不再兼容** `com.android.application`
- `org.jetbrains.kotlin.multiplatform` 插件将**不再兼容** `com.android.library`

这意味着一个模块不能同时是 KMP 模块和 Android Application/Library。

### 迁移目标

| 方面 | 旧结构 | 新结构 |
|------|--------|--------|
| 职责分离 | 混合 | 清晰 |
| 构建速度 | 较慢 | 可并行，更快 |
| 依赖管理 | 容易混乱 | 边界清晰 |
| 团队协作 | 紧耦合 | 松耦合 |

### 前置条件

- [ ] Android Gradle Plugin 升级到 9.0.0+
- [ ] Kotlin 版本兼容新插件
- [ ] 备份当前项目

---

## 当前项目结构

```
KMP_COMPOSE/
│
├── composeApp/                      ← ⚠️ 混合模块（需要拆分）
│   ├── build.gradle.kts
│   │   ├── plugins:
│   │   │   ├── org.jetbrains.kotlin.multiplatform
│   │   │   ├── com.android.application      ← 将不兼容
│   │   │   ├── org.jetbrains.compose
│   │   │   └── org.jetbrains.kotlin.plugin.compose
│   │   └── kotlin targets: android, ios, jvm, js
│   │
│   └── src/
│       ├── commonMain/kotlin/       ← 跨平台 Compose UI
│       │   └── com/gaoshiqi/kmp/
│       │       ├── App.kt
│       │       ├── navigation/
│       │       └── screen/
│       │
│       ├── androidMain/kotlin/      ← Android 入口
│       │   └── com/gaoshiqi/kmp/
│       │       └── MainActivity.kt  ← 需要移动
│       │
│       ├── iosMain/kotlin/          ← iOS 平台适配
│       ├── jvmMain/kotlin/          ← Desktop 入口
│       └── jsMain/kotlin/           ← Web 入口
│
├── shared/                          ← ⚠️ 也需要改插件
│   ├── build.gradle.kts
│   │   ├── plugins:
│   │   │   ├── org.jetbrains.kotlin.multiplatform
│   │   │   └── com.android.library          ← 将不兼容
│   │   └── kotlin targets: android, ios, jvm, js
│   │
│   └── src/
│       ├── commonMain/              ← 业务逻辑、网络、数据库
│       ├── androidMain/             ← Android 平台实现
│       ├── iosMain/                 ← iOS 平台实现
│       └── jvmMain/                 ← JVM 平台实现
│
├── server/                          ← ✅ 不受影响
│   └── Ktor 服务端
│
├── iosApp/                          ← ✅ 不受影响
│   └── Xcode 项目壳
│
├── settings.gradle.kts
├── build.gradle.kts
└── gradle/libs.versions.toml
```

### 当前模块依赖关系

```
┌─────────┐
│ iosApp  │──────────────────┐
└─────────┘                  │
                             ▼
                      ┌─────────────┐      ┌─────────┐
                      │ composeApp  │─────▶│ shared  │
                      │ (Android内嵌)│      └─────────┘
                      └─────────────┘
```

---

## 目标项目结构

```
KMP_COMPOSE/
│
├── 📱 androidApp/                   ← 🆕 新增模块
│   ├── build.gradle.kts
│   │   └── plugins:
│   │       └── com.android.application  ← 纯 Android
│   │
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/com/gaoshiqi/kmp/
│           └── MainActivity.kt      ← 从 composeApp 移过来
│
├── 🎨 composeApp/                   ← 改造后：纯 KMP UI 库
│   ├── build.gradle.kts
│   │   └── plugins:
│   │       ├── org.jetbrains.kotlin.multiplatform
│   │       ├── com.android.kotlin.multiplatform.library  ← 🆕 新插件
│   │       ├── org.jetbrains.compose
│   │       └── org.jetbrains.kotlin.plugin.compose
│   │
│   └── src/
│       ├── commonMain/kotlin/       ← 跨平台 Compose UI（不变）
│       ├── androidMain/kotlin/      ← ⚠️ 只保留平台适配代码
│       ├── iosMain/kotlin/
│       ├── jvmMain/kotlin/
│       └── jsMain/kotlin/
│
├── 📦 shared/                       ← 改造后：使用新插件
│   ├── build.gradle.kts
│   │   └── plugins:
│   │       ├── org.jetbrains.kotlin.multiplatform
│   │       └── com.android.kotlin.multiplatform.library  ← 🆕 新插件
│   │
│   └── src/（不变）
│
├── 🖥️ server/                      ← 不变
├── 🍎 iosApp/                       ← 不变
│
├── settings.gradle.kts              ← 添加 :androidApp
├── build.gradle.kts
└── gradle/libs.versions.toml
```

### 迁移后模块依赖关系

```
┌─────────┐
│ iosApp  │─────────────────────┐
└─────────┘                     │
                                ▼
┌────────────┐           ┌─────────────┐      ┌─────────┐
│ androidApp │──────────▶│ composeApp  │─────▶│ shared  │
└────────────┘           └─────────────┘      └─────────┘
      │                                             ▲
      └─────────────────────────────────────────────┘
```

---

## 迁移步骤

### 第一步：创建 androidApp 模块

#### 1.1 创建目录结构

```bash
mkdir -p androidApp/src/main/kotlin/com/gaoshiqi/kmp
mkdir -p androidApp/src/main/res
```

#### 1.2 创建 androidApp/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.gaoshiqi.kmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gaoshiqi.kmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // 依赖 KMP Compose UI 模块
    implementation(projects.composeApp)

    // Android Compose 依赖
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.uiTooling)
}
```

#### 1.3 移动 MainActivity.kt

**从:** `composeApp/src/androidMain/kotlin/com/gaoshiqi/kmp/MainActivity.kt`

**到:** `androidApp/src/main/kotlin/com/gaoshiqi/kmp/MainActivity.kt`

```kotlin
package com.gaoshiqi.kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()  // 从 composeApp 模块导入
        }
    }
}
```

#### 1.4 创建/移动 AndroidManifest.xml

**位置:** `androidApp/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.KMP">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.KMP">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

#### 1.5 复制资源文件

```bash
# 复制 drawable、mipmap、values 等资源
cp -r composeApp/src/androidMain/res/* androidApp/src/main/res/
```

---

### 第二步：修改 composeApp 模块

#### 2.1 修改 composeApp/build.gradle.kts

```kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // ❌ 移除: alias(libs.plugins.androidApplication)
    // ✅ 新增:
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    // ❌ 移除 androidTarget 配置块
    // androidTarget {
    //     compilerOptions {
    //         jvmTarget.set(JvmTarget.JVM_11)
    //     }
    // }

    // ✅ Android 配置移到 android {} 块

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts("-lsqlite3")
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        // ❌ 移除 androidMain.dependencies 中的 activity-compose
        // androidMain.dependencies {
        //     implementation(libs.compose.uiToolingPreview)
        //     implementation(libs.androidx.activity.compose)  // 移到 androidApp
        // }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.mediaplayer.kmp)
            implementation(projects.shared)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

// ✅ 新的 Android 配置方式
android {
    namespace = "com.gaoshiqi.kmp.ui"  // 注意：改为 UI 库的命名空间
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Desktop 配置保持不变
compose.desktop {
    application {
        mainClass = "com.gaoshiqi.kmp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.gaoshiqi.kmp"
            packageVersion = "1.0.0"
        }
    }
}
```

#### 2.2 清理 composeApp/src/androidMain/

```bash
# 删除 MainActivity.kt（已移到 androidApp）
rm composeApp/src/androidMain/kotlin/com/gaoshiqi/kmp/MainActivity.kt

# 删除 AndroidManifest.xml（如果存在）
rm composeApp/src/androidMain/AndroidManifest.xml

# 保留其他平台适配代码（如果有）
```

---

### 第三步：修改 shared 模块

#### 3.1 修改 shared/build.gradle.kts

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // ❌ 移除: alias(libs.plugins.androidLibrary)
    // ✅ 新增:
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    // ❌ 移除 androidTarget 配置块
    // androidTarget {
    //     compilerOptions {
    //         jvmTarget.set(JvmTarget.JVM_11)
    //     }
    // }

    iosArm64()
    iosSimulatorArm64()

    jvm()

    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.driver.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.driver.native)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.sqldelight.driver.jvm)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.gaoshiqi.kmp.db")
        }
    }
}

// ✅ 新的 Android 配置方式
android {
    namespace = "com.gaoshiqi.kmp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

---

### 第四步：更新 settings.gradle.kts

```kotlin
rootProject.name = "KMP"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")  // 🆕 新增
include(":composeApp")
include(":server")
include(":shared")
```

---

### 第五步：更新版本目录（可选）

如果需要添加新插件到 `gradle/libs.versions.toml`:

```toml
[plugins]
# 新增
androidKotlinMultiplatformLibrary = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
```

---

## 文件变更清单

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `androidApp/build.gradle.kts` | Android App 模块配置 |
| `androidApp/src/main/AndroidManifest.xml` | Android 清单文件 |
| `androidApp/src/main/kotlin/com/gaoshiqi/kmp/MainActivity.kt` | Android 入口 Activity |
| `androidApp/src/main/res/*` | Android 资源文件 |

### 修改文件

| 文件路径 | 变更内容 |
|---------|---------|
| `settings.gradle.kts` | 添加 `include(":androidApp")` |
| `composeApp/build.gradle.kts` | 替换 `com.android.application` → `com.android.kotlin.multiplatform.library` |
| `shared/build.gradle.kts` | 替换 `com.android.library` → `com.android.kotlin.multiplatform.library` |

### 删除文件

| 文件路径 | 说明 |
|---------|------|
| `composeApp/src/androidMain/kotlin/.../MainActivity.kt` | 移到 androidApp |
| `composeApp/src/androidMain/AndroidManifest.xml` | 移到 androidApp（如果存在） |

---

## 验证检查清单

### 构建验证

- [ ] `./gradlew :androidApp:assembleDebug` - Android App 构建成功
- [ ] `./gradlew :composeApp:build` - KMP UI 模块构建成功
- [ ] `./gradlew :shared:build` - Shared 模块构建成功
- [ ] `./gradlew :server:run` - Server 模块运行正常
- [ ] `./gradlew :composeApp:linkDebugFrameworkIosArm64` - iOS Framework 构建成功
- [ ] `./gradlew :composeApp:jsBrowserDevelopmentRun` - Web 版本运行正常
- [ ] `./gradlew :composeApp:run` - Desktop 版本运行正常

### 功能验证

- [ ] Android App 正常启动
- [ ] Android App 所有页面正常显示
- [ ] Android App 网络请求正常
- [ ] Android App 数据库操作正常
- [ ] iOS App 正常启动（通过 Xcode）
- [ ] Desktop App 正常启动
- [ ] Web App 正常启动

### IDE 验证

- [ ] Android Studio 正确识别所有模块
- [ ] 代码补全正常工作
- [ ] 没有未解析的引用错误

---

## 回滚方案

如果迁移失败，按以下步骤回滚：

### 1. 使用 Git 回滚

```bash
# 查看迁移前的 commit
git log --oneline -10

# 回滚到迁移前的 commit
git checkout <commit-hash> .

# 或者使用 reset（谨慎）
git reset --hard <commit-hash>
```

### 2. 手动回滚

1. 删除 `androidApp/` 目录
2. 从备份恢复 `composeApp/build.gradle.kts`
3. 从备份恢复 `shared/build.gradle.kts`
4. 从备份恢复 `settings.gradle.kts`
5. 恢复 `composeApp/src/androidMain/` 中的文件

### 3. 清理缓存

```bash
./gradlew clean
rm -rf .gradle
rm -rf build
rm -rf */build
```

---

## 常见问题

### Q1: 迁移后 Android Studio 无法识别 androidApp 模块

**解决方案:**
1. File → Sync Project with Gradle Files
2. 如果仍有问题，File → Invalidate Caches / Restart

### Q2: 找不到 `com.android.kotlin.multiplatform.library` 插件

**解决方案:**
确保 AGP 版本 >= 9.0.0，该插件从 AGP 9.0 开始提供。

### Q3: composeApp 中的 Android 代码找不到 Context

**解决方案:**
在迁移后，`composeApp` 是一个库模块，不再有 Application Context。
需要通过依赖注入或参数传递的方式获取 Context。

### Q4: iOS 构建失败

**解决方案:**
1. 清理构建缓存：`./gradlew clean`
2. 重新构建 Framework：`./gradlew :composeApp:linkDebugFrameworkIosArm64`
3. 在 Xcode 中 Clean Build Folder (Cmd+Shift+K)

### Q5: 资源文件找不到

**解决方案:**
确保资源文件正确复制到 `androidApp/src/main/res/` 目录。

---

## 参考链接

- [KMP Project Structure Migration](https://kotl.in/kmp-project-structure-migration)
- [AGP New KMP Plugin](https://kotl.in/gradle/agp-new-kmp)
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-02-06 | 初始版本，基于 AGP 9.0 迁移要求编写 |

---

> **注意**: 本文档基于当前项目结构和官方迁移指南编写。在实际迁移时，请以官方最新文档为准。
