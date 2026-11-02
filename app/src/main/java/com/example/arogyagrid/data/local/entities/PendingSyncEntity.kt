package com.example.arogyagrid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an offline write operation waiting to be pushed to the central backend.
 * Conforms to the Hybrid Offline Sync Architecture (Section 08).
 */
@Entity(tableName = "pending_sync_queue")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val syncType: String, // "TRANSACTION", "APPROVAL", "STOCK_ADJUSTMENT"
    val payloadJson: String,
    val status: String = "PENDING", // PENDING, SYNCING, COMPLETED, FAILED
    val retryCount: Int = 0,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
