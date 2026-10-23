package com.example.arogyagrid.domain.models

data class RiskItem(
    val facilityId: String,
    val facilityName: String,
    val medicineId: String,
    val medicineName: String,
    val riskLevel: String,
    val riskScore: Double,
    val daysToStockout: Int,
    val reason: String
)
