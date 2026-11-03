package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.data.model.EnvironmentTheme

/**
 * Fondo ambiental Lykke:
 * Inspirado en un amanecer/atardecer cálido y luminoso sobre cumbres de montañas nevadas
 * (Alpenglow, destellos dorados suaves, rosado alba y nieve fresca clara).
 */
@Composable
fun LykkeBackground(
    environment: EnvironmentTheme,
    overlayAlpha: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Gradiente maestro: Amanecer y Atardecer cálido y claro sobre nieve alpina
    val alpineDawnSunsetGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF6ED), // ☀️ Resplandor dorado claro del sol en la cumbre
            Color(0xFFFFEDE1), // 🌅 Melocotón cálido de amanecer/atardecer
            Color(0xFFFDE8EF), // 🌸 Alpenglow rosado sobre las laderas de nieve
            Color(0xFFF1F5F9), // ❄️ Nieve alpina pura y luminosa
            Color(0xFFE8F0F8)  // 🏔️ Brisa cristalina fresca de alta montaña
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(alpineDawnSunsetGradient)
    ) {
        if (environment.imageRes != null) {
            Image(
                painter = painterResource(id = environment.imageRes),
                contentDescription = environment.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.38f // Translucidez para dejar brillar la luz cálida de montaña
            )
            // Velo sutil translúcido cálido de nieve y sol naciente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFF7ED).copy(alpha = overlayAlpha.coerceIn(0.20f, 0.45f)),
                                Color(0xFFFDE8EF).copy(alpha = (overlayAlpha * 0.9f).coerceIn(0.25f, 0.50f)),
                                Color(0xFFEFF6FF).copy(alpha = (overlayAlpha * 1.1f).coerceIn(0.30f, 0.55f))
                            )
                        )
                    )
            )
        } else {
            // Atmósfera ambiental cálida de nieve y sol de montaña sin imagen estática
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(alpineDawnSunsetGradient)
            )
        }

        // Render foreground content
        content()
    }
}
