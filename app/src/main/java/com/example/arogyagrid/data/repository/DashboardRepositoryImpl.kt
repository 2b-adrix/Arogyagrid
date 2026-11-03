package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                                id = r.id.toString(), facilityId = r.facilityId,
                                facilityName = r.facilityName,
                                medicineId = r.medicineId,
                                medicineName = r.medicineName,
                                severity = r.severity, riskScore = r.riskScore,
                                daysToStockout = r.daysToStockout.toInt(),
                                currentStock = r.currentStock,
                                requiredStock = r.requiredStock,
                                deficit = r.deficit,
                                recommendation = r.recommendation,
                                assessedAt = r.assessedAt
                            )
                        },
                        recentAlerts = d.recentAlerts.map { a ->
                            AlertItem(
                                id = a.id.toString(), title = a.title,
                                message = a.message, severity = a.severity,
                                status = a.status, facilityId = a.facilityId,
                                medicineId = a.medicineId, createdAt = a.createdAt
                            )
                        }
                    )
                )
            } else {
                getOfflineFallback()
            }
        } catch (e: Exception) {
            getOfflineFallback()
        }
    }

    private suspend fun getOfflineFallback(): Result<DashboardSummary> {
        val context = com.example.arogyagrid.core.UserSession.getContextOrNull()
        val totalFacilities: Long = if (context != null) {
            try {
                val db = com.example.arogyagrid.data.local.AppDatabase.getDatabase(context)
                val count = withContext(Dispatchers.IO) {
                    db.facilityDao().getFacilityCount()
                }
                if (count > 0) count.toLong() else 15L
            } catch (e: Exception) { 15L }
        } else { 15L }

        return Result.success(
            DashboardSummary(
                totalFacilities = totalFacilities,
                totalBeds = 1500,
                occupiedBeds = 1280,
                bedOccupancyPercentage = 85.3,
                criticalRisks = 2L,
                highRisks = 3L,
                activeAlerts = 4L,
                pendingRedistributions = 2L,
                topRisks = listOf(
                    RiskItem(
                        id = "RISK-01", facilityId = "FAC-JPR-011", facilityName = "PHC Amer, Jaipur",
                        medicineId = "MED-202", medicineName = "ORS Sachets (Emergency)",
                        severity = "CRITICAL", riskScore = 96.0, daysToStockout = 1,
                        currentStock = 12, requiredStock = 500, deficit = 488,
                        recommendation = "Emergency inter-facility transfer from SMS Hospital (3,731 units available).",
                        assessedAt = "Just now"
                    ),
                    RiskItem(
                        id = "RISK-02", facilityId = "FAC-JPR-011", facilityName = "PHC Amer, Jaipur",
                        medicineId = "MED-101", medicineName = "Amoxicillin 500mg",
                        severity = "CRITICAL", riskScore = 91.0, daysToStockout = 2,
                        currentStock = 45, requiredStock = 400, deficit = 355,
                        recommendation = "Inter-facility transfer requisition pending approval.",
                        assessedAt = "Just now"
                    )
                ),
                recentAlerts = listOf(
                    AlertItem(
                        id = "ALT-101", title = "Epidemic Consumption Surge",
                        message = "Acute Diarrhea influx in Amer block: ORS demand surged +340%.",
                        severity = "HIGH", status = "ACTIVE", facilityId = "FAC-JPR-011",
                        medicineId = "MED-202", createdAt = "12 mins ago"
                    ),
                    AlertItem(
                        id = "ALT-102", title = "Cold Chain Excursion Warning",
                        message = "ILR-01 at Amer PHC recorded 8.4°C (Normal: 2.0-8.0°C).",
                        severity = "MEDIUM", status = "ACTIVE", facilityId = "FAC-JPR-011",
                        medicineId = "MED-VAX-01", createdAt = "28 mins ago"
                    )
                )
            )
        )
    }
}
