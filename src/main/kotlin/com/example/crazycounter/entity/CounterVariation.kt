package com.example.crazycounter.entity

enum class CounterVariation(val displayName: String) {
    BASIC("Basic Counter") {
        override fun getNextNumber(currentNumber: Int): Int = currentNumber + 1
    };

    abstract fun getNextNumber(currentNumber: Int): Int
}