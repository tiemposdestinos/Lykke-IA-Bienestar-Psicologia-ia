package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animación Composable en Jetpack Compose que muestra la sinapsis conectando
 * las dos semillas separadas en forma de hoja (una amarilla y otra verde)
 * unidas por un vástago/sinapsis con nodo circular azul, dentro de dos curvas rojas circulares.
 * Representa la identidad Danesa Lykke y la psicología de conexión mental.
 */
@Composable
fun LykkeSynapseAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    showAura: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SynapseTransition")

    // 1. Pulso continuo del nodo central y energía de la sinapsis
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SynapsePulse"
    )

    // 2. Respiración suave de las semillas
    val seedScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SeedBreathing"
    )

    // 3. Rotación sutil de los arcos rojos circundantes
    val arcRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ArcRotation"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = w.coerceAtMost(h) / 2f

        // --- AURA DE FONDO (Opcional) ---
        if (showAura) {
            val auraRadius = radius * (0.85f + 0.15f * seedScale)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.20f),
                        Color(0xFFEF4444).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = auraRadius
                ),
                radius = auraRadius,
                center = center
            )
        }

        // --- 1. DOS CURVAS ROJAS CIRCULARES EXTERIORES ---
        val redArcStroke = radius * 0.065f
        val arcPadding = radius * 0.12f
        val arcSize = Size((radius - arcPadding) * 2, (radius - arcPadding) * 2)
        val arcTopLeft = Offset(center.x - (radius - arcPadding), center.y - (radius - arcPadding))

        // Arco Rojo 1 (Superior / Lateral)
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFEF4444),
                    Color(0xFFF87171),
                    Color(0xFFDC2626),
                    Color(0xFFEF4444)
                ),
                center = center
            ),
            startAngle = arcRotation + 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = redArcStroke, cap = StrokeCap.Round)
        )

        // Arco Rojo 2 (Inferior / Lateral Opuesto)
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFDC2626),
                    Color(0xFFEF4444),
                    Color(0xFFF87171),
                    Color(0xFFDC2626)
                ),
                center = center
            ),
            startAngle = arcRotation + 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = redArcStroke, cap = StrokeCap.Round)
        )

        // --- 2. TRAYECTORIA DE LA SINAPSIS (LÍNEA / VÁSTAGO EN 'S') ---
        val synapsePath = Path().apply {
            // Desde la semilla amarilla (arriba a la izquierda) pasando por el centro hasta la semilla verde (abajo a la derecha)
            val pStart = Offset(center.x - radius * 0.42f, center.y - radius * 0.18f)
            val pEnd = Offset(center.x + radius * 0.42f, center.y + radius * 0.18f)

            val cp1 = Offset(center.x - radius * 0.15f, center.y + radius * 0.28f)
            val cp2 = Offset(center.x + radius * 0.15f, center.y - radius * 0.28f)

            moveTo(pStart.x, pStart.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pEnd.x, pEnd.y)
        }

        // Dibuja la línea base de la sinapsis
        val synapseStrokeWidth = radius * 0.035f
        drawPath(
            path = synapsePath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFD54F),
                    Color(0xFF38BDF8),
                    Color(0xFF4ADE80)
                )
            ),
            style = Stroke(width = synapseStrokeWidth, cap = StrokeCap.Round)
        )

        // --- 3. PULSO ELÉCTRICO / FOTÓN ANIMADO A LO LARGO DE LA SINAPSIS ---
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(synapsePath, false)
        val pathLength = pathMeasure.length

        if (pathLength > 0f) {
            // Partícula 1: viajando hacia adelante
            val distance1 = pulseProgress * pathLength
            val position1 = pathMeasure.getPosition(distance1)
            drawCircle(
                color = Color.White,
                radius = radius * 0.038f,
                center = position1
            )
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                radius = radius * 0.065f,
                center = position1
            )

            // Partícula 2: viajando con retraso de fase
            val distance2 = ((pulseProgress + 0.5f) % 1f) * pathLength
            val position2 = pathMeasure.getPosition(distance2)
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = 0.9f),
                radius = radius * 0.030f,
                center = position2
            )
        }

        // --- 4. SEMILLA 1: AMARILLA EN FORMA DE HOJA (Lado Izquierdo Superior) ---
        drawLeafSeed(
            scope = this,
            center = Offset(center.x - radius * 0.38f, center.y - radius * 0.20f),
            baseRadius = radius * 0.28f * seedScale,
            rotationAngle = -28f,
            primaryColor = Color(0xFFFFD54F),
            secondaryColor = Color(0xFFF59E0B),
            isFlipped = false
        )

        // --- 5. SEMILLA 2: VERDE EN FORMA DE HOJA (Lado Derecho Inferior) ---
        drawLeafSeed(
            scope = this,
            center = Offset(center.x + radius * 0.38f, center.y + radius * 0.20f),
            baseRadius = radius * 0.28f * seedScale,
            rotationAngle = 152f,
            primaryColor = Color(0xFF4ADE80),
            secondaryColor = Color(0xFF10B981),
            isFlipped = true
        )

        // --- 6. NODO CIRCULAR CENTRAL (Simetría con las curvas rojas) ---
        val nodeRadius = radius * 0.095f
        val pulseRingRadius = nodeRadius * (1f + 0.6f * pulseProgress)
        val pulseRingAlpha = (1f - pulseProgress).coerceIn(0f, 1f)

        // Onda expansiva del nodo central
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = pulseRingAlpha * 0.6f),
            radius = pulseRingRadius,
            center = center,
            style = Stroke(width = radius * 0.02f)
        )

        // Nodo circular azul brillante con resplandor
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE0F2FE),
                    Color(0xFF38BDF8),
                    Color(0xFF0284C7)
                ),
                center = center,
                radius = nodeRadius
            ),
            radius = nodeRadius,
            center = center
        )

        // Centro brillante blanco
        drawCircle(
            color = Color.White,
            radius = nodeRadius * 0.35f,
            center = center
        )
    }
}

/**
 * Función auxiliar para dibujar una semilla orgánica en forma de hoja con nervadura y gradiente.
 */
private fun drawLeafSeed(
    scope: DrawScope,
    center: Offset,
    baseRadius: Float,
    rotationAngle: Float,
    primaryColor: Color,
    secondaryColor: Color,
    isFlipped: Boolean
) {
    val rad = Math.toRadians(rotationAngle.toDouble())
    val cosA = cos(rad).toFloat()
    val sinA = sin(rad).toFloat()

    fun transform(x: Float, y: Float): Offset {
        val flippedX = if (isFlipped) -x else x
        val rx = flippedX * cosA - y * sinA
        val ry = flippedX * sinA + y * cosA
        return Offset(center.x + rx, center.y + ry)
    }

    val leafPath = Path().apply {
        val pTip = transform(0f, -baseRadius)
        val pBase = transform(0f, baseRadius * 0.85f)
        val pRightControl = transform(baseRadius * 0.72f, -baseRadius * 0.15f)
        val pLeftControl = transform(-baseRadius * 0.72f, -baseRadius * 0.15f)

        moveTo(pTip.x, pTip.y)
        quadraticBezierTo(pRightControl.x, pRightControl.y, pBase.x, pBase.y)
        quadraticBezierTo(pLeftControl.x, pLeftControl.y, pTip.x, pTip.y)
        close()
    }

    // Relleno de la hoja con gradiente suave
    scope.drawPath(
        path = leafPath,
        brush = Brush.linearGradient(
            colors = listOf(primaryColor, secondaryColor),
            start = transform(0f, -baseRadius),
            end = transform(0f, baseRadius * 0.85f)
        )
    )

    // Nervadura central sutil de la hoja
    val veinPath = Path().apply {
        val start = transform(0f, -baseRadius * 0.7f)
        val end = transform(0f, baseRadius * 0.7f)
        moveTo(start.x, start.y)
        lineTo(end.x, end.y)
    }

    scope.drawPath(
        path = veinPath,
        color = Color.White.copy(alpha = 0.45f),
        style = Stroke(width = baseRadius * 0.05f, cap = StrokeCap.Round)
    )
}
