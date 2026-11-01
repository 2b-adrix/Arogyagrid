package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.RiskItem

class RiskRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getRisks(
        severity: String? = null,
        facilityId: String? = null,
        district: String? = null
    ): Result<List<RiskItem>> {
        return try {
            val response = api.getRisks(severity, facilityId, district)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { r ->
                    RiskItem(
                        id = r.id, facilityId = r.facilityId,
                        facilityName = r.facilityName,
                        medicineId = r.medicineId,
                        medicineName = r.medicineName,
                        severity = r.severity, riskScore = r.riskScore,
                        daysToStockout = r.daysToStockout,
                        currentStock = r.currentStock,
                        requiredStock = r.requiredStock,
                        deficit = r.deficit,
                        recommendation = r.recommendation,
                        assessedAt = r.assessedAt
                    )
                })
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
