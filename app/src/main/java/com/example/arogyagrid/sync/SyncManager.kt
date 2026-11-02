package com.example.arogyagrid.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.local.entities.PendingSyncEntity
import com.example.arogyagrid.data.remote.dto.InventoryTransactionRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Orchestrator for the Hybrid Sync Engine.
 * Implements WorkManager background scheduling and offline outbox queuing.
 */
object SyncManager {
    private const val TAG = "ArogyaGridSyncManager"
    private const val PERIODIC_WORK_NAME = "ArogyaGridPeriodicSync"
    private const val IMMEDIATE_WORK_NAME = "ArogyaGridImmediateSync"
    private val gson = Gson()

    private val connectedConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodicSync(context: Context) {
        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(connectedConstraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
        Log.i(TAG, "Scheduled 15-minute periodic background sync worker.")
    }

    fun enqueueImmediateSync(context: Context) {
        val immediateRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(connectedConstraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
        Log.i(TAG, "Triggered immediate sync via WorkManager.")
    }

    fun queueOfflineTransaction(
        context: Context,
        request: InventoryTransactionRequest,
        onQueued: () -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val json = gson.toJson(request)
            val entity = PendingSyncEntity(
                syncType = "TRANSACTION",
                payloadJson = json,
                status = "PENDING"
            )
            val id = db.pendingSyncDao().insert(entity)
            Log.i(TAG, "Queued offline transaction to outbox with ID: $id")
            enqueueImmediateSync(context)
            onQueued()
        }
    }

    fun getPendingCountFlow(context: Context): Flow<Int> {
        return AppDatabase.getDatabase(context).pendingSyncDao().getPendingCountFlow()
    }
}
