package com.liftley.habitrek.presentation.featureHomeScreen.model

import androidx.compose.ui.graphics.Color
import com.liftley.habitrek.domain.model.HabitListWithTodayStatusList

fun HabitListWithTodayStatusList.toHomeUiModelList(): List<HomeUiModel> {
    return this.habits.map {
        HomeUiModel(
            id = it.id,
            name = it.name,
            durationMinutes = it.durationMinutes,
            color = Color(it.color.color),
            isCompletedToday = it.id in completedIdSet
        )
    }
}