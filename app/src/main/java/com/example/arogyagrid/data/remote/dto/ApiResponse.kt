package com.example.arogyagrid.data.remote.dto

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val timestamp: String?
)
