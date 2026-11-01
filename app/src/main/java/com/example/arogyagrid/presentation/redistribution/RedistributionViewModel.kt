package com.example.arogyagrid.presentation.redistribution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.data.repository.RedistributionRepositoryImpl
import com.example.arogyagrid.domain.model.RedistributionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RedistributionViewModel : ViewModel() {

    private val repo = RedistributionRepositoryImpl()

    private val _recommendations = MutableStateFlow<List<RedistributionItem>>(emptyList())
    val recommendations: StateFlow<List<RedistributionItem>> = _recommendations.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        loadRedistributions()
    }

    fun loadRedistributions() {
        viewModelScope.launch {
            repo.getRedistributions().onSuccess { items ->
                _recommendations.value = items
            }.onFailure { e ->
                _userMessage.value = e.message
            }
        }
    }

    fun approve(id: String) {
        viewModelScope.launch {
            repo.approveRedistribution(id, approved = true).onSuccess {
                _userMessage.value = "Transfer approved successfully"
                loadRedistributions()
            }.onFailure { e ->
                _userMessage.value = e.message ?: "Approval failed"
            }
        }
    }

    fun reject(id: String) {
        viewModelScope.launch {
            repo.approveRedistribution(id, approved = false).onSuccess {
                _userMessage.value = "Transfer rejected"
                loadRedistributions()
            }.onFailure { e ->
                _userMessage.value = e.message ?: "Rejection failed"
            }
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
