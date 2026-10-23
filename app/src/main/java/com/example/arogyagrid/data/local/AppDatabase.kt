package com.example.arogyagrid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.arogyagrid.data.local.dao.AlertDao
import com.example.arogyagrid.data.local.dao.FacilityDao
import com.example.arogyagrid.data.local.dao.InventoryDao
import com.example.arogyagrid.data.local.dao.MedicineDao
import com.example.arogyagrid.data.local.dao.RedistributionDao
import com.example.arogyagrid.data.local.dao.RiskDao
import com.example.arogyagrid.data.local.entities.AlertEntity
import com.example.arogyagrid.data.local.entities.FacilityEntity
import com.example.arogyagrid.data.local.entities.InventoryBalanceEntity
import com.example.arogyagrid.data.local.entities.MedicineEntity
import com.example.arogyagrid.data.local.entities.RedistributionEntity
import com.example.arogyagrid.data.local.entities.RiskEntity

@Database(
    entities = [
        FacilityEntity::class,
        MedicineEntity::class,
        InventoryBalanceEntity::class,
        RiskEntity::class,
        AlertEntity::class,
        RedistributionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun facilityDao(): FacilityDao
    abstract fun medicineDao(): MedicineDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun riskDao(): RiskDao
    abstract fun alertDao(): AlertDao
    abstract fun redistributionDao(): RedistributionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arogyagrid_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
