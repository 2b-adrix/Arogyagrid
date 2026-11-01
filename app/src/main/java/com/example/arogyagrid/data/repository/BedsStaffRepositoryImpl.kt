package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.BedsStaffSummary
import com.example.arogyagrid.domain.model.Facility

class BedsStaffRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getBedsAndStaff(
        district: String? = null
    ): Result<BedsStaffSummary> {
        return try {
            val response = api.getBedsAndStaff(district)
            if (response.isSuccessful && response.body()?.success == true) {
                val d = response.body()!!.data!!
                Result.success(
                    BedsStaffSummary(
                        totalBeds = d.totalBeds,
                        occupiedBeds = d.occupiedBeds,
                        availableBeds = d.availableBeds,
                        occupancyPercentage = d.occupancyPercentage,
                        totalStaff = d.totalStaff,
                        activeStaff = d.activeStaff,
                        staffingRatio = d.staffingRatio,
                        facilities = d.facilities.map { f ->
                            Facility(
                                facilityId = f.facilityId, name = f.name,
                                type = f.type, district = f.district,
                                subDistrict = f.subDistrict,
                                totalBeds = f.totalBeds,
                                occupiedBeds = f.occupiedBeds,
                                availableBeds = f.availableBeds,
                                bedOccupancyPercentage = f.bedOccupancyPercentage,
                                totalStaff = f.totalStaff,
                                activeStaff = f.activeStaff,
                                staffingRatio = f.staffingRatio,
                                address = f.address,
                                contactPhone = f.contactPhone
                            )
                        }
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
