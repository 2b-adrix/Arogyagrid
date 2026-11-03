package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.data.repository.AiAssistantRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val dataSources: List<String> = emptyList(),
    val actionRoute: String? = null,
    val actionLabel: String? = null
)

data class AiAssistantUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "नमस्कार मुख्य चिकित्सा अधिकारी महोदय (CMHO)! राष्ट्रीय स्वास्थ्य मिशन (NHM) एवं आरएमएससीएल क्लिनिकल व आपूर्ति निर्णय सहायता प्रणाली (CDSS) में आपका स्वागत है।\n\n" +
                    "यह प्रणाली जयपुर जिले के 15 स्वास्थ्य केंद्रों, कोल्ड-चेन आईएलआर सेंसरों व आईडीएसपी सिंड्रोमिक सर्विलांस से लाइव जुड़ी है।\n\n" +
                    "आप निम्न विषयों पर सीधे पूछताछ कर सकते हैं:\n" +
                    "• [1] आपातकालीन आपूर्ति: 'Where can Amer get emergency ORS?'\n" +
                    "• [2] शीत-श्रृंखला मॉनिटरिंग: 'Check cold chain temperature breach'\n" +
                    "• [3] महामारी व प्रकोप प्रक्षेपण: 'Analyze Amer diarrhea outbreak spike'\n" +
                    "• [4] जिला स्वास्थ्य व शय्या स्थिति: 'Summarize Jaipur operations'\n" +
                    "• [5] आवश्यक दवा स्टॉक ऑडिट: 'Check Amoxicillin stock across facilities'",
            isUser = false,
            dataSources = listOf("राष्ट्रीय स्वास्थ्य मिशन (NHM)", "e-Aushadhi RMSCL", "IDSP IHIP पोर्टल")
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
                    dataSources = ai.dataSources,
                    actionRoute = ai.actionRoute,
                    actionLabel = ai.actionLabel
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
