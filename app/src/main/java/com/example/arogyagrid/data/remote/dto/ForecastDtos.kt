package com.example.arogyagrid.data.remote.dto

data class ForecastResponse(
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
