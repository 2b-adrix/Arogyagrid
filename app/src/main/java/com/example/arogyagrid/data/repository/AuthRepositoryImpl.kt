package com.example.arogyagrid.data.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.core.UserRole
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl : AuthRepository {
    private val _currentSession = MutableStateFlow<UserSession?>(
        UserSession(
            userId = "USR-001",
            username = "district_officer_jaipur",
            fullName = "Jaipur District Officer",
            role = UserRole.DISTRICT_OFFICER,
            district = "Jaipur",
            facilityId = null,
            token = "demo_jwt_token_jaipur"
        )
    )
    override val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    override suspend fun login(
        username: String,
        role: UserRole,
        district: String?,
        facilityId: String?
    ): ApiResponse<UserSession> {
        val session = UserSession(
            userId = "USR-${(100..999).random()}",
            username = username,
            fullName = username.replace("_", " ").replaceFirstChar { it.uppercase() },
            role = role,
            district = district,
            facilityId = facilityId,
            token = "jwt_${System.currentTimeMillis()}"
        )
        _currentSession.value = session
        return ApiResponse(success = true, data = session, message = "Logged in successfully")
    }

    override fun logout() {
        _currentSession.value = null
    }
}
