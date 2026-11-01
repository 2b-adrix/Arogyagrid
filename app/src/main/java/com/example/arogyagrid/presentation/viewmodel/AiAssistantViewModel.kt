package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.data.repository.AiAssistantRepositoryImpl
import com.example.arogyagrid.domain.model.AiChatResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val dataSources: List<String> = emptyList()
)

data class AiAssistantUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hello! I'm your ArogyaGrid AI Assistant. Ask me about:\n" +
                    "• Medicine stock status\n" +
                    "• Risk assessments\n" +
                    "• Bed occupancy\n" +
                    "• Facility information",
            isUser = false
        )
    ),
    val error: String? = null
)

class AiAssistantViewModel : ViewModel() {

    private val repo = AiAssistantRepositoryImpl()

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState

    fun sendMessage(prompt: String) {
        val userMsg = ChatMessage(text = prompt, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = repo.chat(prompt)
            result.onSuccess { ai ->
                val botMsg = ChatMessage(
                    text = ai.reply,
                    isUser = false,
                    dataSources = ai.dataSources
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + botMsg,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
