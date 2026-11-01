package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.BedsStaffRepositoryImpl
import com.example.arogyagrid.domain.model.BedsStaffSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BedsStaffUiState(
    val isLoading: Boolean = false,
    val data: BedsStaffSummary? = null,
    val error: String? = null
)

class BedsStaffViewModel : ViewModel() {

    private val repo = BedsStaffRepositoryImpl()

    private val _uiState = MutableStateFlow(BedsStaffUiState())
    val uiState: StateFlow<BedsStaffUiState> = _uiState

    init {
        loadBedsAndStaff()
    }

    fun loadBedsAndStaff() {
        viewModelScope.launch {
            _uiState.value = BedsStaffUiState(isLoading = true)
            val district = UserSession.userDistrict
            val filter = if (district == "ALL") null else district
            val result = repo.getBedsAndStaff(filter)
            result.onSuccess { data ->
                _uiState.value = BedsStaffUiState(data = data)
            }.onFailure { e ->
                _uiState.value = BedsStaffUiState(error = e.message)
            }
        }
    }
}
