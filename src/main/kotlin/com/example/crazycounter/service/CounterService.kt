package com.example.crazycounter.service

import com.example.crazycounter.entity.CounterState
import com.example.crazycounter.entity.CounterVariation
import com.example.crazycounter.repository.CounterStateRepository
import org.springframework.stereotype.Service

@Service
class CounterService(private val counterStateRepository: CounterStateRepository) {
    
    fun processCount(channelId: String, userId: String, message: String): CounterValidationResult {
        val state = getOrCreateCounterState(channelId)
        val expectedNumber = state.getNextExpectedNumber()
        
        return try {
            val inputNumber = message.trim().toInt()
            
            when {
                inputNumber != expectedNumber -> {
                    state.reset()
                    counterStateRepository.save(state)
                    CounterValidationResult.wrongNumber(expectedNumber, inputNumber)
                }
                
                userId == state.lastUserId && state.currentNumber > 0 -> {
                    state.reset()
                    counterStateRepository.save(state)
                    CounterValidationResult.sameUserTwice()
                }
                
                else -> {
                    state.currentNumber = inputNumber
                    state.lastUserId = userId
                    counterStateRepository.save(state)
                    CounterValidationResult.success(inputNumber)
                }
            }
        } catch (e: NumberFormatException) {
            if (state.currentNumber > 0) {
                state.reset()
                counterStateRepository.save(state)
            }
            CounterValidationResult.invalidFormat(expectedNumber)
        }
    }
    
    fun getCounterState(channelId: String): CounterState = getOrCreateCounterState(channelId)
    
    fun resetCounter(channelId: String) {
        val state = getOrCreateCounterState(channelId)
        state.reset()
        counterStateRepository.save(state)
    }
    
    private fun getOrCreateCounterState(channelId: String): CounterState {
        return counterStateRepository.findById(channelId).orElseGet {
            val newState = CounterState(channelId, CounterVariation.BASIC)
            counterStateRepository.save(newState)
        }
    }
}