package com.example.arogyagrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyagrid.data.local.entities.InventoryBalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_balances WHERE facilityId = :facilityId")
    fun getInventoryForFacility(facilityId: String): Flow<List<InventoryBalanceEntity>>

    @Query("SELECT * FROM inventory_balances WHERE facilityId = :facilityId AND medicineId = :medicineId")
    fun getInventoryItem(facilityId: String, medicineId: String): InventoryBalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateBalances(balances: List<InventoryBalanceEntity>)
    
    @Query("DELETE FROM inventory_balances WHERE facilityId = :facilityId")
    fun clearInventoryForFacility(facilityId: String)
}
