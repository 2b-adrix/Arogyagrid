package com.example.arogyagrid.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arogyagrid.core.LanguageManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.ai.AiAssistantScreen
import com.example.arogyagrid.presentation.audit.AuditLogsScreen
import com.example.arogyagrid.presentation.auth.LoginScreen
import com.example.arogyagrid.presentation.coldchain.ColdChainScreen
import com.example.arogyagrid.presentation.dashboard.DashboardScreen
import com.example.arogyagrid.presentation.facility.RegisterFacilityScreen
import com.example.arogyagrid.presentation.forecast.ForecastScreen
import com.example.arogyagrid.presentation.inventory.InventoryScreen
import com.example.arogyagrid.presentation.map.HospitalMapScreen
import com.example.arogyagrid.presentation.outbreak.EpidemicSurveillanceScreen
import com.example.arogyagrid.presentation.profile.ProfileScreen
import com.example.arogyagrid.presentation.redistribution.RedistributionReviewScreen
import com.example.arogyagrid.presentation.resources.BedsStaffScreen
import com.example.arogyagrid.presentation.risk.MedicineRiskScreen
import com.example.arogyagrid.presentation.splash.SplashScreen
import com.example.arogyagrid.presentation.viewmodel.*

sealed class NavItem(val route: String, val labelHi: String, val labelEn: String, val icon: ImageVector) {
    object Dashboard : NavItem("dashboard", "डैशबोर्ड", "Center", Icons.Default.Dashboard)
    object MedicineRisk : NavItem("medicine_risk", "जोखिम", "Risk", Icons.Default.Warning)
    object Inventory : NavItem("inventory", "इन्वेंटरी", "Inventory", Icons.Default.Inventory)
    object Redistribution : NavItem("redistribution", "अनुमोदन", "Approval", Icons.Default.SwapHoriz)
    object Forecast : NavItem("forecast", "पूर्वानुमान", "Forecast", Icons.AutoMirrored.Filled.TrendingUp)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModels
    val authViewModel = remember { AuthViewModel() }
    val dashboardViewModel = remember { DashboardViewModel() }
    val riskViewModel = remember { RiskViewModel() }
    val inventoryViewModel = remember { InventoryViewModel() }
    val redistributionViewModel = remember { RedistributionViewModel() }
    val forecastViewModel = remember { ForecastViewModel() }
    val aiAssistantViewModel = remember { AiAssistantViewModel() }
    val auditViewModel = remember { AuditViewModel() }
    val bedsStaffViewModel = remember { BedsStaffViewModel() }

    val navItems = listOf(
        NavItem.Dashboard,
        NavItem.MedicineRisk,
        NavItem.Inventory,
        NavItem.Redistribution,
        NavItem.Forecast
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (UserSession.isLoggedIn) "dashboard" else "login"

    val currentLang by LanguageManager.currentLanguage.collectAsState()

    val onLogoutAction: () -> Unit = {
        UserSession.logout()
        authViewModel.logout()
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }
    val onNavigateToLoginAction: () -> Unit = {
        navController.navigate("login")
    }
    val onNavigateToProfileAction: () -> Unit = {
        navController.navigate("profile")
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "splash" && currentRoute != "register_facility" && currentRoute != "hospital_map" && currentRoute != "cold_chain" && currentRoute != "outbreak_surveillance") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 3.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        val label = if (currentLang == com.example.arogyagrid.core.AppLanguage.HINDI) item.labelHi else item.labelEn
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = label) },
                            label = {
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(
                    onSplashFinished = {
                        val nextScreen = if (UserSession.isLoggedIn) "dashboard" else "login"
                        navController.navigate(nextScreen) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("dashboard") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable("dashboard") {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToRedistribution = {
                        navController.navigate("redistribution")
                    },
                    onNavigateToScreen = { route ->
                        if (route == "login") {
                            onLogoutAction()
                        } else {
                            navController.navigate(route)
                        }
                    }
                )
            }
            composable("medicine_risk") {
                MedicineRiskScreen(
                    viewModel = riskViewModel,
                    onNavigateToProfile = onNavigateToProfileAction,
                    onLogout = onLogoutAction,
                    onNavigateToLogin = onNavigateToLoginAction
                )
            }
            composable("inventory") {
                InventoryScreen(
                    viewModel = inventoryViewModel,
                    onNavigateToProfile = onNavigateToProfileAction,
                    onLogout = onLogoutAction,
                    onNavigateToLogin = onNavigateToLoginAction
                )
            }
            composable("redistribution") {
                RedistributionReviewScreen(
                    viewModel = redistributionViewModel,
                    onNavigateToProfile = onNavigateToProfileAction,
                    onLogout = onLogoutAction,
                    onNavigateToLogin = onNavigateToLoginAction
                )
            }
            composable("forecast") {
                ForecastScreen(
                    viewModel = forecastViewModel,
                    onNavigateToProfile = onNavigateToProfileAction,
                    onLogout = onLogoutAction,
                    onNavigateToLogin = onNavigateToLoginAction
                )
            }
            composable("ai_assistant") {
                AiAssistantScreen(
                    viewModel = aiAssistantViewModel,
                    onNavigateToScreen = { route ->
                        navController.navigate(route)
                    }
                )
            }
            composable("audit_logs") {
                AuditLogsScreen(viewModel = auditViewModel)
            }
            composable("beds_staff") {
                BedsStaffScreen(viewModel = bedsStaffViewModel)
            }
            composable("profile") {
                ProfileScreen(
                    facilityRepository = null,
                    onNavigateToRegisterFacility = {
                        navController.navigate("register_facility")
                    },
                    onNavigateToMap = {
                        navController.navigate("hospital_map")
                    },
                    onLogout = onLogoutAction
                )
            }
            composable("register_facility") {
                RegisterFacilityScreen(
                    facilityRepository = null,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("hospital_map") {
                HospitalMapScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = onLogoutAction,
                    onNavigateToLogin = onNavigateToLoginAction
                )
            }
            composable("cold_chain") {
                ColdChainScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("outbreak_surveillance") {
                EpidemicSurveillanceScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToMap = {
                        navController.navigate("hospital_map")
                    },
                    onNavigateToTransfer = {
                        navController.navigate("redistribution")
                    }
                )
            }
        }
    }
}
