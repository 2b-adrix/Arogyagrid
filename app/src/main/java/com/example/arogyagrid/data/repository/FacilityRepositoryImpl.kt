package com.example.arogyagrid.data.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.remote.ApiService
import com.example.arogyagrid.data.remote.RegisterFacilityRequest
import com.example.arogyagrid.domain.models.Facility
import com.example.arogyagrid.domain.repository.FacilityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FacilityRepositoryImpl(
    private val db: AppDatabase,
    private val apiService: ApiService
) : FacilityRepository {

    override fun getFacilities(): Flow<List<Facility>> {
        return db.facilityDao().getAllFacilities().map { entities ->
            entities.map {
                Facility(
                    id = it.id,
                    name = it.name,
                    district = it.district,
                    facilityType = it.facilityType,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    totalBeds = it.totalBeds,
                    occupiedBeds = it.occupiedBeds
                )
            }
        }
    }

    override fun getFacilitiesByDistrict(district: String): Flow<List<Facility>> {
        return db.facilityDao().getFacilitiesByDistrict(district).map { entities ->
            entities.map {
                Facility(
                    id = it.id,
                    name = it.name,
                    district = it.district,
                    facilityType = it.facilityType,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    totalBeds = it.totalBeds,
                    occupiedBeds = it.occupiedBeds
                )
            }
        }
    }

    override suspend fun refreshFacilities() = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFacilities()
            if (response.success && response.data != null) {
                // Clear local dummy data to ensure it's fully dynamic
                db.facilityDao().clearAllFacilities()
                db.facilityDao().insertFacilities(response.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun registerFacility(
        name: String,
        district: String,
        facilityType: String,
        latitude: Double,
        longitude: Double,
        totalBeds: Int,
        occupiedBeds: Int
    ): ApiResponse<Facility> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.registerFacility(
                RegisterFacilityRequest(
                    name = name,
                    district = district,
                    facilityType = facilityType,
                    latitude = latitude,
                    longitude = longitude,
                    totalBeds = totalBeds,
                    occupiedBeds = occupiedBeds
                )
            )
            if (response.success && response.data != null) {
                val entity = response.data
                db.facilityDao().insertFacilities(listOf(entity))
                val domainObj = Facility(
                    id = entity.id,
                    name = entity.name,
                    district = entity.district,
                    facilityType = entity.facilityType,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    totalBeds = entity.totalBeds,
                    occupiedBeds = entity.occupiedBeds
                )
                ApiResponse(success = true, data = domainObj, message = response.message)
            } else {
                ApiResponse(success = false, message = response.message ?: "Failed to register facility")
            }
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Network error: ${e.message}")
        }
    }
}
