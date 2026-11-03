package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GlobalNetworkRegistry
import com.example.data.model.GlobalService
import com.example.data.model.ServiceType
import com.example.ui.theme.AzulPlatinadoBorder
import com.example.ui.theme.AzulPlatinadoCard
import com.example.ui.theme.AzulPlatinadoCardHover

/**
 * Botón Dropdown Único para Conexiones Globales (Baidu, Yandex, Google, Gaode, etc.)
 * Encapsula todos los servicios internacionales en un solo botón desplegable.
 * Si el usuario no tiene membresía/clave, muestra únicamente la lista de servicios bloqueados
 * con aviso y botón destacado para 'Canjear Clave' (Redeem Key).
 */
@Composable
fun BotonConexionesGlobalesDropdown(
    esMiembro: Boolean,
    onNavigateToService: (GlobalService) -> Unit,
    onRedeemKey: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var desplegado by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    AzulPlatinadoBorder,
                    if (esMiembro) Color(0xFF38BDF8).copy(alpha = 0.6f) else Color(0xFFFFD54F).copy(alpha = 0.5f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("btn_dropdown_conexiones_globales")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Fila de encabezado interactiva del botón desplegable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { desplegado = !desplegado }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (esMiembro) Color(0xFF0284C7).copy(alpha = 0.25f)
                                else Color(0xFFFFD54F).copy(alpha = 0.20f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌐", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Conexiones Globales",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(
                                containerColor = if (esMiembro) Color(0xFF0F382E) else Color(0xFF3B1515)
                            ) {
                                Text(
                                    text = if (esMiembro) "DESBLOQUEADO" else "🔒 BLOQUEADO",
                                    color = if (esMiembro) Color(0xFF4ADE80) else Color(0xFFFF8A80),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Baidu, Yandex, Google, Gaode Maps, VK Cloud...",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = { desplegado = !desplegado },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (desplegado) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (desplegado) "Contraer" else "Desplegar",
                        tint = Color(0xFF38BDF8)
                    )
                }
            }

            // Contenido desplegable
            AnimatedVisibility(
                visible = desplegado,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider(color = AzulPlatinadoBorder.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!esMiembro) {
                        // Tarjeta de aviso de bloqueo y botón destacado de Canjear Clave
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF381414)),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFFF8A80),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Servicios Globales Bloqueados",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFFFCDD2)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Para conectarte a Baidu, Yandex, Google, Gaode Maps y más con procedencia oculta, debes adquirir tu clave y canjearla.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = Color(0xFFF1F5F9)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = onRedeemKey,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_redeem_key_dropdown")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Canjear Clave (Redeem Key)",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Lista de servicios disponibles tras canjear clave:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Lista visible de servicios bloqueados
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GlobalNetworkRegistry.services.forEach { service ->
                                TarjetaServicioBloqueado(
                                    service = service,
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "🔒 '${service.name}' está bloqueado. Canjea tu clave para acceder.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onRedeemKey()
                                    }
                                )
                            }
                        }
                    } else {
                        // Usuario Miembro con acceso desbloqueado
                        Text(
                            text = "✨ Acceso Total Activo • Selecciona un motor o mapa internacional:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4ADE80)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GlobalNetworkRegistry.services.forEach { service ->
                                TarjetaServicioConectividad(
                                    service = service,
                                    onClick = { onNavigateToService(service) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Menú de Conectividad Global Modal o Pantalla Completa
 */
@Composable
fun MenuConectividadGlobal(
    esMiembro: Boolean = false,
    onNavigateToService: (GlobalService) -> Unit,
    onRedeemKey: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var categoriaSeleccionada by remember { mutableStateOf<ServiceType?>(null) }
    var textoFiltro by remember { mutableStateOf("") }

    val serviciosFiltrados = remember(categoriaSeleccionada, textoFiltro) {
        GlobalNetworkRegistry.services.filter { servicio ->
            val coincideCategoria = categoriaSeleccionada == null || servicio.type == categoriaSeleccionada
            val coincideTexto = textoFiltro.isBlank() ||
                    servicio.name.contains(textoFiltro, ignoreCase = true) ||
                    servicio.region.contains(textoFiltro, ignoreCase = true) ||
                    servicio.descripcion.contains(textoFiltro, ignoreCase = true)
            coincideCategoria && coincideTexto
        }
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(AzulPlatinadoBorder, Color(0xFF38BDF8).copy(alpha = 0.5f)))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("menu_conectividad_global")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Cabecera del Menú
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌐", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Pasarela de Conectividad Global",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "Baidu, Yandex, Google & Motores con Banderas",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }

                Badge(
                    containerColor = if (esMiembro) Color(0xFF10B981) else Color(0xFF3B1515)
                ) {
                    Text(
                        text = if (esMiembro) "ONLINE" else "🔒 BLOQUEADO",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!esMiembro) {
                // Aviso de bloqueo prominente con botón para canjear clave
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF381414)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF8A80), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Servicios Internacionales Bloqueados",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFFFCDD2)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Para conectarte a Baidu, Yandex, Google, Gaode Maps y más con procedencia oculta, debes adquirir tu clave y canjearla.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFFF1F5F9)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onRedeemKey,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_modal_redeem_key")
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Canjear Clave (Redeem Key)", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Campo de Búsqueda Rápida de Servicio / Región
            OutlinedTextField(
                value = textoFiltro,
                onValueChange = { textoFiltro = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_filtro_conectividad"),
                placeholder = {
                    Text("Buscar motor, mapa o país (ej. China, Rusia, Baidu...)", fontSize = 11.5.sp, color = Color(0xFF94A3B8))
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (textoFiltro.isNotBlank()) {
                        IconButton(onClick = { textoFiltro = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0F1E36),
                    unfocusedContainerColor = Color(0xFF0B1728),
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = AzulPlatinadoBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Selector de Categoría (Chips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = categoriaSeleccionada == null,
                    onClick = { categoriaSeleccionada = null },
                    label = { Text("Todos (13)", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )

                FilterChip(
                    selected = categoriaSeleccionada == ServiceType.BUSQUEDA,
                    onClick = { categoriaSeleccionada = ServiceType.BUSQUEDA },
                    label = { Text("Búsqueda 🔍", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )

                FilterChip(
                    selected = categoriaSeleccionada == ServiceType.MAPAS,
                    onClick = { categoriaSeleccionada = ServiceType.MAPAS },
                    label = { Text("Mapas 🗺️", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF10B981),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )

                FilterChip(
                    selected = categoriaSeleccionada == ServiceType.NUBE,
                    onClick = { categoriaSeleccionada = ServiceType.NUBE },
                    label = { Text("Nube ☁️", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD97706),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Listado de Servicios Internacionales
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                serviciosFiltrados.forEach { service ->
                    if (esMiembro) {
                        TarjetaServicioConectividad(
                            service = service,
                            onClick = { onNavigateToService(service) }
                        )
                    } else {
                        TarjetaServicioBloqueado(
                            service = service,
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "🔒 '${service.name}' está bloqueado. Canjea tu clave para acceder.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onRedeemKey()
                            }
                        )
                    }
                }

                if (serviciosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron servicios para '$textoFiltro'",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badge de Privacidad de Procedencia Oculta
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0C2B24),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF4ADE80),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Procedencia protegida: Tu navegación se ejecuta dentro del WebView seguro de Lykke sin exponer identificadores de la app al exterior.",
                        fontSize = 10.5.sp,
                        color = Color(0xFFA7F3D0),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaServicioBloqueado(
    service: GlobalService,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF192231)),
        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("service_card_locked_${service.id}")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = service.flagEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${service.region}",
                        fontSize = 10.5.sp,
                        color = Color(0xFFFFD54F)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = service.descripcion,
                    fontSize = 10.5.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Badge(
                containerColor = Color(0xFF381515)
            ) {
                Text(
                    text = "🔒 BLOQUEADO",
                    color = Color(0xFFFF8A80),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TarjetaServicioConectividad(
    service: GlobalService,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCardHover),
        border = BorderStroke(1.dp, AzulPlatinadoBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("service_card_${service.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bandera con contenedor redondeado
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = service.flagEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.name,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${service.region}",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = service.descripcion,
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Badge de tipo y flecha de navegación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Badge(
                    containerColor = when (service.type) {
                        ServiceType.MAPAS -> Color(0xFF10B981)
                        ServiceType.BUSQUEDA -> Color(0xFF0284C7)
                        ServiceType.NUBE -> Color(0xFFD97706)
                    }
                ) {
                    Text(
                        text = service.type.name,
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Conectar",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
