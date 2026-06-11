package com.liftley.habitrek.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.liftley.habitrek.R

@OptIn(ExperimentalTextApi::class)
val CustomFontFamily = FontFamily(
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.ExtraLight,
        variationSettings = FontVariation.Settings(FontWeight.ExtraLight, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontWeight.Light, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontWeight.Normal, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontWeight.Medium, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontWeight.SemiBold, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontWeight.Bold, FontStyle.Normal)
    )
)
val baseline = Typography()
val Typography = Typography(
    headlineMedium = baseline.headlineMedium.copy(fontFamily = CustomFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = CustomFontFamily)
)