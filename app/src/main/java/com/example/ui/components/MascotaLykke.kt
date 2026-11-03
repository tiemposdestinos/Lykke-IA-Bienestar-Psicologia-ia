package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.LykkeVoiceManager
import com.example.audio.TipoVozMascota
import com.example.data.MascotaInteractivaEngine
import com.example.data.PerfilUsuario
import com.example.ui.theme.AzulPlatinadoBorder
import com.example.ui.theme.AzulPlatinadoCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Componente dinámico de la Mascota / Entidad Lykke.
 * Cuenta con animación de salto (bounce), pulso de aura y brillo reactivo
 * que se activa al completar una tarea diaria o interactuar con el botón de voz.
 */
@Composable
fun MascotaLykke(
    perfil: PerfilUsuario,
    languageCode: String = "es",
    isTaskCompletedTrigger: Boolean = false,
    isVoiceInteractingTrigger: Boolean = false,
    voiceManager: LykkeVoiceManager? = null,
    onVoiceSpeak: ((String) -> Unit)? = null,
    onOpenVoiceConfig: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val localVoiceManager = remember { voiceManager ?: LykkeVoiceManager(context) }
    val scope = rememberCoroutineScope()
    var isExcited by remember { mutableStateOf(false) }
    var mostrarDialogoVoz by remember { mutableStateOf(false) }
    var mensajeMascota by remember {
        mutableStateOf(MascotaInteractivaEngine.generarDialogoMascota(perfil, languageCode))
    }

    // Actualizar mensaje al cambiar perfil o idioma
    LaunchedEffect(perfil, languageCode) {
        mensajeMascota = MascotaInteractivaEngine.generarDialogoMascota(perfil, languageCode)
    }

    // Animación reactiva ante cumplimiento de tarea diaria
    LaunchedEffect(isTaskCompletedTrigger) {
        if (isTaskCompletedTrigger) {
            isExcited = true
            mensajeMascota = "¡Fantástico! 🎉 Acabas de completar tu tarea diaria. ¡Tu racha y bienestar siguen creciendo!"
            delay(2800)
            isExcited = false
        }
    }

    // Animación reactiva ante interacción con voz
    LaunchedEffect(isVoiceInteractingTrigger) {
        if (isVoiceInteractingTrigger) {
            isExcited = true
            delay(2500)
            isExcited = false
        }
    }

    // Transición infinita para respiración natural y parpadeo
    val infiniteTransition = rememberInfiniteTransition(label = "PetBreathing")
    val idleFloat by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdleFloat"
    )

    // Animación de rebote (bounce/scale) excitada
    val bounceAnim = remember { Animatable(1f) }
    val rotateAnim = remember { Animatable(0f) }

    LaunchedEffect(isExcited) {
        if (isExcited) {
            launch {
                bounceAnim.animateTo(
                    targetValue = 1.25f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                )
                bounceAnim.animateTo(1f, animationSpec = tween(400))
            }
            launch {
                rotateAnim.animateTo(10f, animationSpec = tween(150))
                rotateAnim.animateTo(-10f, animationSpec = tween(150))
                rotateAnim.animateTo(0f, animationSpec = tween(150))
            }
        }
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    if (isExcited) Color(0xFFFFD54F) else AzulPlatinadoBorder,
                    Color(0xFF38BDF8).copy(alpha = 0.6f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mascota_lykke_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isExcited) Color(0xFF4ADE80) else Color(0xFF38BDF8))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ENTIDAD COMPAÑERA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        color = Color(0xFF38BDF8)
                    )
                }

                if (onOpenVoiceConfig != null) {
                    TextButton(
                        onClick = onOpenVoiceConfig,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Bautizar / Voz",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar / Mascota interactiva animada
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .offset(y = idleFloat.dp)
                        .scale(bounceAnim.value)
                        .rotate(rotateAnim.value)
                        .clickable {
                            scope.launch {
                                isExcited = true
                                bounceAnim.animateTo(1.22f, spring(Spring.DampingRatioMediumBouncy))
                                bounceAnim.animateTo(1f)
                            }
                            mostrarDialogoVoz = true
                        }
                        .testTag("mascota_lykke_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    PetCanvasGraphic(isExcited = isExcited, size = 84.dp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Globo de diálogo y nombre
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = perfil.apodoConciencia.ifBlank { "Conciencia" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(containerColor = if (isExcited) Color(0xFF10B981) else Color(0xFF0284C7)) {
                            Text(
                                text = if (isExcited) "¡FELIZ! ✨" else "ACTIVA",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .clickable { mostrarDialogoVoz = true }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = mensajeMascota,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra inferior con acciones de voz, diálogo inteligente y refrescar reflexión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        mensajeMascota = MascotaInteractivaEngine.generarDialogoMascota(perfil, languageCode)
                        scope.launch {
                            isExcited = true
                            delay(1000)
                            isExcited = false
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, AzulPlatinadoBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Reflexión", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                }

                Button(
                    onClick = { mostrarDialogoVoz = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mascota_hablar_button")
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Hablar 🎙️", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                if (onVoiceSpeak != null) {
                    Button(
                        onClick = {
                            onVoiceSpeak(mensajeMascota)
                            scope.launch {
                                isExcited = true
                                delay(2000)
                                isExcited = false
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F382E)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mascota_speak_button")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFD54F))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Escuchar", fontSize = 10.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal de diálogo interactivo de voz con la IA Conciencia
    if (mostrarDialogoVoz) {
        DialogoVozInteligenteLykke(
            perfil = perfil,
            languageCode = languageCode,
            voiceManager = localVoiceManager,
            onDismiss = { mostrarDialogoVoz = false },
            onActionCompleted = { accion ->
                scope.launch {
                    isExcited = true
                    delay(1500)
                    isExcited = false
                }
            }
        )
    }
}

/**
 * Gráfico vectorial dibujado en Canvas que da vida a la Mascota Lykke:
 * Un simpático ser orgánico con semillas de energía, ojos expresivos y sonrisa alegre.
 */
@Composable
private fun PetCanvasGraphic(
    isExcited: Boolean,
    size: Dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = w.coerceAtMost(h) / 2f

        // Aura exterior con gradiente místico
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    if (isExcited) Color(0xFFFFD54F).copy(alpha = 0.5f) else Color(0xFF38BDF8).copy(alpha = 0.35f),
                    Color(0xFF4ADE80).copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // Orejas / Hojas orgánicas
        val earRadius = radius * 0.32f
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = earRadius,
            center = Offset(center.x - radius * 0.55f, center.y - radius * 0.45f)
        )
        drawCircle(
            color = Color(0xFF4ADE80),
            radius = earRadius,
            center = Offset(center.x + radius * 0.55f, center.y - radius * 0.45f)
        )

        // Cuerpo / Cabeza principal de la mascota
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE0F2FE),
                    Color(0xFFBAE6FD),
                    Color(0xFF7DD3FC)
                ),
                start = Offset(center.x, center.y - radius * 0.7f),
                end = Offset(center.x, center.y + radius * 0.7f)
            ),
            radius = radius * 0.65f,
            center = center
        )

        // Mejillas rosadas
        drawCircle(
            color = Color(0xFFF472B6).copy(alpha = 0.6f),
            radius = radius * 0.12f,
            center = Offset(center.x - radius * 0.35f, center.y + radius * 0.12f)
        )
        drawCircle(
            color = Color(0xFFF472B6).copy(alpha = 0.6f),
            radius = radius * 0.12f,
            center = Offset(center.x + radius * 0.35f, center.y + radius * 0.12f)
        )

        // Ojos expresivos
        val eyeRadius = if (isExcited) radius * 0.11f else radius * 0.09f
        val eyeY = center.y - radius * 0.06f
        val leftEyeX = center.x - radius * 0.22f
        val rightEyeX = center.x + radius * 0.22f

        drawCircle(color = Color(0xFF0F172A), radius = eyeRadius, center = Offset(leftEyeX, eyeY))
        drawCircle(color = Color(0xFF0F172A), radius = eyeRadius, center = Offset(rightEyeX, eyeY))

        // Brillo en los ojos
        drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(leftEyeX - eyeRadius * 0.3f, eyeY - eyeRadius * 0.3f))
        drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(rightEyeX - eyeRadius * 0.3f, eyeY - eyeRadius * 0.3f))

        // Sonrisa feliz
        val smilePath = Path().apply {
            moveTo(center.x - radius * 0.16f, center.y + radius * 0.15f)
            quadraticBezierTo(
                center.x,
                center.y + (if (isExcited) radius * 0.34f else radius * 0.26f),
                center.x + radius * 0.16f,
                center.y + radius * 0.15f
            )
        }
        drawPath(
            path = smilePath,
            color = Color(0xFF0F172A),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = radius * 0.05f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}
