package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.DashboardRepositoryImpl
import com.example.arogyagrid.domain.model.DashboardSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val data: DashboardSummary? = null,
    val error: String? = null
)

class DashboardViewModel : ViewModel() {

    private val repo = DashboardRepositoryImpl()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(isLoading = true)
            val district = UserSession.userDistrict
            val filter = if (district == "ALL") null else district
            val result = repo.getDashboard(filter)
            result.onSuccess { data ->
                _uiState.value = DashboardUiState(data = data)
            }.onFailure { e ->
                _uiState.value = DashboardUiState(error = e.message)
            }
        }
    }
}
