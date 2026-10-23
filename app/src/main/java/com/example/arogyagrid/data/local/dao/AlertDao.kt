package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyagrid.data.local.entities.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE facilityId = :facilityId ORDER BY createdAt DESC")
    fun getAlertsForFacility(facilityId: String): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlerts(alerts: List<AlertEntity>)

    @Query("DELETE FROM alerts")
    fun clearAllAlerts()
}
