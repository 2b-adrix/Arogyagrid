package com.example.arogyagrid.data.remote

import com.example.arogyagrid.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>

    // ── Dashboard ─────────────────────────────
    @GET("dashboard")
    suspend fun getDashboard(
        @Query("district") district: String? = null
    ): Response<ApiResponse<DashboardSummaryResponse>>

    // ── Facilities ────────────────────────────
    @GET("facilities")
    suspend fun getFacilities(
        @Query("district") district: String? = null
    ): Response<ApiResponse<List<FacilityResponse>>>

    @GET("facilities/{facilityId}")
    suspend fun getFacility(
        @Path("facilityId") facilityId: String
    ): Response<ApiResponse<FacilityResponse>>

    // ── Inventory ─────────────────────────────
    @GET("inventory")
    suspend fun getInventory(
        @Query("facilityId") facilityId: String? = null,
        @Query("district") district: String? = null
    ): Response<ApiResponse<List<InventoryResponse>>>

    @POST("inventory/transaction")
    suspend fun recordTransaction(
        @Body request: InventoryTransactionRequest
    ): Response<ApiResponse<Boolean>>

    // ── Risks ─────────────────────────────────
    @GET("risks")
    suspend fun getRisks(
        @Query("severity") severity: String? = null,
        @Query("facilityId") facilityId: String? = null,
        @Query("district") district: String? = null
    ): Response<ApiResponse<List<RiskResponse>>>

    // ── Alerts ────────────────────────────────
    @GET("alerts")
    suspend fun getAlerts(
        @Query("activeOnly") activeOnly: Boolean = false
    ): Response<ApiResponse<List<AlertResponse>>>

    @PATCH("alerts/{alertId}/resolve")
    suspend fun resolveAlert(
        @Path("alertId") alertId: String
    ): Response<ApiResponse<AlertResponse>>

    // ── Redistribution ────────────────────────
    @GET("redistribution")
    suspend fun getRedistributions(
        @Query("pendingOnly") pendingOnly: Boolean = false
    ): Response<ApiResponse<List<RedistributionResponse>>>

    @POST("redistribution/approve")
    suspend fun approveRedistribution(
        @Body request: RedistributionApprovalRequest
    ): Response<ApiResponse<RedistributionResponse>>

    // ── Forecast ──────────────────────────────
    @GET("forecast")
    suspend fun getForecast(
        @Query("facilityId") facilityId: String? = null
    ): Response<ApiResponse<List<ForecastResponse>>>

    // ── Beds & Staff ──────────────────────────
    @GET("beds-staff")
    suspend fun getBedsAndStaff(
        @Query("district") district: String? = null
    ): Response<ApiResponse<BedsStaffResponse>>

    // ── Audit Logs ────────────────────────────
    @GET("audit-logs")
    suspend fun getAuditLogs(
        @Query("performedBy") performedBy: String? = null,
        @Query("entityType") entityType: String? = null
    ): Response<ApiResponse<List<AuditLogResponse>>>

    // ── AI Assistant ──────────────────────────
    @POST("ai-assistant/chat")
    suspend fun chatWithAi(
        @Body request: AiChatRequest
    ): Response<ApiResponse<AiChatResponse>>
}
