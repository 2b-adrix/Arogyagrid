package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyagrid.data.local.entities.RedistributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RedistributionDao {
    @Query("SELECT * FROM redistribution_recommendations ORDER BY createdAt DESC")
    fun getAllRecommendations(): Flow<List<RedistributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecommendations(recommendations: List<RedistributionEntity>)

    @Query("UPDATE redistribution_recommendations SET status = :status WHERE id = :id")
    fun updateStatus(id: String, status: String)

    @Query("DELETE FROM redistribution_recommendations")
    fun clearAllRecommendations()
}
