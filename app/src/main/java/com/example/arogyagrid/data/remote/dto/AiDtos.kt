package com.example.arogyagrid.data.remote.dto

data class AiChatRequest(
    val prompt: String,
    val context: String? = null
)

data class AiChatResponse(
    val reply: String,
    val dataSources: List<String>,
    val grounded: Boolean
)
