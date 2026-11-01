package com.example.arogyagrid.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.InventoryRepositoryImpl
import com.example.arogyagrid.domain.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    private val repo = InventoryRepositoryImpl()

    private val _inventoryList = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryList: StateFlow<List<InventoryItem>> = _inventoryList.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            val facilityId = UserSession.userFacilityId
            val district = UserSession.userDistrict
            repo.getInventory(
                facilityId = if (UserSession.userRole == "PHARMACIST_OPERATOR") facilityId else null,
                district = if (UserSession.userRole == "DISTRICT_OFFICER") district else null
            ).onSuccess { items ->
                _inventoryList.value = items
            }.onFailure { e ->
                _userMessage.value = e.message
            }
        }
    }

    fun recordTransaction(medicineId: String, quantity: Int, type: String, batch: String?, expiry: String?) {
        viewModelScope.launch {
            val facilityId = UserSession.userFacilityId ?: "F-1"
            repo.recordTransaction(facilityId, medicineId, type, quantity, batch).onSuccess {
                _userMessage.value = "Transaction recorded successfully"
                loadInventory()
            }.onFailure { e ->
                _userMessage.value = e.message ?: "Transaction failed"
            }
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
