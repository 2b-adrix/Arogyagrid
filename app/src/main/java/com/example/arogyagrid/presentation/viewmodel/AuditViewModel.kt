package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.data.repository.AuditRepositoryImpl
import com.example.arogyagrid.domain.model.AuditLogItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuditUiState(
    val isLoading: Boolean = false,
    val items: List<AuditLogItem> = emptyList(),
    val error: String? = null
)

class AuditViewModel : ViewModel() {

    private val repo = AuditRepositoryImpl()

    private val _uiState = MutableStateFlow(AuditUiState())
    val uiState: StateFlow<AuditUiState> = _uiState

    init {
        loadAuditLogs()
    }

    fun loadAuditLogs() {
        viewModelScope.launch {
            _uiState.value = AuditUiState(isLoading = true)
            val result = repo.getAuditLogs()
            result.onSuccess { items ->
                _uiState.value = AuditUiState(items = items)
            }.onFailure { e ->
                _uiState.value = AuditUiState(error = e.message)
            }
        }
    }
}
