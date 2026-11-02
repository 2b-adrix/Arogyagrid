package com.example.arogyagrid

import com.example.arogyagrid.data.remote.dto.AiChatRequest
import com.example.arogyagrid.data.remote.dto.InventoryTransactionRequest
import com.example.arogyagrid.data.remote.dto.LoginRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class BackendApiUnitTest {

    @Test
    fun testLoginApiEndpointContract() = runBlocking {
        val request = LoginRequest("district_officer_jaipur@arogyagrid.in", "pass123")
        assertEquals("district_officer_jaipur@arogyagrid.in", request.email)
        assertEquals("pass123", request.password)
    }

    @Test
    fun testInventoryTransactionRequestContract() = runBlocking {
        val request = InventoryTransactionRequest(
            facilityId = "FAC-002",
            medicineId = "MED-001",
            type = "RECEIVE",
            quantity = 100,
            batchNumber = "BATCH-01",
            remarks = "2026-12-31"
        )
        assertEquals("FAC-002", request.facilityId)
        assertEquals("MED-001", request.medicineId)
        assertEquals(100, request.quantity)
        assertEquals("RECEIVE", request.type)
    }

    @Test
    fun testAiChatRequestContract() = runBlocking {
        val request = AiChatRequest(
            prompt = "Why is Amoxicillin high risk?",
            context = "Jaipur"
        )
        assertEquals("Why is Amoxicillin high risk?", request.prompt)
        assertEquals("Jaipur", request.context)
    }
}
