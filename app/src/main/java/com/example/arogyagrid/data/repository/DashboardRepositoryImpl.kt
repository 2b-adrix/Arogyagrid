package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.*

class DashboardRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getDashboard(district: String? = null): Result<DashboardSummary> {
        return try {
            val response = api.getDashboard(district)
            if (response.isSuccessful && response.body()?.success == true) {
                val d = response.body()!!.data!!
                Result.success(
                    DashboardSummary(
                        totalFacilities = d.totalFacilities,
                        totalBeds = d.totalBeds,
                        occupiedBeds = d.occupiedBeds,
                        bedOccupancyPercentage = d.bedOccupancyPercentage,
                        criticalRisks = d.criticalRisks,
                        highRisks = d.highRisks,
                        activeAlerts = d.activeAlerts,
                        pendingRedistributions = d.pendingRedistributions,
                        topRisks = d.topRisks.map { r ->
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
                        },
                        recentAlerts = d.recentAlerts.map { a ->
                            AlertItem(
                                id = a.id, title = a.title,
                                message = a.message, severity = a.severity,
                                status = a.status, facilityId = a.facilityId,
                                medicineId = a.medicineId, createdAt = a.createdAt
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
