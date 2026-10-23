package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyagrid.data.local.entities.RiskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskDao {
    @Query("SELECT * FROM risks")
    fun getAllRisks(): Flow<List<RiskEntity>>

    @Query("SELECT * FROM risks WHERE facilityId = :facilityId")
    fun getRisksByFacility(facilityId: String): Flow<List<RiskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRisks(risks: List<RiskEntity>)

    @Query("DELETE FROM risks")
    fun clearAllRisks()
}
