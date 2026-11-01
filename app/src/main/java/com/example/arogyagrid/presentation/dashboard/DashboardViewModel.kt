package com.example.arogyagrid.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.DashboardRepositoryImpl
import com.example.arogyagrid.domain.model.DashboardSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repo = DashboardRepositoryImpl()

    private val _summaryState = MutableStateFlow<DashboardSummary?>(null)
    val summaryState: StateFlow<DashboardSummary?> = _summaryState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            val district = UserSession.userDistrict
            val filter = if (district == "ALL") null else district
            repo.getDashboard(filter).onSuccess { data ->
                _summaryState.value = data
            }
        }
    }
}
