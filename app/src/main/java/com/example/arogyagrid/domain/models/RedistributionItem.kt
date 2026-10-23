package com.example.arogyagrid.domain.models

data class RedistributionItem(
    val id: String,
    val medicineId: String,
    val medicineName: String,
    val sourceFacilityId: String,
    val sourceFacilityName: String,
    val destinationFacilityId: String,
    val destinationFacilityName: String,
    val recommendedQuantity: Int,
    val distanceKm: Double,
    val status: String,
    val reason: String
)
