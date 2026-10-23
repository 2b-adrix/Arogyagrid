package com.example.arogyagrid.presentation.redistribution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.domain.models.RedistributionItem
import com.example.arogyagrid.domain.usecase.ApproveRedistributionUseCase
import com.example.arogyagrid.domain.repository.RedistributionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RedistributionViewModel(
    redistributionRepository: RedistributionRepository,
    private val approveRedistributionUseCase: ApproveRedistributionUseCase
) : ViewModel() {

    val recommendations: StateFlow<List<RedistributionItem>> = redistributionRepository
        .getRecommendations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            redistributionRepository.refreshRecommendations()
        }
    }

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun approve(id: String) {
        viewModelScope.launch {
            val res = approveRedistributionUseCase(id, approve = true)
            _userMessage.value = res.message ?: "Approved"
        }
    }

    fun reject(id: String) {
        viewModelScope.launch {
            val res = approveRedistributionUseCase(id, approve = false)
            _userMessage.value = res.message ?: "Rejected"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
