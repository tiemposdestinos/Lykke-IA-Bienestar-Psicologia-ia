package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JornadaGratuita
import com.example.data.model.TextColorChoice
import com.example.data.model.VirtualGift
import com.example.ui.theme.*

@Composable
fun SolidarityScreen(
    gifts: List<VirtualGift>,
    purchasedGifts: List<VirtualGift>,
    totalDonatedUsd: Double,
    textColor: TextColorChoice,
    jornadas: List<JornadaGratuita> = emptyList(),
    completedTasksCount: Int = 0,
    onPurchaseGift: (VirtualGift) -> Unit,
    onMakeDonation: (Double) -> Unit
) {
    var customDonationInput by remember { mutableStateOf("10") }

    val effectiveJornadas = remember(jornadas) {
        if (jornadas.isNotEmpty()) jornadas else listOf(
            JornadaGratuita("j_1", "Jornada de Arteterapia y Expresión", "Canalización de emociones a través de pintura y modelado.", "🎨", 25, true),
            JornadaGratuita("j_2", "Jornada Pequeños Héroes de la Calma", "Talleres lúdicos de respiración guiada para niños.", "🧘‍♀️", 30, true),
            JornadaGratuita("j_3", "Jornada de Contención del Miedo y Acoso", "Acompañamiento psicológico grupal en escuelas vulnerables.", "🛡️", 40, true),
            JornadaGratuita("j_4", "Jornada Integral Lykke", "Cuadernillos terapéuticos y orientación familiar gratuita.", "🌳", 50, true)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Header en Azul Platinado
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AzulPlatinadoBorder, AzulPlatinadoHighlight))),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("solidarity_hero_banner")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Recompensas 100% Gratuitas",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Regalos y Jornadas Infantiles",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¡La opción de regalos y jornadas son totalmente gratuitos! Cada vez que completas cualquier tarea o prueba en las demás áreas (incluyendo las 5 pruebas de desahogo), recibes automáticamente recompensas que financian el bienestar infantil.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Indicador de avance de tareas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎁",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "¡Cada tarea completada desbloquea recompensas!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "Diario Seguro • Chat de Desahogo • 5 Pruebas • Respiración",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // 2. Sección: Regalos Virtuales Gratuitos de Buena Suerte
        item {
            Column {
                Text(
                    text = "Regalos Virtuales Gratuitos de Buena Suerte",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Recompensas otorgadas por tus avances. Actívalos gratuitamente sin costo alguno.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        items(gifts, key = { it.id }) { gift ->
            val isClaimed = purchasedGifts.any { it.id == gift.id }
            AnimatedGiftCard(
                gift = gift,
                isClaimed = isClaimed,
                onPurchaseGift = { onPurchaseGift(gift) }
            )
        }

        // 3. Sección: Jornadas de Bienestar Infantil Gratuitas
        item {
            Column {
                Text(
                    text = "Jornadas Sociales Comunitarias Gratuitas",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Impulsadas y financiadas por las tareas que realizas día a día en la plataforma.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        items(effectiveJornadas, key = { it.id }) { jornada ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AzulPlatinadoBorder, Color(0xFF475569)))),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = jornada.iconEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = jornada.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = jornada.description,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "❤️ Beneficio directo a ${jornada.childrenBenefited} niños vulnerables",
                            fontSize = 11.sp,
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Badge(containerColor = Color(0xFF10B981)) {
                        Text("100% GRATIS", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Sección: Aportación Voluntaria Libre (SOBRESALE con otro color y FRANJAS DE AMISTAD)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DonacionAmistadBg),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DonacionAmistadBorder, Color(0xFFF472B6)))),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_donation_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // FRANJAS DE AMISTAD DECORATIVAS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    ) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad1))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad2))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad3))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad4))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad5))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad6))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "❤️ Aportación Voluntaria Libre",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF472B6),
                            modifier = Modifier.weight(1f)
                        )
                        Badge(containerColor = Color(0xFFF59E0B)) {
                            Text("🤝 LAZOS DE AMISTAD", color = Color(0xFF0F172A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tu apoyo económico voluntario está destinado exclusivamente al soporte técnico, mantenimiento de servidores y el financiamiento para el desarrollo de futuras herramientas gratuitas dentro de esta plataforma. ¡Gracias por ayudarnos a mantener este entorno seguro!",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFFFCE7F3)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5.0, 10.0, 25.0).forEach { amt ->
                            OutlinedButton(
                                onClick = { onMakeDonation(amt) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD54F)),
                                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFF472B6)))),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+$${amt.toInt()} USD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customDonationInput,
                            onValueChange = { customDonationInput = it },
                            label = { Text("Otro Monto (USD)", color = Color(0xFFFCE7F3)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD54F),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                val amount = customDonationInput.toDoubleOrNull() ?: 10.0
                                onMakeDonation(amount)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("Donar", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Franja inferior de cierre de amistad
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    ) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad4))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad5))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad1))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad2))
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGiftCard(
    gift: VirtualGift,
    isClaimed: Boolean,
    onPurchaseGift: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GiftAnim_${gift.id}")

    // Animación continua grande de pulso y flotación
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200 + (gift.id.hashCode() % 300).coerceAtLeast(0), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GiftScale"
    )

    val wobbleAnim by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GiftWobble"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GiftGlow"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    AzulPlatinadoBorder,
                    if (isClaimed) Color(0xFF10B981) else Color(0xFF38BDF8).copy(alpha = glowPulse)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gift_card_${gift.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Figura animada grande con halo
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                // Halo de fondo animado
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(scaleAnim)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (isClaimed) Color(0xFF10B981).copy(alpha = glowPulse * 0.5f)
                                    else Color(0xFFFFD54F).copy(alpha = glowPulse * 0.45f),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                )

                // Figura grande animada (corazón, regalo, árbol, libro, escudo, copa, medalla, moneda)
                Text(
                    text = gift.iconEmoji,
                    fontSize = 38.sp,
                    modifier = Modifier
                        .scale(scaleAnim)
                        .rotate(wobbleAnim)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = gift.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Badge(containerColor = if (isClaimed) Color(0xFF059669) else Color(0xFF0284C7)) {
                        Text(
                            text = if (isClaimed) "DESBLOQUEADO" else "GRATIS",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = gift.description,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🌱 ${gift.socialImpact}",
                    fontSize = 11.sp,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onPurchaseGift,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isClaimed) Color(0xFF059669) else Color(0xFFFFD54F)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("buy_gift_btn_${gift.id}")
            ) {
                Text(
                    text = if (isClaimed) "✓ Activo" else "Reclamar",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isClaimed) Color.White else Color(0xFF0F172A)
                )
            }
        }
    }
}

