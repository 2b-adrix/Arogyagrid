package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyagrid.data.local.entities.FacilityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Query("SELECT * FROM facilities")
    fun getAllFacilities(): Flow<List<FacilityEntity>>

    @Query("SELECT * FROM facilities WHERE district = :district")
    fun getFacilitiesByDistrict(district: String): Flow<List<FacilityEntity>>

    @Query("SELECT * FROM facilities WHERE id = :id")
    fun getFacilityById(id: String): FacilityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFacilities(facilities: List<FacilityEntity>)

    @Query("DELETE FROM facilities")
    fun clearAllFacilities()
}
