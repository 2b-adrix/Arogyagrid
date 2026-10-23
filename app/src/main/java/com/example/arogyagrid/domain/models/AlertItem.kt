package com.example.arogyagrid.domain.models

data class AlertItem(
    val id: String,
    val facilityId: String,
    val facilityName: String,
    val type: String,
    val severity: String,
    val title: String,
    val message: String,
    val isResolved: Boolean,
    val createdAtFormatted: String
)
