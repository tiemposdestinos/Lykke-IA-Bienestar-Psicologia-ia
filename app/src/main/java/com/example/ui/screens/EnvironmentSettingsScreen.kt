package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnvironmentTheme
import com.example.data.model.TextColorChoice
import com.example.ui.components.DialogoBuildApkYGoogleServices

@Composable
fun EnvironmentSettingsScreen(
    currentEnvironment: EnvironmentTheme,
    currentTextColor: TextColorChoice,
    currentOverlayAlpha: Float,
    onSelectEnvironment: (EnvironmentTheme) -> Unit,
    onSelectTextColor: (TextColorChoice) -> Unit,
    onSetOverlayAlpha: (Float) -> Unit
) {
    var showBuildDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Personalización del Entorno Seguro",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTextColor.color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configura tu fondo terapéutico preferido y el color de las letras para una experiencia visual calmante y adaptada a ti.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = currentTextColor.color.copy(alpha = 0.85f)
                )
            }
        }

        // Live Preview Sample
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102737).copy(alpha = 0.90f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preview_sample_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Vista Previa de Lectura",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Entorno actual: ${currentEnvironment.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTextColor.color
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“En este espacio digital, cada palabra cuenta tu historia y cada respiración te devuelve la calma.”",
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 18.sp,
                        color = currentTextColor.color.copy(alpha = 0.90f)
                    )
                }
            }
        }

        // 1. Choice of Background Environment
        item {
            Text(
                text = "1. Elección de Fondo del Entorno Seguro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = currentTextColor.color
            )
        }

        val environments = listOf(
            EnvironmentTheme.CLINIC,
            EnvironmentTheme.PLAYFUL,
            EnvironmentTheme.NATURE,
            EnvironmentTheme.MINIMAL_ZEN
        )

        items(environments) { env ->
            val isSelected = currentEnvironment == env

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF004D40).copy(alpha = 0.85f) else Color(0xFF132330).copy(alpha = 0.85f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFF80CBC4) else Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectEnvironment(env) }
                    .testTag("env_option_${env.name}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (env.imageRes != null) {
                        Image(
                            painter = painterResource(id = env.imageRes),
                            contentDescription = env.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A3B4C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = env.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = env.subtitle,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = Color(0xFF80CBC4)
                        )
                    }
                }
            }
        }

        // 2. Choice of Text Colors
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "2. Elección de Color de las Letras",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = currentTextColor.color
            )
        }

        val textColors = listOf(
            TextColorChoice.PURE_WHITE,
            TextColorChoice.WARM_IVORY,
            TextColorChoice.CALM_TEAL,
            TextColorChoice.GOLDEN_SUN,
            TextColorChoice.SKY_BLUE,
            TextColorChoice.LAVENDER_MIST
        )

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                textColors.chunked(2).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowColors.forEach { choice ->
                            val isSelected = currentTextColor == choice
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF004D40) else Color(0xFF142432).copy(alpha = 0.85f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) choice.color else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectTextColor(choice) }
                                    .testTag("color_btn_${choice.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(choice.color)
                                            .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = choice.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = choice.color
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Background Darkening Overlay Slider
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2231).copy(alpha = 0.90f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Nivel de Contraste del Velo de Fondo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ajusta la opacidad oscura para que las letras resalten con total nitidez sobre la imagen de fondo.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(
                        value = currentOverlayAlpha,
                        onValueChange = onSetOverlayAlpha,
                        valueRange = 0.70f..0.95f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF26A69A),
                            activeTrackColor = Color(0xFF26A69A),
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("overlay_slider")
                    )
                    Text(
                        text = "Opacidad: ${(currentOverlayAlpha * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD54F)
                    )
                }
            }
        }

        // 4. Build APK, App Bundle & Google Services (Firebase)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF34D399).copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_settings_build_apk_google_services")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4. Build APK & Google Services",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF064E3B)
                        ) {
                            Text(
                                text = "ACTIVO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "google-services.json vinculado (Firebase Auth & Firestore). Consulta la ruta del menú 'Build ➔ Build App Bundle(s)/APK(s) ➔ Build APK(s)' y el APK compilado listo para descargar.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showBuildDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Estado del Build & Google Services", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showBuildDialog) {
        DialogoBuildApkYGoogleServices(
            onDismiss = { showBuildDialog = false }
        )
    }
}
