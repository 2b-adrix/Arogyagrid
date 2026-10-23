package com.example.arogyagrid.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject

class MockApiInterceptor(
    var isMockEnabled: Boolean = true
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val path = request.url.encodedPath
        val method = request.method

        if (!isMockEnabled || path.contains("api/")) {
            // For Node.js endpoints, proceed with the real network request.
            // Do NOT catch the exception here so the Repository can handle it properly.
            return chain.proceed(request)
        }

        // Simulate realistic network latency (300ms - 800ms) to trigger loading states in the UI
        val simulatedDelay = (300L..800L).random()
        try {
            Thread.sleep(if (path.endsWith("ai/chat")) 1200L else simulatedDelay) // AI needs slightly more time
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val jsonResponse = when {
            (path.endsWith("auth/session") && method == "POST") -> getLoginResponse()
            (path.endsWith("facilities") && method == "GET") -> getFacilitiesResponse()
            (path.endsWith("medicines") && method == "GET") -> getMedicinesResponse()
            (path.contains("inventory/") && !path.contains("transaction") && method == "GET") -> getInventoryResponse()
            (path.endsWith("inventory/transaction") && method == "POST") -> getTransactionResponse()
            (path.endsWith("risks") && method == "GET") -> getRisksResponse()
            (path.endsWith("alerts") && method == "GET") -> getAlertsResponse()
            (path.contains("redistribution/recommendations") && method == "GET") -> getRedistributionResponse()
            (path.contains("redistribution/") && path.endsWith("/approve") && method == "POST") -> getApproveResponse()
            (path.contains("redistribution/") && path.endsWith("/reject") && method == "POST") -> getRejectResponse()
            (path.endsWith("ai/chat") && method == "POST") -> getAiChatResponse(request)
            else -> getGenericSuccessResponse()
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(jsonResponse.toByteArray().toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun getLoginResponse(): String {
        return """
            {
                "success": true,
                "message": "Authenticated successfully via Mock Backend",
                "data": {
                    "userId": "USR-001",
                    "username": "district_officer_jaipur",
                    "fullName": "Jaipur District Officer",
                    "role": "DISTRICT_OFFICER",
                    "district": "Jaipur",
                    "facilityId": null,
                    "token": "mock_jwt_token_jaipur_2025"
                }
            }
        """.trimIndent()
    }

    private fun getFacilitiesResponse(): String {
        val now = System.currentTimeMillis()
        return """
            {
                "success": true,
                "message": "Facilities retrieved successfully",
                "data": [
                    {
                        "id": "FAC-001",
                        "name": "Jaipur DH 1",
                        "district": "Jaipur",
                        "facilityType": "District Hospital",
                        "latitude": 26.9124,
                        "longitude": 75.7873,
                        "totalBeds": 250,
                        "occupiedBeds": 210,
                        "updatedAt": $now
                    },
                    {
                        "id": "FAC-002",
                        "name": "Jaipur PHC 1",
                        "district": "Jaipur",
                        "facilityType": "Primary Health Centre",
                        "latitude": 26.9200,
                        "longitude": 75.8000,
                        "totalBeds": 30,
                        "occupiedBeds": 28,
                        "updatedAt": $now
                    },
                    {
                        "id": "FAC-003",
                        "name": "Jaipur PHC 2",
                        "district": "Jaipur",
                        "facilityType": "Primary Health Centre",
                        "latitude": 26.9500,
                        "longitude": 75.8200,
                        "totalBeds": 25,
                        "occupiedBeds": 12,
                        "updatedAt": $now
                    }
                ]
            }
        """.trimIndent()
    }

    private fun getMedicinesResponse(): String {
        return """
            {
                "success": true,
                "message": "Medicines retrieved successfully",
                "data": [
                    {
                        "id": "MED-001",
                        "name": "Amoxicillin 500mg",
                        "category": "Antibiotics",
                        "unit": "tablets",
                        "criticalityLevel": "HIGH",
                        "reorderLevel": 200,
                        "leadTimeDays": 7
                    },
                    {
                        "id": "MED-002",
                        "name": "Paracetamol 500mg",
                        "category": "Analgesic",
                        "unit": "tablets",
                        "criticalityLevel": "MEDIUM",
                        "reorderLevel": 500,
                        "leadTimeDays": 5
                    },
                    {
                        "id": "MED-003",
                        "name": "ORS Sachets",
                        "category": "Essential",
                        "unit": "sachets",
                        "criticalityLevel": "CRITICAL",
                        "reorderLevel": 300,
                        "leadTimeDays": 4
                    }
                ]
            }
        """.trimIndent()
    }

    private fun getInventoryResponse(): String {
        val now = System.currentTimeMillis()
        return """
            {
                "success": true,
                "message": "Inventory retrieved successfully",
                "data": [
                    {
                        "facilityId": "FAC-002",
                        "medicineId": "MED-001",
                        "closingStock": 70,
                        "averageDailyConsumption": 35.0,
                        "coverageDays": 2.0,
                        "stockGap": 330,
                        "updatedAt": $now
                    },
                    {
                        "facilityId": "FAC-001",
                        "medicineId": "MED-001",
                        "closingStock": 850,
                        "averageDailyConsumption": 10.0,
                        "coverageDays": 85.0,
                        "stockGap": 0,
                        "updatedAt": $now
                    }
                ]
            }
        """.trimIndent()
    }

    private fun getTransactionResponse(): String {
        return """
            {
                "success": true,
                "message": "Inventory transaction recorded in mock backend",
                "data": true
            }
        """.trimIndent()
    }

    private fun getRisksResponse(): String {
        val now = System.currentTimeMillis()
        return """
            {
                "success": true,
                "message": "Risks retrieved successfully",
                "data": [
                    {
                        "facilityId": "FAC-002",
                        "medicineId": "MED-001",
                        "riskLevel": "CRITICAL",
                        "riskScore": 92.5,
                        "daysToStockout": 2,
                        "reason": "70 units in stock vs 35/day consumption. High seasonal spike expected.",
                        "updatedAt": $now
                    }
                ]
            }
        """.trimIndent()
    }

    private fun getAlertsResponse(): String {
        val now = System.currentTimeMillis()
        return """
            {
                "success": true,
                "message": "Alerts retrieved successfully",
                "data": [
                    {
                        "id": "ALT-001",
                        "facilityId": "FAC-002",
                        "type": "STOCKOUT_RISK",
                        "severity": "CRITICAL",
                        "title": "Critical Stockout Risk - Amoxicillin",
                        "message": "Jaipur PHC 1 will run out of Amoxicillin 500mg in 2 days.",
                        "isResolved": false,
                        "createdAt": $now
                    }
                ]
            }
        """.trimIndent()
    }

    private fun getRedistributionResponse(): String {
        val now = System.currentTimeMillis()
        return """
            {
                "success": true,
                "message": "Redistribution recommendations retrieved",
                "data": [
                    {
                        "id": "REC-001",
                        "medicineId": "MED-001",
                        "sourceFacilityId": "FAC-001",
                        "destinationFacilityId": "FAC-002",
                        "recommendedQuantity": 330,
                        "distanceKm": 12.5,
                        "status": "PENDING_APPROVAL",
                        "reason": "Transfer 330 units of Amoxicillin from Jaipur DH 1 (85 days coverage) to Jaipur PHC 1 (2 days coverage).",
                        "createdAt": $now
                    }
                ]
            }
        """.trimIndent()
    }

    private fun getApproveResponse(): String {
        return """
            {
                "success": true,
                "message": "Redistribution recommendation approved",
                "data": true
            }
        """.trimIndent()
    }

    private fun getRejectResponse(): String {
        return """
            {
                "success": true,
                "message": "Redistribution recommendation rejected",
                "data": true
            }
        """.trimIndent()
    }

    private fun getAiChatResponse(request: Request): String {
        var promptText = ""
        try {
            val buffer = Buffer()
            request.body?.writeTo(buffer)
            val jsonStr = buffer.readUtf8()
            val jsonObj = JSONObject(jsonStr)
            promptText = jsonObj.optString("prompt", "")
        } catch (_: Exception) {}

        val answerText = when {
            promptText.contains("Amoxicillin", ignoreCase = true) || promptText.contains("risk", ignoreCase = true) -> {
                "Grounded Data Analysis: Amoxicillin 500mg at Jaipur PHC 1 is classified as CRITICAL risk with 70 units remaining against an average daily consumption of 35 units/day (2 days coverage). The local lead time is 7 days, leaving a projected deficit of 330 units."
            }
            promptText.contains("recommendation", ignoreCase = true) || promptText.contains("transfer", ignoreCase = true) || promptText.contains("redistribution", ignoreCase = true) -> {
                "Redistribution Logic Explanation: Jaipur DH 1 currently holds 850 units of Amoxicillin (85 days coverage). Reallocating 330 units to Jaipur PHC 1 (12.5 km away) completely eliminates PHC 1's stock gap while leaving DH 1 with a healthy 52 days buffer."
            }
            promptText.contains("Jaipur", ignoreCase = true) || promptText.contains("summary", ignoreCase = true) || promptText.contains("operation", ignoreCase = true) -> {
                "District Summary (Jaipur): 24 total health facilities monitored (8 DH, 16 PHC). Overall bed occupancy is at 84%. Currently 1 critical stockout alert and 1 pending inter-facility redistribution transfer."
            }
            else -> {
                "SmartHealth Gemini AI Grounded Operational Copilot: Real-time intelligence connected to Jaipur health grid. Currently monitoring 24 facilities, 1 critical stockout risk (Amoxicillin at PHC 1), and 1 pending transfer."
            }
        }

        return """
            {
                "success": true,
                "message": "AI Copilot explanation generated",
                "data": {
                    "answer": "${answerText.replace("\"", "\\\"")}",
                    "groundedData": "Verified against local Room DB & Network State for Jaipur District."
                }
            }
        """.trimIndent()
    }

    private fun getGenericSuccessResponse(): String {
        return """
            {
                "success": true,
                "message": "Mock API success response",
                "data": null
            }
        """.trimIndent()
    }
}
