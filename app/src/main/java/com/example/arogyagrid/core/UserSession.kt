package com.example.arogyagrid.core

data class UserSession(
    val userId: String,
    val username: String,
    val fullName: String,
    val role: UserRole,
    val district: String? = null,
    val facilityId: String? = null,
    val token: String? = null
)
