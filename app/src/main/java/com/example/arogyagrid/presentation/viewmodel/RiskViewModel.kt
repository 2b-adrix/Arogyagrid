package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.RiskRepositoryImpl
import com.example.arogyagrid.domain.model.RiskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RiskUiState(
    val isLoading: Boolean = false,
    val items: List<RiskItem> = emptyList(),
    val error: String? = null
)

class RiskViewModel : ViewModel() {

    private val repo = RiskRepositoryImpl()

    private val _uiState = MutableStateFlow(RiskUiState())
    val uiState: StateFlow<RiskUiState> = _uiState

    init {
        loadRisks()
    }

    fun loadRisks() {
        viewModelScope.launch {
            _uiState.value = RiskUiState(isLoading = true)
            val district = UserSession.userDistrict
            val facilityId = UserSession.userFacilityId
            val result = repo.getRisks(
                facilityId = if (UserSession.userRole == "PHARMACIST_OPERATOR") facilityId else null,
                district = if (UserSession.userRole == "DISTRICT_OFFICER") district else null
            )
            result.onSuccess { items ->
                _uiState.value = RiskUiState(items = items)
            }.onFailure { e ->
                _uiState.value = RiskUiState(error = e.message)
            }
        }
    }
}
