package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.data.model.GlobalNetworkRegistry
import com.example.data.model.GlobalService

enum class ModoConexionIA(val id: String, val titulo: String, val descripcionCorta: String, val icono: String) {
    LOCAL_OFFLINE(
        id = "local",
        titulo = "Modo Local (Sin Internet)",
        descripcionCorta = "Base de datos recopilada 100% offline",
        icono = "🛡️"
    ),
    INTERNET_EXPANDED(
        id = "online",
        titulo = "Modo En Línea (Internet)",
        descripcionCorta = "Capacidad ampliada con conexión a red",
        icono = "🌐"
    )
}

/**
 * Diálogo interactivo que se presenta automáticamente al iniciar la app
 * permitiendo elegir entre la base de datos local cerrada (sin internet) o
 * conectarse a internet para ampliar la capacidad de la IA Conciencia.
 * Incluye un sub-menú internacional con motores y mapas con sus banderas nacionales
 * (Google, Baidu, Gaode, Yandex, 2GIS, VK Cloud, DuckDuckGo, Ecosia, Qwant, DeIC).
 */
@Composable
fun DialogoModoConexion(
    modoActual: ModoConexionIA,
    onSeleccionarModo: (ModoConexionIA) -> Unit,
    onAbrirServicioGlobal: ((GlobalService) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFFFFD54F)))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 8.dp)
                .testTag("dialogo_modo_conexion")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera Icono
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🧠", fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "I.A. Conciencia Lykke",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "¿Cómo deseas que opere tu I.A. en esta sesión?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Elige operar con la base de datos local cerrada o acceder a la pasarela global de búsqueda y mapas.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Opción 1: Modo Local (Sin Internet / Base Recopilada)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (modoActual == ModoConexionIA.LOCAL_OFFLINE) Color(0xFFE0F2FE) else Color.White
                    ),
                    border = BorderStroke(
                        if (modoActual == ModoConexionIA.LOCAL_OFFLINE) 2.dp else 1.dp,
                        if (modoActual == ModoConexionIA.LOCAL_OFFLINE) Color(0xFF0284C7) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeleccionarModo(ModoConexionIA.LOCAL_OFFLINE) }
                        .testTag("opcion_modo_local")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛡️", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Modo Local (Sin Internet)",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Badge(containerColor = Color(0xFF10B981)) {
                                    Text("OFFLINE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Base de datos recopilada y cerrada con 3,000+ reflexiones, citas históricas y lenguas mayas. 100% privado, veloz y sin consumo de datos.",
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opción 2: Modo En Línea (Conectarse a Internet)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (modoActual == ModoConexionIA.INTERNET_EXPANDED) Color(0xFFFEF3C7) else Color.White
                    ),
                    border = BorderStroke(
                        if (modoActual == ModoConexionIA.INTERNET_EXPANDED) 2.dp else 1.dp,
                        if (modoActual == ModoConexionIA.INTERNET_EXPANDED) Color(0xFFD97706) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeleccionarModo(ModoConexionIA.INTERNET_EXPANDED) }
                        .testTag("opcion_modo_internet")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD97706).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🌐", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Modo En Línea (Internet)",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Badge(containerColor = Color(0xFF0284C7)) {
                                        Text("EXPANDIDO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Capacidad ampliada con conexión a internet para consultas globales, mapas y motores de búsqueda internacionales.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // Submenú de Servicios Internacionales con Bandera
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Pasarela de Conexión Global (Toca para navegar):",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            GlobalNetworkRegistry.services.forEach { service ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSeleccionarModo(ModoConexionIA.INTERNET_EXPANDED)
                                            onAbrirServicioGlobal?.invoke(service)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = service.flagEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = service.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "${service.region} • ${service.descripcion}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B),
                                                maxLines = 1
                                            )
                                        }
                                        Badge(
                                            containerColor = if (service.type == com.example.data.model.ServiceType.MAPAS) Color(0xFF10B981) else Color(0xFF0284C7)
                                        ) {
                                            Text(service.type.name, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSeleccionarModo(ModoConexionIA.LOCAL_OFFLINE) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_elegir_local"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Text(text = "🛡️ Modo Local", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }

                    Button(
                        onClick = { onSeleccionarModo(ModoConexionIA.INTERNET_EXPANDED) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_elegir_online"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text(text = "🌐 Modo Red", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
