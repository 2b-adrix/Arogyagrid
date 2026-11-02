package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.arogyagrid.data.local.entities.PendingSyncEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingSyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PendingSyncEntity): Long

    @Query("SELECT * FROM pending_sync_queue WHERE status = 'PENDING' OR status = 'FAILED' ORDER BY createdAt ASC")
    suspend fun getPendingOperations(): List<PendingSyncEntity>

    @Query("SELECT COUNT(*) FROM pending_sync_queue WHERE status = 'PENDING' OR status = 'FAILED'")
    fun getPendingCountFlow(): Flow<Int>

    @Update
    suspend fun update(entity: PendingSyncEntity)

    @Query("DELETE FROM pending_sync_queue WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pending_sync_queue WHERE status = 'COMPLETED'")
    suspend fun clearCompleted()
}
