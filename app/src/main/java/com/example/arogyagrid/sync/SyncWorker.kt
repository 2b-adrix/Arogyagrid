package com.example.arogyagrid.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.data.remote.dto.InventoryTransactionRequest
import com.google.gson.Gson

/**
 * WorkManager CoroutineWorker implementing the Hybrid Offline Sync Engine (Section 08).
 * Processes pending offline operations, pushes them to the central Spring Boot API,
 * and handles deterministic retry and reconciliation.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val tag = "ArogyaGridSyncWorker"
    private val db = AppDatabase.getDatabase(appContext)
    private val pendingDao = db.pendingSyncDao()
    private val apiService = NetworkClient.apiService
    private val gson = Gson()

    override suspend fun doWork(): Result {
        Log.i(tag, "Starting Hybrid Sync Worker execution...")
        val pendingOps = pendingDao.getPendingOperations()
        if (pendingOps.isEmpty()) {
            Log.i(tag, "No pending operations in outbox queue.")
            return Result.success()
        }

        Log.i(tag, "Found ${pendingOps.size} pending operations to synchronize.")
        var hasFailures = false

        for (op in pendingOps) {
            try {
                pendingDao.update(op.copy(status = "SYNCING"))

                when (op.syncType) {
                    "TRANSACTION" -> {
                        val request = gson.fromJson(op.payloadJson, InventoryTransactionRequest::class.java)
                        val response = apiService.recordTransaction(request)
                        if (response.isSuccessful && response.body()?.success == true) {
                            Log.i(tag, "Successfully synced transaction for facility: ${request.facilityId}, med: ${request.medicineId}")
                            pendingDao.deleteById(op.id)
                        } else {
                            Log.w(tag, "Backend rejected transaction: ${response.errorBody()?.string()}")
                            pendingDao.update(
                                op.copy(
                                    status = "FAILED",
                                    retryCount = op.retryCount + 1,
                                    errorMessage = "HTTP ${response.code()}: ${response.message()}"
                                )
                            )
                            hasFailures = true
                        }
                    }
                    else -> {
                        Log.w(tag, "Unknown sync type: ${op.syncType}")
                        pendingDao.deleteById(op.id)
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Network/Server error while syncing operation ${op.id}: ${e.message}", e)
                pendingDao.update(
                    op.copy(
                        status = "FAILED",
                        retryCount = op.retryCount + 1,
                        errorMessage = e.localizedMessage
                    )
                )
                hasFailures = true
            }
        }

        return if (hasFailures) {
            Log.w(tag, "Sync completed with some failures. WorkManager will retry.")
            Result.retry()
        } else {
            Log.i(tag, "All pending offline operations synchronized successfully.")
            Result.success()
        }
    }
}
