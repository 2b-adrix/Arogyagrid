package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.ForecastItem

class ForecastRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getForecast(
        facilityId: String? = null
    ): Result<List<ForecastItem>> {
        return try {
            val response = api.getForecast(facilityId)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { f ->
                    ForecastItem(
                        medicineId = f.medicineId,
                        medicineName = f.medicineName,
                        facilityId = f.facilityId,
                        facilityName = f.facilityName,
                        currentDailyConsumption = f.currentDailyConsumption,
                        predictedDailyConsumption = f.predictedDailyConsumption,
                        currentStock = f.currentStock,
                        predictedDaysToStockout = f.predictedDaysToStockout,
                        weeklyForecast = f.weeklyForecast,
                        trendDirection = f.trendDirection
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
