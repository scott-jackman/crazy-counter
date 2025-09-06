package com.example.crazycounter.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "counter_states")
data class CounterState(
    @Id
    val channelId: String = "",
    
    @Column(nullable = false)
    var currentNumber: Int = 0,
    
    @Column(nullable = false)
    var lastUserId: String = "",
    
    @Column(nullable = false)
    var lastUpdateTime: LocalDateTime = LocalDateTime.now(),
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var variation: CounterVariation = CounterVariation.BASIC
) {
    constructor(channelId: String, variation: CounterVariation) : this(
        channelId = channelId,
        variation = variation,
        currentNumber = 0,
        lastUserId = "",
        lastUpdateTime = LocalDateTime.now()
    )
    
    fun getNextExpectedNumber(): Int = variation.getNextNumber(currentNumber)
    
    fun reset() {
        currentNumber = 0
        lastUserId = ""
        lastUpdateTime = LocalDateTime.now()
    }
}