package com.example.arogyagrid

import com.example.arogyagrid.data.remote.AiChatRequest
import com.example.arogyagrid.data.remote.InventoryTransactionRequest
import com.example.arogyagrid.data.remote.LoginRequest
import com.example.arogyagrid.data.remote.NetworkClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendApiUnitTest {

    @Test
    fun testLoginApiEndpoint() = runBlocking {
        val apiService = NetworkClient.apiService
        val response = apiService.login(LoginRequest("district_officer_jaipur", "pass123"))

        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("Jaipur", response.data?.district)
        assertEquals("DISTRICT_OFFICER", response.data?.role)
    }

    @Test
    fun testGetFacilitiesApiEndpoint() = runBlocking {
        val apiService = NetworkClient.apiService
        val response = apiService.getFacilities()

        assertTrue(response.success)
        assertNotNull(response.data)
        assertTrue((response.data?.size ?: 0) > 0)
        assertEquals("Jaipur DH 1", response.data?.first()?.name)
    }

    @Test
    fun testGetMedicinesApiEndpoint() = runBlocking {
        val apiService = NetworkClient.apiService
        val response = apiService.getMedicines()

        assertTrue(response.success)
        assertNotNull(response.data)
        assertTrue((response.data?.size ?: 0) > 0)
    }

    @Test
    fun testRecordTransactionApiEndpoint() = runBlocking {
        val apiService = NetworkClient.apiService
        val response = apiService.recordTransaction(
            InventoryTransactionRequest("FAC-002", "MED-001", 100, "RECEIVE", "BATCH-01", "2026-12-31")
        )

        assertTrue(response.success)
        assertEquals(true, response.data)
    }

    @Test
    fun testAiChatGroundedResponseApiEndpoint() = runBlocking {
        val apiService = NetworkClient.apiService
        val response = apiService.askGemini(AiChatRequest("Why is Amoxicillin high risk?", "Jaipur"))

        assertTrue(response.success)
        assertNotNull(response.data)
        assertTrue(response.data?.answer?.contains("Amoxicillin") == true)
    }
}
