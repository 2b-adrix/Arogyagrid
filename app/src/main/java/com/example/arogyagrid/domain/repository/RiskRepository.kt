package com.example.arogyagrid.domain.repository

import com.example.arogyagrid.domain.models.AlertItem
import com.example.arogyagrid.domain.models.RiskItem
import kotlinx.coroutines.flow.Flow

interface RiskRepository {
    fun getAllRisks(): Flow<List<RiskItem>>
    fun getRisksByFacility(facilityId: String): Flow<List<RiskItem>>
    fun getAlerts(): Flow<List<AlertItem>>
    suspend fun refreshRisksAndAlerts()
}
