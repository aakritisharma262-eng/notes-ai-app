package com.aakriti.notesnative.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NotesTypography = Typography(
  headlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Black,
    fontSize = 28.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.4).sp,
  ),
  titleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.2).sp,
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
  ),
  labelMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
  ),
)

