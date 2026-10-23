package com.example.arogyagrid.domain.models

data class Medicine(
    val id: String,
    val name: String,
    val category: String,
    val unit: String,
    val criticalityLevel: String,
    val reorderLevel: Int,
    val leadTimeDays: Int
)
