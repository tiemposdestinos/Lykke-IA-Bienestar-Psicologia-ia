package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BancoDeCapsulas
import com.example.data.model.BancoDeTerapias
import com.example.data.model.LecturaFamilia
import com.example.data.model.LecturasSeed

/**
 * Diálogo interactivo para explorar las Lecturas de Familia, Paternidad,
 * Maternidad, Hermandad, Perdón y el Banco de Cápsulas y Terapias.
 */
@Composable
fun DialogoLecturasFamilia(
    onDismiss: () -> Unit
) {
    val lecturas = remember { LecturasSeed.obtenerLecturasIniciales() }
    val capsulas = remember { BancoDeCapsulas.todas }
    val terapias = remember { BancoDeTerapias.todas }

    var tabSeleccionada by remember { mutableIntStateOf(0) } // 0: Familia, 1: Cápsulas, 2: Terapias
    var lecturaActual by remember { mutableStateOf<LecturaFamilia?>(lecturas.firstOrNull()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
                border = BorderStroke(
                    1.2.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFF38BDF8), Color(0xFF1E3A8A), Color(0xFFFFD54F))
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .fillMaxHeight(0.85f)
                    .testTag("dialogo_lecturas_familia")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Cabecera con botón de cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Lecturas",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sabiduría y Lazos Sagrados",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pestañas superiores
                    TabRow(
                        selectedTabIndex = tabSeleccionada,
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color(0xFF38BDF8),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = tabSeleccionada == 0,
                            onClick = { tabSeleccionada = 0 },
                            text = { Text("Familia y Perdón", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = tabSeleccionada == 1,
                            onClick = { tabSeleccionada = 1 },
                            text = { Text("Cápsulas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = tabSeleccionada == 2,
                            onClick = { tabSeleccionada = 2 },
                            text = { Text("Terapias", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contenido según pestaña
                    when (tabSeleccionada) {
                        0 -> {
                            // Selector de lecturas de familia
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(lecturas) { lectura ->
                                    val isSelected = lectura == lecturaActual
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Color(0xFF1E40AF) else Color(0xFF1E293B),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFFFFD54F) else Color(0xFF334155)
                                        ),
                                        modifier = Modifier.clickable { lecturaActual = lectura }
                                    ) {
                                        Text(
                                            text = when (lectura.categoria) {
                                                "FAMILIA_PERDON" -> "❤️ El Perdón"
                                                "ROL_PADRE" -> "🛡️ Paternidad"
                                                "ROL_MADRE" -> "🌸 Maternidad"
                                                "HERMANDAD" -> "🤝 Hermandad"
                                                else -> lectura.titulo
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            lecturaActual?.let { lectura ->
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(Color(0xFF0A1424), RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    item {
                                        Text(
                                            text = lectura.titulo,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = lectura.contenido,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(capsulas) { capsula ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "💡 ${capsula.categoria}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF38BDF8)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = capsula.titulo,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD54F)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = capsula.contenido,
                                                fontSize = 12.5.sp,
                                                lineHeight = 18.sp,
                                                color = Color(0xFFE2E8F0)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(terapias) { terapia ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                        border = BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.4f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    tint = Color(0xFF4ADE80),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = terapia.titulo,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF4ADE80)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = terapia.pasoAPaso,
                                                fontSize = 12.5.sp,
                                                lineHeight = 18.sp,
                                                color = Color(0xFFE2E8F0)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Cerrar y Regresar", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
