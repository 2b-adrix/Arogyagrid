package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.Facility

class FacilityRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getFacilities(district: String? = null): Result<List<Facility>> {
        return try {
            val response = api.getFacilities(district)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { f ->
                    Facility(
                        facilityId = f.facilityId, name = f.name,
                        type = f.type, district = f.district,
                        subDistrict = f.subDistrict, totalBeds = f.totalBeds,
                        occupiedBeds = f.occupiedBeds,
                        availableBeds = f.availableBeds,
                        bedOccupancyPercentage = f.bedOccupancyPercentage,
                        totalStaff = f.totalStaff, activeStaff = f.activeStaff,
                        staffingRatio = f.staffingRatio,
                        address = f.address, contactPhone = f.contactPhone
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
