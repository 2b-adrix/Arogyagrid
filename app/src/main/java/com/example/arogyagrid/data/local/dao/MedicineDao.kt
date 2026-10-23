package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyagrid.data.local.entities.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: String): MedicineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicines(medicines: List<MedicineEntity>)
}
