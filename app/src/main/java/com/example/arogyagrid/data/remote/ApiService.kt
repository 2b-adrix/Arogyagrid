package com.example.arogyagrid.data.remote

import com.example.arogyagrid.core.ApiResponse
import com.example.arogyagrid.data.local.entities.AlertEntity
import com.example.arogyagrid.data.local.entities.FacilityEntity
import com.example.arogyagrid.data.local.entities.InventoryBalanceEntity
import com.example.arogyagrid.data.local.entities.MedicineEntity
import com.example.arogyagrid.data.local.entities.RedistributionEntity
import com.example.arogyagrid.data.local.entities.RiskEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(val username: String, val passwordHash: String)
data class LoginResponse(
    val userId: String,
    val username: String,
    val fullName: String,
    val role: String,
    val district: String?,
    val facilityId: String?,
    val token: String
)

data class InventoryTransactionRequest(
    val facilityId: String,
    val medicineId: String,
    val quantity: Int,
    val type: String, // RECEIVE, ISSUE, ADJUSTMENT
    val batchNumber: String?,
    val expiryDate: String?
)

data class RegisterFacilityRequest(
    val name: String,
    val district: String,
    val facilityType: String,
    val latitude: Double,
    val longitude: Double,
    val totalBeds: Int,
    val occupiedBeds: Int
)

data class AiChatRequest(val prompt: String, val contextDistrict: String? = null, val contextFacilityId: String? = null)
data class AiChatResponse(val answer: String, val groundedData: String? = null)

interface ApiService {
    @POST("api/auth/session")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("api/facilities")
    suspend fun getFacilities(): ApiResponse<List<FacilityEntity>>

    @POST("api/facilities/register")
    suspend fun registerFacility(@Body request: RegisterFacilityRequest): ApiResponse<FacilityEntity>

    @GET("api/medicines")
    suspend fun getMedicines(): ApiResponse<List<MedicineEntity>>

    @GET("api/inventory/{facilityId}")
    suspend fun getInventory(@Path("facilityId") facilityId: String): ApiResponse<List<InventoryBalanceEntity>>

    @POST("api/inventory/transaction")
    suspend fun recordTransaction(@Body request: InventoryTransactionRequest): ApiResponse<Boolean>

    @GET("api/risks")
    suspend fun getRisks(): ApiResponse<List<RiskEntity>>

    @GET("api/facilities/alerts")
    suspend fun getAlerts(): ApiResponse<List<AlertEntity>>

    @GET("api/redistribution/recommendations")
    suspend fun getRecommendations(): ApiResponse<List<RedistributionEntity>>

    @POST("api/redistribution/{id}/approve")
    suspend fun approveRecommendation(@Path("id") id: String): ApiResponse<Boolean>

    @POST("api/redistribution/{id}/reject")
    suspend fun rejectRecommendation(@Path("id") id: String): ApiResponse<Boolean>

    @POST("api/ai/chat")
    suspend fun askGemini(@Body request: AiChatRequest): ApiResponse<AiChatResponse>

    @GET("api/supplies")
    suspend fun getSupplies(): List<NetworkSupplyItem>

    @POST("api/supplies/{id}/transaction")
    suspend fun recordSupplyTransaction(
        @Path("id") id: String,
        @Body request: SupplyTransactionRequest
    ): SupplyTransactionResponse
}
