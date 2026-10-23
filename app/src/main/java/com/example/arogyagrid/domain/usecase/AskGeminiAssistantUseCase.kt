package com.example.arogyagrid.domain.usecase

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.domain.repository.AiRepository

class AskGeminiAssistantUseCase(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(prompt: String, district: String? = null, facilityId: String? = null): ApiResponse<String> {
        if (prompt.isBlank()) {
            return ApiResponse(success = false, message = "Prompt cannot be empty")
        }
        return aiRepository.askGemini(prompt, district, facilityId)
    }
}
