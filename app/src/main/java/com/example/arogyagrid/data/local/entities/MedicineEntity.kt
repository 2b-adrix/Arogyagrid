package com.example.arogyagrid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val unit: String,
    val criticalityLevel: String,
    val reorderLevel: Int,
    val leadTimeDays: Int
)
