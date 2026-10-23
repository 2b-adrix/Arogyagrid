package com.example.arogyagrid.domain.repository

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.core.UserRole
import com.example.arogyagrid.core.UserSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentSession: StateFlow<UserSession?>
    suspend fun login(username: String, role: UserRole, district: String? = null, facilityId: String? = null): ApiResponse<UserSession>
    fun logout()
}
