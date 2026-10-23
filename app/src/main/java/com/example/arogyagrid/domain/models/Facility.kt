package com.example.arogyagrid.domain.models

data class Facility(
    val id: String,
    val name: String,
    val district: String,
    val facilityType: String,
    val latitude: Double,
    val longitude: Double,
    val totalBeds: Int,
    val occupiedBeds: Int
) {
    val occupancyPercentage: Int
        get() = if (totalBeds > 0) (occupiedBeds * 100 / totalBeds) else 0
}
