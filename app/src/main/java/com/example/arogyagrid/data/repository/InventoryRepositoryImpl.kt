package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.remote.dto.InventoryTransactionRequest
import com.example.arogyagrid.domain.model.InventoryItem

class InventoryRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getInventory(
        facilityId: String? = null,
        district: String? = null
    ): Result<List<InventoryItem>> {
        return try {
            val response = api.getInventory(facilityId, district)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { i ->
                    InventoryItem(
                        facilityId = i.facilityId,
                        facilityName = i.facilityName,
                        medicineId = i.medicineId,
                        medicineName = i.medicineName,
                        criticality = i.criticality,
                        closingStock = i.closingStock,
                        avgDailyConsumption = i.avgDailyConsumption,
                        coverageDays = i.coverageDays,
                        stockGap = i.stockGap,
                        minimumStock = i.minimumStock,
                        maximumStock = i.maximumStock,
                        batchNumber = i.batchNumber,
                        expiryDate = i.expiryDate
                    )
                })
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordTransaction(
        facilityId: String,
        medicineId: String,
        type: String,
        quantity: Int,
        batchNumber: String? = null,
        remarks: String? = null
    ): Result<Boolean> {
        return try {
            val response = api.recordTransaction(
                InventoryTransactionRequest(
                    facilityId, medicineId, type, quantity, batchNumber, remarks
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
