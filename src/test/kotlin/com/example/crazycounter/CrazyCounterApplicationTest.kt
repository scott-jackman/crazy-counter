package com.example.crazycounter

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

@SpringBootTest
@ActiveProfiles("test")
class CrazyCounterApplicationTest : AbstractTestNGSpringContextTests() {

    @Test
    fun `application context should load successfully`() {
        // This test verifies that the Spring application context loads without errors
        // If the context fails to load, this test will fail
    }
}