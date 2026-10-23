package com.example.arogyagrid.domain.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.models.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventoryForFacility(facilityId: String): Flow<List<InventoryItem>>
    suspend fun recordTransaction(
        facilityId: String,
        medicineId: String,
        quantity: Int,
        type: String,
        batchNumber: String? = null,
        expiryDate: String? = null
    ): ApiResponse<Boolean>
    suspend fun refreshInventory(facilityId: String)
}
