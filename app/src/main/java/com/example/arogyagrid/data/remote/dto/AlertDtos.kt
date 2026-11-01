package com.example.arogyagrid.data.remote.dto

data class AlertResponse(
    val id: String,
    val title: String,
    val message: String,
    val severity: String,
    val status: String,
    val facilityId: String?,
    val medicineId: String?,
    val createdAt: String?
)
