package com.example.arogyagrid.domain.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.models.RedistributionItem
import kotlinx.coroutines.flow.Flow

interface RedistributionRepository {
    fun getRecommendations(): Flow<List<RedistributionItem>>
    suspend fun approveRecommendation(id: String): ApiResponse<Boolean>
    suspend fun rejectRecommendation(id: String): ApiResponse<Boolean>
    suspend fun refreshRecommendations()
}
