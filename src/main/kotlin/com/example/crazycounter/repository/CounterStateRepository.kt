package com.example.crazycounter.repository

import com.example.crazycounter.entity.CounterState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CounterStateRepository : JpaRepository<CounterState, String>