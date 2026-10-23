package com.example.arogyagrid.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.domain.models.InventoryItem
import com.example.arogyagrid.domain.repository.InventoryRepository
import com.example.arogyagrid.domain.usecase.RecordInventoryTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository,
    private val recordInventoryTransactionUseCase: RecordInventoryTransactionUseCase,
    private val facilityId: String = "F-3" // Default Jaipur PHC 1
) : ViewModel() {

    val inventoryList: StateFlow<List<InventoryItem>> = inventoryRepository
        .getInventoryForFacility(facilityId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            inventoryRepository.refreshInventory(facilityId)
        }
    }

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun recordTransaction(medicineId: String, quantity: Int, type: String, batch: String?, expiry: String?) {
        viewModelScope.launch {
            val result = recordInventoryTransactionUseCase(
                facilityId = facilityId,
                medicineId = medicineId,
                quantity = quantity,
                type = type,
                batchNumber = batch,
                expiryDate = expiry
            )
            _userMessage.value = result.message ?: if (result.success) "Transaction completed" else "Transaction failed"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
