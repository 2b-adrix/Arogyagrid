package com.example.arogyagrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.navigation.AppNavigation
import com.example.arogyagrid.presentation.theme.SmartHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.init(this)
        enableEdgeToEdge()
        setContent {
            val themeMode by ThemeManager.themeMode.collectAsState()
            
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            SmartHealthTheme(darkTheme = isDark) {
                AppNavigation()
            }
        }
    }
}
