package com.example.arogyagrid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "redistribution_recommendations")
data class RedistributionEntity(
    @PrimaryKey val id: String,
    val medicineId: String,
    val sourceFacilityId: String,
    val destinationFacilityId: String,
    val recommendedQuantity: Int,
    val distanceKm: Double,
    val status: String, // PENDING_APPROVAL, APPROVED, REJECTED, COMPLETED
    val reason: String,
    val createdAt: Long
)
