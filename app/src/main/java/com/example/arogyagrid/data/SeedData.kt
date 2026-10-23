package com.example.arogyagrid.data

import com.example.arogyagrid.data.local.AppDatabase
import com.example.arogyagrid.data.local.entities.AlertEntity
import com.example.arogyagrid.data.local.entities.FacilityEntity
import com.example.arogyagrid.data.local.entities.InventoryBalanceEntity
import com.example.arogyagrid.data.local.entities.MedicineEntity
import com.example.arogyagrid.data.local.entities.RedistributionEntity
import com.example.arogyagrid.data.local.entities.RiskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedData {
    val districts = listOf("Jaipur", "Udaipur", "Jodhpur", "Kota")

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        val facilityDao = db.facilityDao()
        val medicineDao = db.medicineDao()
        val inventoryDao = db.inventoryDao()
        val riskDao = db.riskDao()
        val alertDao = db.alertDao()
        val redistributionDao = db.redistributionDao()

        // Seed Facilities (24 Facilities: 8 District Hospitals + 16 PHCs)
        val facilities = mutableListOf<FacilityEntity>()
        var fId = 1
        districts.forEach { district ->
            // 2 District Hospitals
            facilities.add(
                FacilityEntity(
                    id = "F-$fId",
                    name = "$district District Hospital 1",
                    district = district,
                    facilityType = "District Hospital",
                    latitude = 26.9124 + (fId * 0.01),
                    longitude = 75.7873 + (fId * 0.01),
                    totalBeds = 250,
                    occupiedBeds = 210,
                    updatedAt = System.currentTimeMillis()
                )
            )
            fId++
            facilities.add(
                FacilityEntity(
                    id = "F-$fId",
                    name = "$district District Hospital 2",
                    district = district,
                    facilityType = "District Hospital",
                    latitude = 26.9124 + (fId * 0.01),
                    longitude = 75.7873 + (fId * 0.01),
                    totalBeds = 200,
                    occupiedBeds = 160,
                    updatedAt = System.currentTimeMillis()
                )
            )
            fId++

            // 4 PHCs
            for (p in 1..4) {
                facilities.add(
                    FacilityEntity(
                        id = "F-$fId",
                        name = "$district PHC $p",
                        district = district,
                        facilityType = "PHC",
                        latitude = 26.9124 + (fId * 0.015),
                        longitude = 75.7873 + (fId * 0.015),
                        totalBeds = 30,
                        occupiedBeds = if (p == 1 && district == "Jaipur") 28 else 18,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                fId++
            }
        }
        facilityDao.insertFacilities(facilities)

        // Seed Medicines (10 Essential Medicines)
        val medicines = listOf(
            MedicineEntity("M-101", "Amoxicillin 500mg", "Antibiotic", "capsule", "CRITICAL", 200, 7),
            MedicineEntity("M-102", "Paracetamol 500mg", "Analgesic", "tablet", "HIGH", 500, 3),
            MedicineEntity("M-103", "ORS Packets", "Essential", "sachet", "HIGH", 300, 5),
            MedicineEntity("M-104", "Azithromycin 250mg", "Antibiotic", "tablet", "CRITICAL", 150, 7),
            MedicineEntity("M-105", "Metformin 500mg", "Antidiabetic", "tablet", "MEDIUM", 400, 10),
            MedicineEntity("M-106", "Amlodipine 5mg", "Antihypertensive", "tablet", "MEDIUM", 350, 10),
            MedicineEntity("M-107", "Cetirizine 10mg", "Antihistamine", "tablet", "LOW", 250, 5),
            MedicineEntity("M-108", "Ciprofloxacin 500mg", "Antibiotic", "tablet", "HIGH", 200, 7),
            MedicineEntity("M-109", "Oral Rehydration Salt", "Essential", "sachet", "MEDIUM", 300, 4),
            MedicineEntity("M-110", "Iron Folic Acid", "Supplement", "tablet", "LOW", 600, 14)
        )
        medicineDao.insertMedicines(medicines)

        // Seed Inventory & Demo Scenario
        // Demo Scenario: Jaipur PHC 1 (F-3) + Amoxicillin (M-101): Stock 300, Daily 90, Shortage 330
        // Source Jaipur DH 1 (F-1) + Amoxicillin (M-101): Stock 900, Surplus 400
        val balances = mutableListOf<InventoryBalanceEntity>()
        facilities.forEach { fac ->
            medicines.forEach { med ->
                val stock = when {
                    fac.id == "F-3" && med.id == "M-101" -> 300 // Jaipur PHC 1 Amoxicillin
                    fac.id == "F-1" && med.id == "M-101" -> 900 // Jaipur DH 1 Amoxicillin
                    fac.facilityType == "District Hospital" -> 800 + (fac.id.takeLast(1).toIntOrNull() ?: 1) * 50
                    else -> 250 + (med.id.takeLast(1).toIntOrNull() ?: 1) * 30
                }
                val dailyCons = if (fac.id == "F-3" && med.id == "M-101") 90.0 else 25.0
                val covDays = if (dailyCons > 0) stock / dailyCons else 30.0
                val gap = if (covDays < med.leadTimeDays) (med.leadTimeDays * dailyCons - stock).toInt().coerceAtLeast(0) else 0

                balances.add(
                    InventoryBalanceEntity(
                        facilityId = fac.id,
                        medicineId = med.id,
                        closingStock = stock,
                        averageDailyConsumption = dailyCons,
                        coverageDays = covDays,
                        stockGap = gap,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
        inventoryDao.insertOrUpdateBalances(balances)

        // Seed Risks
        val risks = mutableListOf<RiskEntity>()
        facilities.forEach { fac ->
            medicines.forEach { med ->
                val isDemoShortage = (fac.id == "F-3" && med.id == "M-101")
                val riskLevel = when {
                    isDemoShortage -> "CRITICAL"
                    fac.id.endsWith("3") && med.criticalityLevel == "CRITICAL" -> "HIGH"
                    med.criticalityLevel == "HIGH" -> "MEDIUM"
                    else -> "LOW"
                }
                val daysToStockout = if (isDemoShortage) 3 else 12
                val reason = if (isDemoShortage) "High daily consumption (90/day) vs current stock (300). Stockout predicted in 3.3 days."
                             else "Normal consumption pattern"

                risks.add(
                    RiskEntity(
                        facilityId = fac.id,
                        medicineId = med.id,
                        riskLevel = riskLevel,
                        riskScore = if (isDemoShortage) 9.4 else 3.2,
                        daysToStockout = daysToStockout,
                        reason = reason,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
        riskDao.insertRisks(risks)

        // Seed Alerts
        val alerts = listOf(
            AlertEntity(
                id = "ALT-1001",
                facilityId = "F-3",
                type = "STOCKOUT_RISK",
                severity = "CRITICAL",
                title = "Critical Shortage: Amoxicillin 500mg",
                message = "Jaipur PHC 1 stock coverage is 3.3 days, below lead time of 7 days. Projected gap: 330 units.",
                isResolved = false,
                createdAt = System.currentTimeMillis() - 3600000
            ),
            AlertEntity(
                id = "ALT-1002",
                facilityId = "F-3",
                type = "BED_PRESSURE",
                severity = "HIGH",
                title = "High Bed Occupancy: 93%",
                message = "Jaipur PHC 1 has 28 of 30 beds occupied.",
                isResolved = false,
                createdAt = System.currentTimeMillis() - 7200000
            ),
            AlertEntity(
                id = "ALT-1003",
                facilityId = "F-5",
                type = "EXPIRED_STOCK",
                severity = "MEDIUM",
                title = "Upcoming Expiry: Paracetamol",
                message = "Udaipur DH 1 has 500 units of Paracetamol expiring in 30 days.",
                isResolved = false,
                createdAt = System.currentTimeMillis() - 86400000
            )
        )
        alertDao.insertAlerts(alerts)

        // Seed Redistribution Recommendation (Demo Scenario)
        val recs = listOf(
            RedistributionEntity(
                id = "REC-2026-001",
                medicineId = "M-101",
                sourceFacilityId = "F-1", // Jaipur DH 1
                destinationFacilityId = "F-3", // Jaipur PHC 1
                recommendedQuantity = 330,
                distanceKm = 18.4,
                status = "PENDING_APPROVAL",
                reason = "Jaipur PHC 1 projected shortage 330 units. Source Jaipur DH 1 has eligible surplus 400 units within 18 km.",
                createdAt = System.currentTimeMillis()
            ),
            RedistributionEntity(
                id = "REC-2026-002",
                medicineId = "M-104",
                sourceFacilityId = "F-2", // Jaipur DH 2
                destinationFacilityId = "F-4", // Jaipur PHC 2
                recommendedQuantity = 150,
                distanceKm = 24.2,
                status = "PENDING_APPROVAL",
                reason = "Jaipur PHC 2 facing seasonal antibiotic demand spike. Transfer from DH 2 to maintain safe buffer.",
                createdAt = System.currentTimeMillis() - 3600000
            )
        )
        redistributionDao.insertRecommendations(recs)
    }
}
