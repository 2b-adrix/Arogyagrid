package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.remote.dto.AiChatRequest
import com.example.arogyagrid.domain.model.AiChatResult

class AiAssistantRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun chat(prompt: String): Result<AiChatResult> {
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
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
