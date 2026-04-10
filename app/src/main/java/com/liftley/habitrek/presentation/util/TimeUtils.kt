package com.liftley.habitrek.presentation.util

fun Int.toDurationString(): String {
    val hours = this / 60
    val minutes = this % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}hr ${minutes}min / day"
        hours > 0 -> "${hours}hr / day"
        else -> "${minutes}min / day"
    }
}