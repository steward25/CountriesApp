package com.stewardapostol.countriesapp.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


// Custom colors (based on your screenshot)
private val CustomDarkColorScheme = darkColorScheme(
    primary = Color(0xFFBCAEF3), // Light purple (button, accent)
    onPrimary = Color.Black,
    secondary = Color(0xFF4C6EF5), // Blue (header)
    onSecondary = Color.White,
    background = Color(0xFF000000), // Black background
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF1E1E1E), // Dark gray input
    onSurface = Color.White,
    error = Color.Red,
    onError = Color.White,
)

// Optional: Light color scheme if you want
private val CustomLightColorScheme = lightColorScheme(
    primary = Color(0xFF8E77FF),
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF1F1F1),
    onSurface = Color.Black
)

// Optional: Custom typography
private val CustomTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4C6EF5) // Blue title
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )
)
@Composable
fun CountriesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> CustomDarkColorScheme
        else -> CustomLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}
