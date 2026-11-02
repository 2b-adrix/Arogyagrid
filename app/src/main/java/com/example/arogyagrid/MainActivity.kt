package com.example.arogyagrid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.navigation.AppNavigation
import com.example.arogyagrid.presentation.theme.SmartHealthTheme
import com.example.arogyagrid.sync.SyncManager
import com.example.arogyagrid.util.NetworkMonitor
import com.example.arogyagrid.util.NotificationHelper
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.init(this)

        // Initialize WorkManager Hybrid Sync Engine
        SyncManager.schedulePeriodicSync(this)

        // Initialize Local Notification Channels & Network Monitor
        NotificationHelper.initNotificationChannels(this)
        NetworkMonitor.startMonitoring(this)

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Initialize Osmdroid Configuration with compliant User-Agent to prevent 403/404 blocks
        Configuration.getInstance().apply {
            load(applicationContext, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = "ArogyaGridApp/1.0 (Android; com.example.arogyagrid; contact@arogyagrid.com)"
        }

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
