package com.example.arogyagrid.domain.models

data class InventoryItem(
    val facilityId: String,
    val medicineId: String,
    val medicineName: String,
    val category: String,
    val unit: String,
    val closingStock: Int,
    val averageDailyConsumption: Double,
    val coverageDays: Double,
    val stockGap: Int,
    val riskLevel: String // LOW, MEDIUM, HIGH, CRITICAL
)
