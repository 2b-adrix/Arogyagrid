package com.example.arogyagrid.data.remote.dto

data class DashboardSummaryResponse(
    val totalFacilities: Long,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val bedOccupancyPercentage: Double,
    val criticalRisks: Long,
    val highRisks: Long,
    val activeAlerts: Long,
    val pendingRedistributions: Long,
    val topRisks: List<RiskResponse>,
    val recentAlerts: List<AlertResponse>
)
