package com.example.arogyagrid.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.ai.AiAssistantScreen
import com.example.arogyagrid.presentation.audit.AuditLogsScreen
import com.example.arogyagrid.presentation.auth.LoginScreen
import com.example.arogyagrid.presentation.dashboard.DashboardScreen
import com.example.arogyagrid.presentation.facility.RegisterFacilityScreen
import com.example.arogyagrid.presentation.forecast.ForecastScreen
import com.example.arogyagrid.presentation.inventory.InventoryScreen
import com.example.arogyagrid.presentation.profile.ProfileScreen
import com.example.arogyagrid.presentation.redistribution.RedistributionReviewScreen
import com.example.arogyagrid.presentation.resources.BedsStaffScreen
import com.example.arogyagrid.presentation.risk.MedicineRiskScreen
import com.example.arogyagrid.presentation.splash.SplashScreen
import com.example.arogyagrid.presentation.viewmodel.AiAssistantViewModel
import com.example.arogyagrid.presentation.viewmodel.AuditViewModel
import com.example.arogyagrid.presentation.viewmodel.AuthViewModel
import com.example.arogyagrid.presentation.viewmodel.BedsStaffViewModel
import com.example.arogyagrid.presentation.viewmodel.DashboardViewModel
import com.example.arogyagrid.presentation.viewmodel.ForecastViewModel
import com.example.arogyagrid.presentation.viewmodel.InventoryViewModel
import com.example.arogyagrid.presentation.viewmodel.RedistributionViewModel
import com.example.arogyagrid.presentation.viewmodel.RiskViewModel

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : NavItem("dashboard", "Center", Icons.Default.Dashboard)
    object MedicineRisk : NavItem("medicine_risk", "Risk", Icons.Default.MedicalServices)
    object Inventory : NavItem("inventory", "Inventory", Icons.Default.Inventory)
    object Redistribution : NavItem("redistribution", "Approval", Icons.Default.SwapHoriz)
    object Forecast : NavItem("forecast", "Forecast", Icons.Default.BarChart)
    object BedsStaff : NavItem("beds_staff", "Capacity", Icons.Default.Hotel)
    object AiAssistant : NavItem("ai_assistant", "Copilot", Icons.Default.AutoAwesome)
    object Profile : NavItem("profile", "Profile", Icons.Default.Person)
    object RegisterFacility : NavItem("register_facility", "Register PHC", Icons.Default.AddBusiness)
    object AuditLogs : NavItem("audit_logs", "Audit", Icons.AutoMirrored.Filled.ListAlt)
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
    val bedsStaffViewModel = remember { BedsStaffViewModel() }
    val aiAssistantViewModel = remember { AiAssistantViewModel() }
    val auditViewModel = remember { AuditViewModel() }

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

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "splash" && currentRoute != "register_facility") {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { fadeIn(animationSpec = tween(400)) },
            popExitTransition = { fadeOut(animationSpec = tween(400)) }
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
                        navController.navigate(route)
                    }
                )
            }
            composable("medicine_risk") {
                MedicineRiskScreen(viewModel = riskViewModel)
            }
            composable("inventory") {
                InventoryScreen(viewModel = inventoryViewModel)
            }
            composable("redistribution") {
                RedistributionReviewScreen(viewModel = redistributionViewModel)
            }
            composable("forecast") {
                ForecastScreen(viewModel = forecastViewModel)
            }
            composable("beds_staff") {
                BedsStaffScreen(viewModel = bedsStaffViewModel)
            }
            composable("ai_assistant") {
                AiAssistantScreen(viewModel = aiAssistantViewModel)
            }
            composable("profile") {
                ProfileScreen(
                    facilityRepository = null,
                    onNavigateToRegisterFacility = {
                        navController.navigate("register_facility")
                    },
                    onLogout = {
                        UserSession.logout()
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
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
            composable("audit_logs") {
                AuditLogsScreen(viewModel = auditViewModel)
            }
        }
    }
}
