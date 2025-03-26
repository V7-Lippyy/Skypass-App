// ui/splash/SplashScreen.kt
package com.example.skypass.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.skypass.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    // Check if the system is in dark theme
    val isDarkTheme = isSystemInDarkTheme()

    // Choose logo based on the theme
    val logoResId = if (isDarkTheme) {
        R.drawable.logo_dark
    } else {
        R.drawable.logo_light
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )
    }

    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateToHome()
    }
}