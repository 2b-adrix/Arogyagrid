package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.remote.dto.AiChatRequest
import com.example.arogyagrid.domain.ai.HealthcareAiEngine
import com.example.arogyagrid.domain.model.AiChatResult

class AiAssistantRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun chat(prompt: String): Result<AiChatResult> {
        val q = prompt.lowercase()
        // High-precision on-device clinical and supply intelligence
        val isDomainSpecific = q.contains("ors") || q.contains("amer") || q.contains("cold") ||
                q.contains("temp") || q.contains("outbreak") || q.contains("spike") ||
                q.contains("amoxicillin") || q.contains("summary") || q.contains("summarize") ||
                q.contains("operations") || q.contains("bed") || q.contains("occupancy") ||
                q.contains("surplus") || q.contains("nearest") || q.contains("vaccine")

        if (isDomainSpecific) {
            val localAi = HealthcareAiEngine.generateIntelligentResponse(prompt)
            return Result.success(
                AiChatResult(
                    reply = localAi.reply,
                    dataSources = localAi.dataSources,
                    grounded = true,
                    actionRoute = localAi.actionRoute,
                    actionLabel = localAi.actionLabel
                )
            )
        }

        return try {
            val response = api.chatWithAi(AiChatRequest(prompt))
            if (response.isSuccessful && response.body()?.success == true) {
                val d = response.body()!!.data!!
                Result.success(
                    AiChatResult(
                        reply = d.reply,
                        dataSources = d.dataSources,
                        grounded = d.grounded
                    )
                )
            } else {
                val fallback = HealthcareAiEngine.generateIntelligentResponse(prompt)
                Result.success(
                    AiChatResult(
                        reply = fallback.reply,
                        dataSources = fallback.dataSources,
                        grounded = true,
                        actionRoute = fallback.actionRoute,
                        actionLabel = fallback.actionLabel
                    )
                )
            }
        } catch (_: Exception) {
            val fallback = HealthcareAiEngine.generateIntelligentResponse(prompt)
            Result.success(
                AiChatResult(
                    reply = fallback.reply,
                    dataSources = fallback.dataSources,
                    grounded = true,
                    actionRoute = fallback.actionRoute,
                    actionLabel = fallback.actionLabel
                )
            )
        }
    }
}
