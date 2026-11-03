package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Badge
import com.example.data.model.TextColorChoice
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DiplomaProgressScreen(
    sessionCount: Int,
    sessionHistory: List<String>,
    userLevel: Int,
    userLevelTitle: String,
    userXp: Int,
    badges: List<Badge>,
    textColor: TextColorChoice,
    onRequestDiplomaEmail: (email: String, name: String) -> Unit,
    onFastTrackTenSessions: () -> Unit
) {
    var recipientName by remember { mutableStateOf("Participante Destacado/a") }
    var recipientEmail by remember { mutableStateOf("") }
    var isDiplomaRequested by remember { mutableStateOf(false) }

    val isUnlocked = sessionCount >= 10

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gamification Level Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132D3B).copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gamification_level_hero")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Nivel $userLevel: $userLevelTitle",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "Progreso de Gamificación & Autocuidado",
                                fontSize = 12.sp,
                                color = Color(0xFF80CBC4)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC107).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⭐", fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Total acumulado: $userXp Puntos de Bienestar (XP)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    val currentBase = (userLevel - 1) * 120
                    val progressToNext = ((userXp - currentBase).toFloat() / 120f).coerceIn(0.05f, 1f)
                    LinearProgressIndicator(
                        progress = { progressToNext },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFD54F),
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gana XP en: Juegos de preguntas (+50), Diario seguro (+40), Respiración (+40), Consultas (+100).",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Insignias / Badges Showcase
        item {
            Text(
                text = "Insignias de Logro & Superación",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.color
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.chunked(2).forEach { rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowBadges.forEach { badge ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (badge.isUnlocked)
                                        Color(0xFF0F382E).copy(alpha = 0.90f)
                                    else
                                        Color(0xFF14222E).copy(alpha = 0.75f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = if (badge.isUnlocked) Color(0xFF80CBC4) else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .testTag("badge_${badge.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = badge.icon,
                                        fontSize = 26.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Column {
                                        Text(
                                            text = badge.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (badge.isUnlocked) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = if (badge.isUnlocked) "Desbloqueada ✨" else "Bloqueada 🔒",
                                            fontSize = 10.sp,
                                            color = if (badge.isUnlocked) Color(0xFFA5D6A7) else Color(0xFF90A4AE)
                                        )
                                    }
                                }
                            }
                        }
                        if (rowBadges.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Milestone 10 Sessions Header Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) Color(0xFF26200A).copy(alpha = 0.95f) else Color(0xFF102535).copy(alpha = 0.90f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isUnlocked) "¡Hito de 10 Sesiones Superado!" else "Camino de las 10 Sesiones",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFFFFD54F) else textColor.color
                            )
                            Text(
                                text = "Progreso de asistencia y superación emocional",
                                fontSize = 12.sp,
                                color = if (isUnlocked) Color(0xFFFFE082) else Color(0xFF80CBC4)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) Color(0xFFFFC107).copy(alpha = 0.3f) else Color(0xFF00796B).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.WorkspacePremium else Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) Color(0xFFFFD54F) else Color(0xFF80CBC4),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    val progressFloat = (sessionCount / 10f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressFloat },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (isUnlocked) Color(0xFFFFC107) else Color(0xFF26A69A),
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$sessionCount sesiones registradas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isUnlocked) "Desbloqueado 100%" else "Meta: 10 sesiones",
                            fontSize = 12.sp,
                            color = if (isUnlocked) Color(0xFFFFD54F) else Color(0xFF80DEEA)
                        )
                    }

                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onFastTrackTenSessions,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD54F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Completar 10 sesiones (Probar desbloqueo)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // UNLOCKED: Personalized Reflection
        if (isUnlocked) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F322D).copy(alpha = 0.95f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unlocked_reflection_card")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF80CBC4), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Reflexión Personalizada sobre tu Progreso Emocional",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE0F2F1)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Has recorrido un trayecto constante de introspección a lo largo de tus 10 sesiones en Asesorías Consultorias psicología (LivsForm Lykke). Tu evolución personal evidencia tres pilares clave:",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        ReflectionPoint(
                            title = "1. Regulación del Estrés y Tensión Somática",
                            detail = "Has aprendido a no reaccionar en piloto automático ante la sobrecarga, sustituyendo la parálisis por micro-pausas de respiración y descompresión consciente."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ReflectionPoint(
                            title = "2. Transmutación de la Frustración en Resiliencia",
                            detail = "Dejaste de interpretar el desvío de tus planes como un fracaso definitivo. Has desarrollado una flexibilidad cognitiva que te permite recalcular alternativas sin castigarte."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ReflectionPoint(
                            title = "3. Brújula Clara en tus Metas y Sueños",
                            detail = "Reconociste que el momento perfecto no existe y que los sueños cobran vida cuando das pasos pequeños y sostenibles, respetando tu autocuidado diario."
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "“El verdadero progreso emocional no consiste en no sentir miedo ni tristeza, sino en no permitir que decidan por ti.” — Asesorías Consultorias psicología",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }

            // UNLOCKED: Virtual Diploma Certificate Rendering
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFD4AF37), Color(0xFFFFDF73), Color(0xFFAA7C11))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .testTag("virtual_diploma_certificate")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ASESORÍAS CONSULTORIAS PSICOLOGÍA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B4965),
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "CENTRO DE PSICOLOGÍA & BIENESTAR INTEGRAL LIVSFORM LYKKE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7A6832),
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "DIPLOMA VIRTUAL DE SUPERACIÓN",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = Color(0xFF8B6508),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Y PROGRESO EMOCIONAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB8860B),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Se otorga con distinción y reconocimiento a:",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF555555)
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recipientName.ifBlank { "Participante de Asesorías Consultorias" },
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = Color(0xFF0F2B3E),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Por haber completado satisfactoriamente el ciclo de 10 sesiones de fortalecimiento personal, manejo del estrés y la frustración, y enfoque de metas y sueños.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF444444),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Fecha de Emisión",
                                    fontSize = 9.sp,
                                    color = Color(0xFF777777)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD4AF37)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Text(text = "OFICIAL", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "LivsForm Lykke",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2B3E)
                                )
                                Text(
                                    text = "asesoriaslykke@gmail.com",
                                    fontSize = 8.sp,
                                    color = Color(0xFF777777)
                                )
                            }
                        }
                    }
                }
            }

            // Order form for diploma
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF142634).copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Adquirir y Enviar Certificado Oficial a tu Correo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Complementa tu proceso y recibe en alta resolución tu diploma acreditativo con sello oficial expedido por Asesorías Consultorias psicología a tu bandeja de correo.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = { Text("Nombre para el Diploma") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD54F),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = recipientEmail,
                            onValueChange = { recipientEmail = it },
                            label = { Text("Correo al que deseas que se envíe") },
                            placeholder = { Text("ejemplo@gmail.com") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD54F),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onRequestDiplomaEmail(recipientEmail, recipientName)
                                isDiplomaRequested = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("request_diploma_send_btn")
                        ) {
                            Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF261D00))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Comprar / Solicitar Envío de Certificado",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF261D00)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Emite: Asesorías Consultorias psicología (asesoriaslykke@gmail.com)",
                            fontSize = 11.sp,
                            color = Color(0xFF80DEEA),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13222F).copy(alpha = 0.85f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF90A4AE), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reflexión Personalizada y Diploma Bloqueados",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Te faltan ${10 - sessionCount} sesiones para completar las 10 requeridas. Puedes realizarlas interactuando con tu Diario Emocional Seguro, los Juegos de Preguntas o los Ejercicios de Respiración.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Session History
        item {
            Text(
                text = "Historial de Sesiones Registradas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.color
            )
        }

        items(sessionHistory) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E29).copy(alpha = 0.80f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF80CBC4), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = item, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                }
            }
        }
    }
}

@Composable
fun ReflectionPoint(title: String, detail: String) {
    Column {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
        Text(text = detail, fontSize = 12.sp, lineHeight = 17.sp, color = Color.White.copy(alpha = 0.85f))
    }
}
