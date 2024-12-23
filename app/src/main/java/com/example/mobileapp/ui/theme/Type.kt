package com.example.mobileapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mobileapp.R

val title = FontFamily(Font(R.font.title))
val description = FontFamily(Font(R.font.description))
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = title,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        color = Purple40,
        fontFamily = title,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = description,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)