package com.example.arogyagrid.data.repository

import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.remote.dto.LoginRequest
import com.example.arogyagrid.data.remote.dto.RegisterRequest
import com.example.arogyagrid.domain.model.AuthResult

class AuthRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                UserSession.saveLogin(
                    token = data.token,
                    userId = data.id,
                    name = data.name,
                    email = data.email,
                    role = data.role,
                    district = data.district,
                    facilityId = data.facilityId
                )
                Result.success(
                    AuthResult(
                        userId = data.id,
                        name = data.name,
                        email = data.email,
                        role = data.role,
                        token = data.token
                    )
                )
            } else {
                val msg = response.body()?.message ?: "Login failed"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String, email: String, password: String,
        role: String, district: String?, facilityId: String?
    ): Result<AuthResult> {
        return try {
            val response = api.register(
                RegisterRequest(name, email, password, role, district, facilityId)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                UserSession.saveLogin(
                    token = data.token,
                    userId = data.id,
                    name = data.name,
                    email = data.email,
                    role = data.role,
                    district = data.district,
                    facilityId = data.facilityId
                )
                Result.success(
                    AuthResult(
                        userId = data.id,
                        name = data.name,
                        email = data.email,
                        role = data.role,
                        token = data.token
                    )
                )
            } else {
                val msg = response.body()?.message ?: "Registration failed"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
