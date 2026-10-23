package com.example.arogyagrid.data.local.entities

import androidx.room.Entity

@Entity(tableName = "inventory_balances", primaryKeys = ["facilityId", "medicineId"])
data class InventoryBalanceEntity(
    val facilityId: String,
    val medicineId: String,
    val closingStock: Int,
    val averageDailyConsumption: Double,
    val coverageDays: Double,
    val stockGap: Int,
    val updatedAt: Long
)
