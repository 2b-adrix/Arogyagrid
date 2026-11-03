package com.example.arogyagrid

import com.example.arogyagrid.domain.ai.HealthcareAiEngine
import org.junit.Assert.*
import org.junit.Test

class HealthcareAiEngineTest {

    @Test
    fun testOrsEmergencySupplyQuery() {
        val res = HealthcareAiEngine.generateIntelligentResponse("Where can Amer get emergency ORS?")
        assertTrue(res.reply.contains("Sawai ManSingh (SMS) Hospital"))
        assertTrue(res.reply.contains("3,731 units"))
        assertEquals("hospital_map", res.actionRoute)
        assertNotNull(res.actionLabel)
    }

    @Test
    fun testColdChainExcursionQuery() {
        val res = HealthcareAiEngine.generateIntelligentResponse("Check cold chain temperature breach")
        assertTrue(res.reply.contains("8.4°C"))
        assertTrue(res.reply.contains("PHC Amer"))
        assertEquals("cold_chain", res.actionRoute)
    }

    @Test
    fun testEpidemicSurveillanceQuery() {
        val res = HealthcareAiEngine.generateIntelligentResponse("Analyze Amer diarrhea outbreak spike")
        assertTrue(res.reply.contains("+340% demand surge"))
        assertTrue(res.reply.contains("38 hours"))
        assertEquals("outbreak_surveillance", res.actionRoute)
    }

    @Test
    fun testJaipurDistrictSummaryQuery() {
        val res = HealthcareAiEngine.generateIntelligentResponse("Summarize Jaipur operations")
        assertTrue(res.reply.contains("15 registered & monitored live"))
        assertTrue(res.reply.contains("84.03%"))
        assertEquals("dashboard", res.actionRoute)
    }

    @Test
    fun testBedCapacityQuery() {
        val res = HealthcareAiEngine.generateIntelligentResponse("How many ICU beds and occupancy in Jaipur?")
        assertTrue(res.reply.contains("4,760 beds"))
        assertEquals("beds_staff", res.actionRoute)
    }
}
