package com.example.arogyagrid.domain.repository

import com.example.arogyagrid.core.ApiResponse

interface AiRepository {
    suspend fun askGemini(prompt: String, district: String? = null, facilityId: String? = null): ApiResponse<String>
}
