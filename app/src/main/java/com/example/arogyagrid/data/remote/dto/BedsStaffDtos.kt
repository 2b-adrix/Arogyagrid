package com.example.arogyagrid.data.remote.dto

data class BedsStaffResponse(
    val totalBeds: Int,
    val occupiedBeds: Int,
    val availableBeds: Int,
    val occupancyPercentage: Double,
    val totalStaff: Int,
    val activeStaff: Int,
    val staffingRatio: Double,
    val facilities: List<FacilityResponse>
)
