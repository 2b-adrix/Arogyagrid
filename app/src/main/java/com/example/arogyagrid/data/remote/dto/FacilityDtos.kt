package com.example.arogyagrid.data.remote.dto

data class FacilityResponse(
    val facilityId: String,
    val name: String,
    val type: String,
    val district: String,
    val subDistrict: String?,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val availableBeds: Int,
    val bedOccupancyPercentage: Double,
    val totalStaff: Int,
    val activeStaff: Int,
    val staffingRatio: Double,
    val address: String?,
    val contactPhone: String?
)
