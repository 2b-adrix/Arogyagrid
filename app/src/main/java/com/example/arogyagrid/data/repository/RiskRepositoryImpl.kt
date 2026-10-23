package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.remote.ApiService
import com.example.arogyagrid.domain.models.AlertItem
import com.example.arogyagrid.domain.models.RiskItem
import com.example.arogyagrid.domain.repository.RiskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiskRepositoryImpl(
    private val db: AppDatabase,
    private val apiService: ApiService
) : RiskRepository {

    override fun getAllRisks(): Flow<List<RiskItem>> {
        return combine(
            db.riskDao().getAllRisks(),
            db.facilityDao().getAllFacilities(),
            db.medicineDao().getAllMedicines()
        ) { risks, facilities, medicines ->
            val facMap = facilities.associateBy { it.id }
            val medMap = medicines.associateBy { it.id }

            risks.map { risk ->
                RiskItem(
                    facilityId = risk.facilityId,
                    facilityName = facMap[risk.facilityId]?.name ?: risk.facilityId,
                    medicineId = risk.medicineId,
                    medicineName = medMap[risk.medicineId]?.name ?: risk.medicineId,
                    riskLevel = risk.riskLevel,
                    riskScore = risk.riskScore,
                    daysToStockout = risk.daysToStockout,
                    reason = risk.reason
                )
            }
        }
    }

    override fun getRisksByFacility(facilityId: String): Flow<List<RiskItem>> {
        return combine(
            db.riskDao().getRisksByFacility(facilityId),
            db.facilityDao().getAllFacilities(),
            db.medicineDao().getAllMedicines()
        ) { risks, facilities, medicines ->
            val facMap = facilities.associateBy { it.id }
            val medMap = medicines.associateBy { it.id }

            risks.map { risk ->
                RiskItem(
                    facilityId = risk.facilityId,
                    facilityName = facMap[risk.facilityId]?.name ?: risk.facilityId,
                    medicineId = risk.medicineId,
                    medicineName = medMap[risk.medicineId]?.name ?: risk.medicineId,
                    riskLevel = risk.riskLevel,
                    riskScore = risk.riskScore,
                    daysToStockout = risk.daysToStockout,
                    reason = risk.reason
                )
            }
        }
    }

    override fun getAlerts(): Flow<List<AlertItem>> {
        return combine(
            db.alertDao().getAllAlerts(),
            db.facilityDao().getAllFacilities()
        ) { alerts, facilities ->
            val facMap = facilities.associateBy { it.id }
            val sdf = SimpleDateFormat("HH:mm dd MMM", Locale.getDefault())

            alerts.map { alert ->
                AlertItem(
                    id = alert.id,
                    facilityId = alert.facilityId,
                    facilityName = facMap[alert.facilityId]?.name ?: alert.facilityId,
                    type = alert.type,
                    severity = alert.severity,
                    title = alert.title,
                    message = alert.message,
                    isResolved = alert.isResolved,
                    createdAtFormatted = sdf.format(Date(alert.createdAt))
                )
            }
        }
    }

    override suspend fun refreshRisksAndAlerts() = withContext(Dispatchers.IO) {
        try {
            val riskRes = apiService.getRisks()
            if (riskRes.success && riskRes.data != null) {
                db.riskDao().clearAllRisks()
                db.riskDao().insertRisks(riskRes.data)
            }
            val alertRes = apiService.getAlerts()
            if (alertRes.success && alertRes.data != null) {
                db.alertDao().clearAllAlerts()
                db.alertDao().insertAlerts(alertRes.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
