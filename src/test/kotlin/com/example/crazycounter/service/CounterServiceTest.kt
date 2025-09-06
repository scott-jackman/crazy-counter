package com.example.crazycounter.service

import com.example.crazycounter.entity.CounterState
import com.example.crazycounter.entity.CounterVariation
import com.example.crazycounter.repository.CounterStateRepository
import org.mockito.kotlin.*
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.Assert.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.*

class CounterServiceTest : AbstractTestNGSpringContextTests() {

    private lateinit var counterStateRepository: CounterStateRepository
    private lateinit var counterService: CounterService

    @BeforeMethod
    fun setUp() {
        counterStateRepository = mock()
        counterService = CounterService(counterStateRepository)
    }

    @Test
    fun `processCount should succeed with correct number`() {
        // Given
        val channelId = "test-channel"
        val userId = "test-user"
        val state = CounterState(channelId, CounterVariation.BASIC)
        whenever(counterStateRepository.findById(channelId)).thenReturn(Optional.of(state))

        // When
        val result = counterService.processCount(channelId, userId, "1")

        // Then
        assertTrue(result.success)
        assertEquals(result.currentNumber, 1)
        assertEquals(result.message, "Count successful: 1")
        verify(counterStateRepository).save(any())
    }

    @Test
    fun `processCount should reset counter with wrong number`() {
        // Given
        val channelId = "test-channel"
        val userId = "test-user"
        val state = CounterState(channelId, CounterVariation.BASIC).apply {
            currentNumber = 5
            lastUserId = "other-user"
        }
        whenever(counterStateRepository.findById(channelId)).thenReturn(Optional.of(state))

        // When
        val result = counterService.processCount(channelId, userId, "10")

        // Then
        assertFalse(result.success)
        assertEquals(result.currentNumber, 0)
        assertTrue(result.message.contains("Wrong number! Expected 6 but got 10"))
        verify(counterStateRepository).save(any())
    }

    @Test
    fun `processCount should reset counter when same user counts twice`() {
        // Given
        val channelId = "test-channel"
        val userId = "test-user"
        val state = CounterState(channelId, CounterVariation.BASIC).apply {
            currentNumber = 5
            lastUserId = userId
        }
        whenever(counterStateRepository.findById(channelId)).thenReturn(Optional.of(state))

        // When
        val result = counterService.processCount(channelId, userId, "6")

        // Then
        assertFalse(result.success)
        assertEquals(result.currentNumber, 0)
        assertTrue(result.message.contains("Same user cannot count twice"))
        verify(counterStateRepository).save(any())
    }

    @Test
    fun `processCount should handle invalid number format`() {
        // Given
        val channelId = "test-channel"
        val userId = "test-user"
        val state = CounterState(channelId, CounterVariation.BASIC)
        whenever(counterStateRepository.findById(channelId)).thenReturn(Optional.of(state))

        // When
        val result = counterService.processCount(channelId, userId, "not-a-number")

        // Then
        assertFalse(result.success)
        assertEquals(result.currentNumber, 0)
        assertTrue(result.message.contains("Invalid number format"))
    }

    @Test
    fun `resetCounter should reset counter state`() {
        // Given
        val channelId = "test-channel"
        val state = CounterState(channelId, CounterVariation.BASIC).apply {
            currentNumber = 10
            lastUserId = "test-user"
        }
        whenever(counterStateRepository.findById(channelId)).thenReturn(Optional.of(state))

        // When
        counterService.resetCounter(channelId)

        // Then
        verify(counterStateRepository).save(argThat { 
            currentNumber == 0 && lastUserId == ""
        })
    }

    @Test
    fun `getCounterState should create new state if not exists`() {
        // Given
        val channelId = "new-channel"
        val newState = CounterState(channelId, CounterVariation.BASIC)
        whenever(counterStateRepository.findById(channelId)).thenReturn(Optional.empty())
        whenever(counterStateRepository.save(any<CounterState>())).thenReturn(newState)

        // When
        val result = counterService.getCounterState(channelId)

        // Then
        assertEquals(result.channelId, channelId)
        assertEquals(result.variation, CounterVariation.BASIC)
        verify(counterStateRepository).save(any())
    }
}