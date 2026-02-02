package com.gaoshiqi.kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gaoshiqi.kmp.db.DatabaseDriverFactory
import com.gaoshiqi.kmp.db.DatabaseProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 初始化数据库
        // Android 需要传入 Context 来访问应用的数据库目录
        DatabaseProvider.init(DatabaseDriverFactory(applicationContext))

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}