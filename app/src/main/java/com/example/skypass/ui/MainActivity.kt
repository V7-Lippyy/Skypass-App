// MainActivity.kt
package com.example.skypass.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.skypass.ui.navigation.SkyPassNavGraph
import com.example.skypass.ui.theme.SkyPassTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SkyPassTheme {
                SkyPassApp()
            }
        }
    }
}

@Composable
fun SkyPassApp() {
    val navController = rememberNavController()
    SkyPassNavGraph(navController = navController)
}