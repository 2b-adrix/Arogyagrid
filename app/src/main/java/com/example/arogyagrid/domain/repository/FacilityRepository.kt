package com.example.arogyagrid.domain.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.models.Facility
import kotlinx.coroutines.flow.Flow

interface FacilityRepository {
    fun getFacilities(): Flow<List<Facility>>
    fun getFacilitiesByDistrict(district: String): Flow<List<Facility>>
    suspend fun refreshFacilities()
    suspend fun registerFacility(
        name: String,
        district: String,
        facilityType: String,
        latitude: Double,
        longitude: Double,
        totalBeds: Int,
        occupiedBeds: Int
    ): ApiResponse<Facility>
}
