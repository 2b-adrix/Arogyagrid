package com.example.arogyagrid.data.repository

import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.domain.model.AuditLogItem

class AuditRepositoryImpl {

    private val api = NetworkClient.apiService

    suspend fun getAuditLogs(
        performedBy: String? = null,
        entityType: String? = null
    ): Result<List<AuditLogItem>> {
        return try {
            val response = api.getAuditLogs(performedBy, entityType)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data!!
                Result.success(list.map { a ->
                    AuditLogItem(
                        id = a.id, action = a.action,
                        entityType = a.entityType, entityId = a.entityId,
                        performedBy = a.performedBy, userRole = a.userRole,
                        details = a.details, timestamp = a.timestamp
                    )
                })
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
