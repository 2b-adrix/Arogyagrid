package com.example.arogyagrid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facilities")
data class FacilityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val district: String,
    val facilityType: String, // District Hospital or PHC
    val latitude: Double,
    val longitude: Double,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val updatedAt: Long
)
