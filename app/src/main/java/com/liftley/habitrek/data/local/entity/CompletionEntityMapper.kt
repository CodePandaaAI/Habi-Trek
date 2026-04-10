package com.liftley.habitrek.data.local.entity

import com.liftley.habitrek.domain.model.Completion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Completion.toEntity(): CompletionEntity {
    return CompletionEntity(
        id = id,
        habitId = habitId,
        dateMillis = dateMillis
    )
}

fun Flow<List<CompletionEntity>>.toFlowCompletionList(): Flow<List<Completion>> {
    return this.map { list -> // 1. Map the Flow (gives you the List)
        list.map { entity ->   // 2. Map the List (gives you the Entity)
            Completion(
                id = entity.id,
                habitId = entity.habitId,
                dateMillis = entity.dateMillis
            )
        }
    }
}