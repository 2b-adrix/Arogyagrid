package com.example.arogyagrid.data.remote.dto

data class RiskResponse(
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
