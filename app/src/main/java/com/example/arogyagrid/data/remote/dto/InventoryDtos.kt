package com.example.arogyagrid.data.remote.dto

data class InventoryResponse(
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

data class InventoryTransactionRequest(
    val facilityId: String,
    val medicineId: String,
    val type: String,
    val quantity: Int,
    val batchNumber: String? = null,
    val remarks: String? = null
)
