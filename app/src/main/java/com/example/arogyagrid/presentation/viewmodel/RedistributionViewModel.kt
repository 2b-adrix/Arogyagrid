package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.data.repository.RedistributionRepositoryImpl
import com.example.arogyagrid.domain.model.RedistributionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RedistributionUiState(
    val isLoading: Boolean = false,
    val items: List<RedistributionItem> = emptyList(),
    val error: String? = null
)

class RedistributionViewModel : ViewModel() {

    private val repo = RedistributionRepositoryImpl()

    private val _uiState = MutableStateFlow(RedistributionUiState())
    val uiState: StateFlow<RedistributionUiState> = _uiState

    init {
        loadRedistributions()
    }

    fun loadRedistributions() {
        viewModelScope.launch {
            _uiState.value = RedistributionUiState(isLoading = true)
            val result = repo.getRedistributions()
            result.onSuccess { items ->
                _uiState.value = RedistributionUiState(items = items)
            }.onFailure { e ->
                _uiState.value = RedistributionUiState(error = e.message)
            }
        }
    }

    fun approve(recommendationId: String, approved: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repo.approveRedistribution(recommendationId, approved)
            result.onSuccess {
                loadRedistributions() // Refresh list
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message
                )
            }
        }
    }
}
