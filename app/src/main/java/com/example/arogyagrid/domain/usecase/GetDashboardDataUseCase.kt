package com.example.arogyagrid.domain.usecase

import com.example.arogyagrid.domain.models.AlertItem
import com.example.arogyagrid.domain.models.Facility
import com.example.arogyagrid.domain.models.RedistributionItem
import com.example.arogyagrid.domain.models.RiskItem
import com.example.arogyagrid.domain.repository.FacilityRepository
import com.example.arogyagrid.domain.repository.RedistributionRepository
import com.example.arogyagrid.domain.repository.RiskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DashboardSummary(
    val totalFacilities: Int,
    val criticalRisksCount: Int,
    val stockAlertsCount: Int,
    val pendingRedistributionsCount: Int,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val bedOccupancyPercentage: Int,
    val facilities: List<Facility>,
    val topRisks: List<RiskItem>,
    val recentAlerts: List<AlertItem>,
    val pendingRecommendations: List<RedistributionItem>
)

class GetDashboardDataUseCase(
    private val facilityRepository: FacilityRepository,
    private val riskRepository: RiskRepository,
    private val redistributionRepository: RedistributionRepository
) {
    operator fun invoke(districtFilter: String? = null): Flow<DashboardSummary> {
        val facilitiesFlow = if (districtFilter != null) {
            facilityRepository.getFacilitiesByDistrict(districtFilter)
        } else {
            facilityRepository.getFacilities()
        }

        return combine(
            facilitiesFlow,
            riskRepository.getAllRisks(),
            riskRepository.getAlerts(),
            redistributionRepository.getRecommendations()
        ) { facilities, risks, alerts, recommendations ->
            val totalBeds = facilities.sumOf { it.totalBeds }
            val occupiedBeds = facilities.sumOf { it.occupiedBeds }
            val occupancyPct = if (totalBeds > 0) (occupiedBeds * 100 / totalBeds) else 0

            val filteredRisks = if (districtFilter != null) {
                val facilityIds = facilities.map { it.id }.toSet()
                risks.filter { it.facilityId in facilityIds }
            } else risks

            val criticalCount = filteredRisks.count { it.riskLevel == "CRITICAL" || it.riskLevel == "HIGH" }
            val alertsCount = alerts.count { !it.isResolved }
            val pendingCount = recommendations.count { it.status == "PENDING_APPROVAL" }

            DashboardSummary(
                totalFacilities = facilities.size,
                criticalRisksCount = criticalCount,
                stockAlertsCount = alertsCount,
                pendingRedistributionsCount = pendingCount,
                totalBeds = totalBeds,
                occupiedBeds = occupiedBeds,
                bedOccupancyPercentage = occupancyPct,
                facilities = facilities,
                topRisks = filteredRisks.sortedByDescending { it.riskScore },
                recentAlerts = alerts.take(5),
                pendingRecommendations = recommendations.filter { it.status == "PENDING_APPROVAL" }
            )
        }
    }

    suspend fun refreshDashboardData() {
        facilityRepository.refreshFacilities()
        riskRepository.refreshRisksAndAlerts()
    }
}
