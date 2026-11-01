package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.InventoryRepositoryImpl
import com.example.arogyagrid.domain.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class InventoryUiState(
    val isLoading: Boolean = false,
    val items: List<InventoryItem> = emptyList(),
    val transactionSuccess: Boolean = false,
    val error: String? = null
)

class InventoryViewModel : ViewModel() {

    private val repo = InventoryRepositoryImpl()

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState(isLoading = true)
            val facilityId = UserSession.userFacilityId
            val district = UserSession.userDistrict
            val result = repo.getInventory(
                facilityId = if (UserSession.userRole == "PHARMACIST_OPERATOR") facilityId else null,
                district = if (UserSession.userRole == "DISTRICT_OFFICER") district else null
            )
            result.onSuccess { items ->
                _uiState.value = InventoryUiState(items = items)
            }.onFailure { e ->
                _uiState.value = InventoryUiState(error = e.message)
            }
        }
    }

    fun recordTransaction(
        facilityId: String, medicineId: String,
        type: String, quantity: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repo.recordTransaction(facilityId, medicineId, type, quantity)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, transactionSuccess = true
                )
                loadInventory() // Refresh
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message
                )
            }
        }
    }
}
