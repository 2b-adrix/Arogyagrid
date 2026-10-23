package com.example.arogyagrid

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.repository.InventoryRepository
import com.example.arogyagrid.domain.repository.RedistributionRepository
import com.example.arogyagrid.domain.usecase.ApproveRedistributionUseCase
import com.example.arogyagrid.domain.usecase.RecordInventoryTransactionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartHealthUnitTest {

    @Test
    fun testRecordTransactionZeroQuantityFails() = runBlocking {
        val fakeRepo = object : InventoryRepository {
            override fun getInventoryForFacility(facilityId: String) = throw NotImplementedError()
            override suspend fun recordTransaction(facilityId: String, medicineId: String, quantity: Int, type: String, batchNumber: String?, expiryDate: String?): ApiResponse<Boolean> {
                return ApiResponse(success = true, data = true)
            }
            override suspend fun refreshInventory(facilityId: String) {}
        }

        val useCase = RecordInventoryTransactionUseCase(fakeRepo)
        val result = useCase(
            facilityId = "F-3",
            medicineId = "M-101",
            quantity = 0,
            type = "RECEIVE"
        )

        assertFalse(result.success)
        assertEquals("Quantity must be greater than zero", result.message)
    }

    @Test
    fun testApproveRedistribution() = runBlocking {
        var approved = false
        val fakeRepo = object : RedistributionRepository {
            override fun getRecommendations() = throw NotImplementedError()
            override suspend fun approveRecommendation(id: String): ApiResponse<Boolean> {
                approved = true
                return ApiResponse(success = true, data = true, message = "Approved")
            }
            override suspend fun rejectRecommendation(id: String): ApiResponse<Boolean> {
                return ApiResponse(success = true, data = false)
            }
            override suspend fun refreshRecommendations() {}
        }

        val useCase = ApproveRedistributionUseCase(fakeRepo)
        val result = useCase("REC-2026-001", approve = true)

        assertTrue(result.success)
        assertTrue(approved)
    }
}
