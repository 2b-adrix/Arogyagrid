package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.remote.dto.RedistributionApprovalRequest
import com.example.arogyagrid.domain.model.RedistributionItem

class RedistributionRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getRedistributions(
        pendingOnly: Boolean = false
    ): Result<List<RedistributionItem>> {
        return try {
            val response = api.getRedistributions(pendingOnly)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { r ->
                    RedistributionItem(
                        recommendationId = r.recommendationId,
                        sourceFacilityId = r.sourceFacilityId,
                        sourceFacilityName = r.sourceFacilityName,
                        targetFacilityId = r.targetFacilityId,
                        targetFacilityName = r.targetFacilityName,
                        medicineId = r.medicineId,
                        medicineName = r.medicineName,
                        transferQuantity = r.transferQuantity,
                        sourceSurplus = r.sourceSurplus,
                        targetDeficit = r.targetDeficit,
                        priorityScore = r.priorityScore,
                        status = r.status,
                        justification = r.justification,
                        createdAt = r.createdAt
                    )
                })
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveRedistribution(
        recommendationId: String,
        approved: Boolean,
        remarks: String? = null
    ): Result<RedistributionItem> {
        return try {
            val response = api.approveRedistribution(
                RedistributionApprovalRequest(recommendationId, approved, remarks)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val r = response.body()!!.data!!
                Result.success(
                    RedistributionItem(
                        recommendationId = r.recommendationId,
                        sourceFacilityId = r.sourceFacilityId,
                        sourceFacilityName = r.sourceFacilityName,
                        targetFacilityId = r.targetFacilityId,
                        targetFacilityName = r.targetFacilityName,
                        medicineId = r.medicineId,
                        medicineName = r.medicineName,
                        transferQuantity = r.transferQuantity,
                        sourceSurplus = r.sourceSurplus,
                        targetDeficit = r.targetDeficit,
                        priorityScore = r.priorityScore,
                        status = r.status,
                        justification = r.justification,
                        createdAt = r.createdAt
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
