package com.example.arogyagrid.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.domain.usecase.AskGeminiAssistantUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val sender: String, val text: String, val isGrounded: Boolean = false)

class AiAssistantViewModel(
    private val askGeminiAssistantUseCase: AskGeminiAssistantUseCase
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI", "Hello! I am your SmartHealth AI Assistant. Ask me about stockout risks, demand forecasts, or redistribution recommendations.", isGrounded = true)
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun askQuestion(prompt: String, district: String? = "Jaipur") {
        val userMsg = ChatMessage("USER", prompt)
        _chatHistory.value = _chatHistory.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            val response = askGeminiAssistantUseCase(prompt, district)
            val aiText = if (response.success && response.data != null) response.data else "Unable to retrieve response."
            _chatHistory.value = _chatHistory.value + ChatMessage("AI", aiText, isGrounded = true)
            _isLoading.value = false
        }
    }
}
