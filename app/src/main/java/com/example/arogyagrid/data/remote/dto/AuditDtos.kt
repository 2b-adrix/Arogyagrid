package com.example.arogyagrid.data.remote.dto

data class AuditLogResponse(
    val id: String,
    val action: String,
    val entityType: String,
    val entityId: String?,
    val performedBy: String?,
    val userRole: String?,
    val details: String?,
    val timestamp: String?
)
