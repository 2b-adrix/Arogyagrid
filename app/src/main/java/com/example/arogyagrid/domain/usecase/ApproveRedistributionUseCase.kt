package com.example.arogyagrid.domain.usecase

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.repository.RedistributionRepository

class ApproveRedistributionUseCase(
    private val redistributionRepository: RedistributionRepository
) {
    suspend operator fun invoke(recommendationId: String, approve: Boolean): ApiResponse<Boolean> {
        return if (approve) {
            redistributionRepository.approveRecommendation(recommendationId)
        } else {
            redistributionRepository.rejectRecommendation(recommendationId)
        }
    }
}
