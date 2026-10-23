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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arogyagrid.data.SeedData
import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.repository.AiRepositoryImpl
import com.example.arogyagrid.data.repository.FacilityRepositoryImpl
import com.example.arogyagrid.data.repository.InventoryRepositoryImpl
import com.example.arogyagrid.data.repository.RedistributionRepositoryImpl
import com.example.arogyagrid.data.repository.RiskRepositoryImpl
import com.example.arogyagrid.domain.usecase.ApproveRedistributionUseCase
import com.example.arogyagrid.domain.usecase.AskGeminiAssistantUseCase
import com.example.arogyagrid.domain.usecase.GetDashboardDataUseCase
import com.example.arogyagrid.domain.usecase.RecordInventoryTransactionUseCase
import com.example.arogyagrid.presentation.ai.AiAssistantScreen
import com.example.arogyagrid.presentation.ai.AiAssistantViewModel
import com.example.arogyagrid.presentation.audit.AuditLogsScreen
import com.example.arogyagrid.presentation.auth.LoginScreen
import com.example.arogyagrid.presentation.dashboard.DashboardScreen
import com.example.arogyagrid.presentation.dashboard.DashboardViewModel
import com.example.arogyagrid.presentation.facility.RegisterFacilityScreen
import com.example.arogyagrid.presentation.forecast.ForecastScreen
import com.example.arogyagrid.presentation.inventory.InventoryScreen
import com.example.arogyagrid.presentation.inventory.InventoryViewModel
import com.example.arogyagrid.presentation.profile.ProfileScreen
import com.example.arogyagrid.presentation.redistribution.RedistributionReviewScreen
import com.example.arogyagrid.presentation.redistribution.RedistributionViewModel
import com.example.arogyagrid.presentation.resources.BedsStaffScreen
import com.example.arogyagrid.presentation.risk.MedicineRiskScreen
import com.example.arogyagrid.presentation.splash.SplashScreen

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
    val context = LocalContext.current

    // Initialize DB & Repositories
    val db = remember { AppDatabase.getDatabase(context) }
    LaunchedEffect(Unit) {
        SeedData.seedDatabaseIfEmpty(db)
    }

    val apiService = NetworkClient.apiService
    val facilityRepo = remember { FacilityRepositoryImpl(db, apiService) }
    val inventoryRepo = remember { InventoryRepositoryImpl(db, apiService) }
    val riskRepo = remember { RiskRepositoryImpl(db, apiService) }
    val redistributionRepo = remember { RedistributionRepositoryImpl(db, apiService) }
    val aiRepo = remember { AiRepositoryImpl(db, apiService) }

    // Use Cases
    val getDashboardDataUseCase = remember { GetDashboardDataUseCase(facilityRepo, riskRepo, redistributionRepo) }
    val recordInventoryTransactionUseCase = remember { RecordInventoryTransactionUseCase(inventoryRepo) }
    val approveRedistributionUseCase = remember { ApproveRedistributionUseCase(redistributionRepo) }
    val askGeminiAssistantUseCase = remember { AskGeminiAssistantUseCase(aiRepo) }

    // ViewModels
    val dashboardViewModel = remember { DashboardViewModel(getDashboardDataUseCase) }
    val inventoryViewModel = remember { InventoryViewModel(inventoryRepo, recordInventoryTransactionUseCase) }
    val redistributionViewModel = remember { RedistributionViewModel(redistributionRepo, approveRedistributionUseCase) }
    val aiAssistantViewModel = remember { AiAssistantViewModel(askGeminiAssistantUseCase) }

    val navItems = listOf(
        NavItem.Dashboard,
        NavItem.MedicineRisk,
        NavItem.Inventory,
        NavItem.Redistribution,
        NavItem.Forecast
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { fadeIn(animationSpec = tween(400)) },
            popExitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            composable("splash") {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { _, _ ->
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
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
                MedicineRiskScreen(viewModel = dashboardViewModel)
            }
            composable("inventory") {
                InventoryScreen(viewModel = inventoryViewModel)
            }
            composable("redistribution") {
                RedistributionReviewScreen(viewModel = redistributionViewModel)
            }
            composable("forecast") {
                ForecastScreen()
            }
            composable("beds_staff") {
                BedsStaffScreen(viewModel = dashboardViewModel)
            }
            composable("ai_assistant") {
                AiAssistantScreen(viewModel = aiAssistantViewModel)
            }
            composable("profile") {
                ProfileScreen(
                    facilityRepository = facilityRepo,
                    onNavigateToRegisterFacility = {
                        navController.navigate("register_facility")
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable("register_facility") {
                RegisterFacilityScreen(
                    facilityRepository = facilityRepo,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("audit_logs") {
                AuditLogsScreen()
            }
        }
    }
}
