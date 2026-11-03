package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TextColorChoice
import com.example.ui.BreathingExerciseState

@Composable
fun BreathingScreen(
    state: BreathingExerciseState,
    textColor: TextColorChoice,
    onStartExercise: (String) -> Unit,
    onStopExercise: () -> Unit
) {
    // Dynamic scale depending on breath phase
    val targetScale = when (state.phase) {
        "Inhala" -> 1.35f
        "Sostén" -> 1.35f
        "Exhala" -> 0.85f
        else -> 1.0f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 1000),
        label = "breath_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & description
        Text(
            text = "Respiración Rápida & Descompresión",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor.color
        )
        Text(
            text = "Sincroniza el sistema parasimpático para reducir el cortisol y disipar la sensación de frustración o pánico en menos de 2 minutos.",
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = textColor.color.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Exercise Type Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onStartExercise("4-7-8") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.exerciseName.contains("4-7-8")) Color(0xFF00695C) else Color(0xFF162938)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Método 4-7-8 (Calma)", fontSize = 12.sp)
            }

            Button(
                onClick = { onStartExercise("box") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.exerciseName.contains("Cuadrada")) Color(0xFF00695C) else Color(0xFF162938)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Caja 4-4-4-4 (Enfoque)", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Breathing Orb Canvas
        Box(
            modifier = Modifier
                .size(240.dp)
                .testTag("breathing_visual_orb"),
            contentAlignment = Alignment.Center
        ) {
            val primaryColor = when (state.phase) {
                "Inhala" -> Color(0xFF4DB6AC)
                "Sostén" -> Color(0xFFFFD54F)
                "Exhala" -> Color(0xFF81D4FA)
                else -> Color(0xFF80CBC4)
            }

            Canvas(modifier = Modifier.size(220.dp * animatedScale)) {
                // Outer gentle aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.35f),
                            primaryColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2f
                )

                // Glowing rim
                drawCircle(
                    color = primaryColor.copy(alpha = 0.7f),
                    radius = size.minDimension / 2.3f,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Inner core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.5f),
                            Color(0xFF0C1D2A)
                        )
                    ),
                    radius = size.minDimension / 2.6f
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.phase,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (state.isRunning && state.secondsRemaining > 0) {
                    Text(
                        text = "${state.secondsRemaining}s",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = "Ciclo ${state.cycleCount}/${state.targetCycles}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                } else if (!state.isRunning) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Phase instruction card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF102737).copy(alpha = 0.90f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.phaseInstruction,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = textColor.color,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Action controls
        if (!state.isRunning) {
            Button(
                onClick = { onStartExercise("4-7-8") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_breathing_btn")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión de Respiración Guiada", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onStopExercise,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("stop_breathing_btn")
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Detener Ejercicio", fontSize = 14.sp)
            }
        }

        // Psychological benefits explanation
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1924).copy(alpha = 0.85f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Beneficio Clínico Comprobado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF80CBC4)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exhalar durante el doble de tiempo que la inhalación activa el nervio vago y ralentiza de inmediato las pulsaciones cardíacas. Cada ciclo completado suma a tus 10 sesiones de evolución.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}
