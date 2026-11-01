package com.example.arogyagrid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.repository.AuthRepositoryImpl
import com.example.arogyagrid.domain.model.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = UserSession.isLoggedIn,
    val user: AuthResult? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val repo = AuthRepositoryImpl()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repo.login(email, password)
            result.onSuccess { auth ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false, isLoggedIn = true, user = auth
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message ?: "Login failed"
                )
            }
        }
    }

    fun logout() {
        UserSession.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }
}
