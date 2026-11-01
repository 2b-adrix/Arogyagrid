package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.ForecastRepositoryImpl
import com.example.arogyagrid.domain.model.ForecastItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ForecastUiState(
    val isLoading: Boolean = false,
    val items: List<ForecastItem> = emptyList(),
    val error: String? = null
)

class ForecastViewModel : ViewModel() {

    private val repo = ForecastRepositoryImpl()

    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState

    init {
        loadForecast()
    }

    fun loadForecast() {
        viewModelScope.launch {
            _uiState.value = ForecastUiState(isLoading = true)
            val facilityId = UserSession.userFacilityId
            val result = repo.getForecast(
                facilityId = if (UserSession.userRole == "PHARMACIST_OPERATOR") facilityId else null
            )
            result.onSuccess { items ->
                _uiState.value = ForecastUiState(items = items)
            }.onFailure { e ->
                _uiState.value = ForecastUiState(error = e.message)
            }
        }
    }
}
