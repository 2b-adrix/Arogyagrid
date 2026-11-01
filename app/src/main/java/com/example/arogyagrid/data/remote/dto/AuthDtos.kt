package com.example.arogyagrid.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val district: String? = null,
    val facilityId: String? = null
)

data class AuthResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val district: String?,
    val facilityId: String?,
    val token: String,
    val tokenType: String
)
