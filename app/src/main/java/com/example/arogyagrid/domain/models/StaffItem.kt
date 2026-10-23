package com.example.arogyagrid.domain.models

data class StaffItem(
    val facilityId: String,
    val facilityName: String,
    val district: String,
    val requiredStaff: Int,
    val availableStaff: Int,
    val staffGap: Int
) {
    val availabilityPercentage: Int
        get() = if (requiredStaff > 0) (availableStaff * 100 / requiredStaff) else 0
}
