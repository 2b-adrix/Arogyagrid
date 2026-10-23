package com.example.arogyagrid.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.domain.usecase.DashboardSummary
import com.example.arogyagrid.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    getDashboardDataUseCase: GetDashboardDataUseCase,
    districtFilter: String? = "Jaipur"
) : ViewModel() {

    val summaryState: StateFlow<DashboardSummary?> = getDashboardDataUseCase(districtFilter)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            getDashboardDataUseCase.refreshDashboardData()
        }
    }
}
