package com.example.crazycounter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrazyCounterApplication

fun main(args: Array<String>) {
    runApplication<CrazyCounterApplication>(*args)
}