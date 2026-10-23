package com.example.arogyagrid.data.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.local.entities.InventoryBalanceEntity
import com.example.arogyagrid.data.local.entities.MedicineEntity
import com.example.arogyagrid.data.remote.ApiService
import com.example.arogyagrid.data.remote.SupplyTransactionRequest
import com.example.arogyagrid.domain.models.InventoryItem
import com.example.arogyagrid.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class InventoryRepositoryImpl(
    private val db: AppDatabase,
    private val apiService: ApiService
) : InventoryRepository {

    override fun getInventoryForFacility(facilityId: String): Flow<List<InventoryItem>> {
        return combine(
            db.inventoryDao().getInventoryForFacility(facilityId),
            db.medicineDao().getAllMedicines()
        ) { balances, medicines ->
            val medMap = medicines.associateBy { it.id }
            balances.map { bal ->
                val med = medMap[bal.medicineId]
                val medName = med?.name ?: bal.medicineId
                val category = med?.category ?: "Medicine"
                val unit = med?.unit ?: "units"
                val leadTime = med?.leadTimeDays ?: 7

                val riskLevel = when {
                    bal.coverageDays < leadTime -> "CRITICAL"
                    bal.coverageDays < (leadTime * 1.5) -> "HIGH"
                    bal.coverageDays < (leadTime * 2.5) -> "MEDIUM"
                    else -> "LOW"
                }

                InventoryItem(
                    facilityId = bal.facilityId,
                    medicineId = bal.medicineId,
                    medicineName = medName,
                    category = category,
                    unit = unit,
                    closingStock = bal.closingStock,
                    averageDailyConsumption = bal.averageDailyConsumption,
                    coverageDays = bal.coverageDays,
                    stockGap = bal.stockGap,
                    riskLevel = riskLevel
                )
            }
        }
    }

    override suspend fun recordTransaction(
        facilityId: String,
        medicineId: String, // This is the SKU
        quantity: Int,
        type: String,
        batchNumber: String?,
        expiryDate: String?
    ): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Call the Node.js Backend API
            apiService.recordSupplyTransaction(
                id = medicineId, // Now passing SKU
                request = SupplyTransactionRequest(
                    transactionType = type,
                    quantity = quantity
                )
            )
            
            // Update local DB to reflect changes immediately
            val current = db.inventoryDao().getInventoryItem(facilityId, medicineId)
            if (current != null) {
                val newStock = when (type) {
                    "RECEIVE" -> current.closingStock + quantity
                    "ISSUE" -> (current.closingStock - quantity).coerceAtLeast(0)
                    else -> quantity
                }
                val newCov = if (current.averageDailyConsumption > 0) newStock / current.averageDailyConsumption else 30.0
                db.inventoryDao().insertOrUpdateBalances(
                    listOf(current.copy(closingStock = newStock, coverageDays = newCov, updatedAt = System.currentTimeMillis()))
                )
            }
            ApiResponse(success = true, data = true, message = "Transaction recorded successfully")
        } catch (e: Exception) {
            ApiResponse(success = false, data = false, message = "Network error: ${e.message}")
        }
    }

    override suspend fun refreshInventory(facilityId: String) = withContext(Dispatchers.IO) {
        try {
            // Fetch from the Node.js Backend
            val supplies = apiService.getSupplies()
            
            val medicineEntities = supplies.map { supply ->
                MedicineEntity(
                    id = supply.sku,
                    name = supply.name,
                    category = supply.category,
                    unit = supply.unit,
                    criticalityLevel = "MEDIUM",
                    reorderLevel = supply.minimumStockLevel,
                    leadTimeDays = 7
                )
            }
            
            val balanceEntities = supplies.map { supply ->
                val dailyConsumption = 30.0 // Default assumption
                InventoryBalanceEntity(
                    facilityId = facilityId,
                    medicineId = supply.sku,
                    closingStock = supply.currentStock,
                    averageDailyConsumption = dailyConsumption,
                    coverageDays = supply.currentStock / dailyConsumption,
                    stockGap = supply.minimumStockLevel - supply.currentStock,
                    updatedAt = System.currentTimeMillis()
                )
            }
            
            // Clear local dummy data to ensure it's fully dynamic
            db.inventoryDao().clearInventoryForFacility(facilityId)
            
            // Save to local cache
            db.medicineDao().insertMedicines(medicineEntities)
            db.inventoryDao().insertOrUpdateBalances(balanceEntities)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
