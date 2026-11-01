package com.example.arogyagrid.domain.model

// ── Dashboard ─────────────────────────────────
data class DashboardSummary(
    val totalFacilities: Long = 0,
    val totalBeds: Int = 0,
    val occupiedBeds: Int = 0,
    val bedOccupancyPercentage: Double = 0.0,
    val criticalRisks: Long = 0,
    val highRisks: Long = 0,
    val activeAlerts: Long = 0,
    val pendingRedistributions: Long = 0,
    val topRisks: List<RiskItem> = emptyList(),
    val recentAlerts: List<AlertItem> = emptyList()
)

// ── Facility ──────────────────────────────────
data class Facility(
    val facilityId: String,
    val name: String,
    val type: String,
    val district: String,
    val subDistrict: String?,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val availableBeds: Int,
    val bedOccupancyPercentage: Double,
    val totalStaff: Int,
    val activeStaff: Int,
    val staffingRatio: Double,
    val address: String?,
    val contactPhone: String?
)

// ── Medicine / Inventory ──────────────────────
data class InventoryItem(
    val facilityId: String,
    val facilityName: String,
    val medicineId: String,
    val medicineName: String,
    val criticality: String,
    val closingStock: Int,
    val avgDailyConsumption: Double,
    val coverageDays: Int,
    val stockGap: Int,
    val minimumStock: Int,
    val maximumStock: Int,
    val batchNumber: String?,
    val expiryDate: String?
)

// ── Risk ──────────────────────────────────────
data class RiskItem(
    val id: String,
    val facilityId: String,
    val facilityName: String,
    val medicineId: String,
    val medicineName: String,
    val severity: String,
    val riskScore: Double,
    val daysToStockout: Int,
    val currentStock: Int,
    val requiredStock: Int,
    val deficit: Int,
    val recommendation: String?,
    val assessedAt: String?
)

// ── Alert ─────────────────────────────────────
data class AlertItem(
    val id: String,
    val title: String,
    val message: String,
    val severity: String,
    val status: String,
    val facilityId: String?,
    val medicineId: String?,
    val createdAt: String?
)

// ── Redistribution ────────────────────────────
data class RedistributionItem(
    val recommendationId: String,
    val sourceFacilityId: String,
    val sourceFacilityName: String,
    val targetFacilityId: String,
    val targetFacilityName: String,
    val medicineId: String,
    val medicineName: String,
    val transferQuantity: Int,
    val sourceSurplus: Int,
    val targetDeficit: Int,
    val priorityScore: Double,
    val status: String,
    val justification: String?,
    val createdAt: String?
)

// ── Forecast ──────────────────────────────────
data class ForecastItem(
    val medicineId: String,
    val medicineName: String,
    val facilityId: String,
    val facilityName: String,
    val currentDailyConsumption: Double,
    val predictedDailyConsumption: Double,
    val currentStock: Int,
    val predictedDaysToStockout: Int,
    val weeklyForecast: List<Double>,
    val trendDirection: String
)

// ── Beds & Staff ──────────────────────────────
data class BedsStaffSummary(
    val totalBeds: Int,
    val occupiedBeds: Int,
    val availableBeds: Int,
    val occupancyPercentage: Double,
    val totalStaff: Int,
    val activeStaff: Int,
    val staffingRatio: Double,
    val facilities: List<Facility>
)

// ── Audit Log ─────────────────────────────────
data class AuditLogItem(
    val id: String,
    val action: String,
    val entityType: String,
    val entityId: String?,
    val performedBy: String?,
    val userRole: String?,
    val details: String?,
    val timestamp: String?
)

// ── AI Chat ───────────────────────────────────
data class AiChatResult(
    val reply: String,
    val dataSources: List<String>,
    val grounded: Boolean
)

data class AuthResult(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String
)
