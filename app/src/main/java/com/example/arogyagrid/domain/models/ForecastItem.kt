package com.example.arogyagrid.domain.models

data class ForecastItem(
    val day: String,
    val actualDemand: Int,
    val predictedDemand: Int,
    val lowerBound: Int,
    val upperBound: Int
)
