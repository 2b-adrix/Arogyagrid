package com.example.arogyagrid.data.remote.dto

data class RedistributionResponse(
    val recommendationId: String,
    val sourceFacilityId: String,
    val sourceFacilityName: String,
    val targetFacilityId: String,
    val targetFacilityName: String,
    val medicineId: String,
    val medicineName: String,
    val transferQuantity: Int,
    val sourceSurplus: Int,
    val targetDeficit: Int,
    val priorityScore: Double,
    val status: String,
    val justification: String?,
    val createdAt: String?
)

data class RedistributionApprovalRequest(
    val recommendationId: String,
    val approved: Boolean,
    val remarks: String? = null
)
