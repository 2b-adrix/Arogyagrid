package com.example.arogyagrid.data.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.remote.ApiService
import com.example.arogyagrid.domain.models.RedistributionItem
import com.example.arogyagrid.domain.repository.RedistributionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class RedistributionRepositoryImpl(
    private val db: AppDatabase,
    private val apiService: ApiService
) : RedistributionRepository {

    override fun getRecommendations(): Flow<List<RedistributionItem>> {
        return combine(
            db.redistributionDao().getAllRecommendations(),
            db.facilityDao().getAllFacilities(),
            db.medicineDao().getAllMedicines()
        ) { recs, facilities, medicines ->
            val facMap = facilities.associateBy { it.id }
            val medMap = medicines.associateBy { it.id }

            recs.map { rec ->
                RedistributionItem(
                    id = rec.id,
                    medicineId = rec.medicineId,
                    medicineName = medMap[rec.medicineId]?.name ?: rec.medicineId,
                    sourceFacilityId = rec.sourceFacilityId,
                    sourceFacilityName = facMap[rec.sourceFacilityId]?.name ?: rec.sourceFacilityId,
                    destinationFacilityId = rec.destinationFacilityId,
                    destinationFacilityName = facMap[rec.destinationFacilityId]?.name ?: rec.destinationFacilityId,
                    recommendedQuantity = rec.recommendedQuantity,
                    distanceKm = rec.distanceKm,
                    status = rec.status,
                    reason = rec.reason
                )
            }
        }
    }

    override suspend fun approveRecommendation(id: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        try {
            apiService.approveRecommendation(id)
            db.redistributionDao().updateStatus(id, "APPROVED")
            ApiResponse(success = true, data = true, message = "Redistribution approved and inventory transfer recorded")
        } catch (e: Exception) {
            db.redistributionDao().updateStatus(id, "APPROVED")
            ApiResponse(success = true, data = true, message = "Redistribution approved locally")
        }
    }

    override suspend fun rejectRecommendation(id: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        try {
            apiService.rejectRecommendation(id)
            db.redistributionDao().updateStatus(id, "REJECTED")
            ApiResponse(success = true, data = true, message = "Redistribution rejected")
        } catch (e: Exception) {
            db.redistributionDao().updateStatus(id, "REJECTED")
            ApiResponse(success = true, data = true, message = "Redistribution rejected locally")
        }
    }

    override suspend fun refreshRecommendations() = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getRecommendations()
            if (res.success && res.data != null) {
                db.redistributionDao().clearAllRecommendations()
                db.redistributionDao().insertRecommendations(res.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
