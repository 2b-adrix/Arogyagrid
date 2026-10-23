package com.example.arogyagrid.data.local.entities

import androidx.room.Entity

@Entity(tableName = "risks", primaryKeys = ["facilityId", "medicineId"])
data class RiskEntity(
    val facilityId: String,
    val medicineId: String,
    val riskLevel: String, // LOW, MEDIUM, HIGH, CRITICAL
    val riskScore: Double,
    val daysToStockout: Int,
    val reason: String,
    val updatedAt: Long
)
