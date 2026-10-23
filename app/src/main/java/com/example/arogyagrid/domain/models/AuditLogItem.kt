package com.example.arogyagrid.domain.models

data class AuditLogItem(
    val id: String,
    val timeFormatted: String,
    val action: String,
    val actor: String,
    val entityReference: String
)
