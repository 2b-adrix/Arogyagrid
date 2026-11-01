package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.AlertItem

class AlertRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getAlerts(activeOnly: Boolean = false): Result<List<AlertItem>> {
        return try {
            val response = api.getAlerts(activeOnly)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { a ->
                    AlertItem(
                        id = a.id, title = a.title, message = a.message,
                        severity = a.severity, status = a.status,
                        facilityId = a.facilityId, medicineId = a.medicineId,
                        createdAt = a.createdAt
                    )
                })
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveAlert(alertId: String): Result<AlertItem> {
        return try {
            val response = api.resolveAlert(alertId)
            if (response.isSuccessful && response.body()?.success == true) {
                val a = response.body()!!.data!!
                Result.success(
                    AlertItem(
                        id = a.id, title = a.title, message = a.message,
                        severity = a.severity, status = a.status,
                        facilityId = a.facilityId, medicineId = a.medicineId,
                        createdAt = a.createdAt
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
