package com.example.crazycounter.service

data class CounterValidationResult(
    val success: Boolean,
    val message: String,
    val currentNumber: Int
) {
    companion object {
        fun success(number: Int) = CounterValidationResult(
            success = true,
            message = "Count successful: $number",
            currentNumber = number
        )
        
        fun wrongNumber(expected: Int, actual: Int) = CounterValidationResult(
            success = false,
            message = "❌ Wrong number! Expected $expected but got $actual. Counter reset to 0.",
            currentNumber = 0
        )
        
        fun sameUserTwice() = CounterValidationResult(
            success = false,
            message = "❌ Same user cannot count twice in a row! Counter reset to 0.",
            currentNumber = 0
        )
        
        fun invalidFormat(expected: Int) = CounterValidationResult(
            success = false,
            message = "❌ Invalid number format! Expected $expected. Counter reset to 0.",
            currentNumber = 0
        )
    }
}