package com.liftley.habitrek.domain.model

data class Completion(
    val id: Int,
    val habitId: Int,
    val dateMillis: Long
)