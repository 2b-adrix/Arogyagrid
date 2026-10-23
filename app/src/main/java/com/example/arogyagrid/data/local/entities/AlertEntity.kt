package com.example.arogyagrid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val facilityId: String,
    val type: String,
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val title: String,
    val message: String,
    val isResolved: Boolean,
    val createdAt: Long
)
