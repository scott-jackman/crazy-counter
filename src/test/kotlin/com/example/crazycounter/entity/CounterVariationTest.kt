package com.example.crazycounter.entity

import org.testng.Assert.*
import org.testng.annotations.Test

class CounterVariationTest {

    @Test
    fun `BASIC variation should increment by 1`() {
        // Given
        val variation = CounterVariation.BASIC

        // When & Then
        assertEquals(variation.getNextNumber(0), 1)
        assertEquals(variation.getNextNumber(1), 2)
        assertEquals(variation.getNextNumber(5), 6)
        assertEquals(variation.getNextNumber(100), 101)
    }

    @Test
    fun `BASIC variation should have correct display name`() {
        // Given
        val variation = CounterVariation.BASIC

        // When & Then
        assertEquals(variation.displayName, "Basic Counter")
    }

    @Test
    fun `BASIC variation should handle negative numbers`() {
        // Given
        val variation = CounterVariation.BASIC

        // When & Then
        assertEquals(variation.getNextNumber(-1), 0)
        assertEquals(variation.getNextNumber(-10), -9)
    }
}