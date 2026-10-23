package com.example.arogyagrid.domain.usecase

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.repository.InventoryRepository

class RecordInventoryTransactionUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        facilityId: String,
        medicineId: String,
        quantity: Int,
        type: String, // RECEIVE, ISSUE, ADJUSTMENT
        batchNumber: String? = null,
        expiryDate: String? = null
    ): ApiResponse<Boolean> {
        if (quantity <= 0) {
            return ApiResponse(
                success = false,
                message = "Quantity must be greater than zero"
            )
        }

        return inventoryRepository.recordTransaction(
            facilityId = facilityId,
            medicineId = medicineId,
            quantity = quantity,
            type = type,
            batchNumber = batchNumber,
            expiryDate = expiryDate
        )
    }
}
