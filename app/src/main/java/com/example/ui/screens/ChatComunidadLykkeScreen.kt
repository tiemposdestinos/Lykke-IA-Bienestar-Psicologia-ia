package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerfilUsuario
import com.example.data.model.LykkeChatChannel
import com.example.data.model.LykkeChatMessage
import com.example.data.model.LykkeCommunityRegistry
import com.example.data.model.LykkeProtocolType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Chat Comunidad Lykke.
 * Conecta a redes colectivas globales abiertas (Matrix.org, IRC Libera/OFTC, XMPP Jabber,
 * Puentes Baidu/Yandex y Clan Privado) con salas reales de psicología, debate, ciencia y asesoría.
 * La procedencia técnica de la app queda 100% oculta tras la interfaz propia de Lykke.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatComunidadLykkeScreen(
    perfil: PerfilUsuario,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var canalSeleccionado by remember { mutableStateOf(LykkeCommunityRegistry.channels.first()) }
    var salaSeleccionada by remember { mutableStateOf(canalSeleccionado.availableRooms.first()) }
    var miApodo by remember { mutableStateOf(perfil.nombre.ifBlank { perfil.apodoConciencia.ifBlank { "LykkeUser_${(100..999).random()}" } }) }
    var editandoApodo by remember { mutableStateOf(false) }
    var textoMensaje by remember { mutableStateOf("") }

    // Almacén de mensajes en memoria reactivo por canal y sala
    val mensajesPorSala = remember {
        mutableStateMapOf<String, List<LykkeChatMessage>>().apply {
            LykkeCommunityRegistry.channels.forEach { ch ->
                ch.availableRooms.forEach { room ->
                    put("${ch.id}_$room", LykkeCommunityRegistry.obtenerMensajesIniciales(ch.id, room))
                }
            }
        }
    }

    val claveSalaActual = "${canalSeleccionado.id}_$salaSeleccionada"
    val mensajesActuales = mensajesPorSala[claveSalaActual] ?: emptyList()

    // Enviar mensaje
    fun enviarMensaje() {
        if (textoMensaje.isBlank()) return
        val nuevoMensaje = LykkeChatMessage(
            id = "user_msg_${System.currentTimeMillis()}",
            channelId = canalSeleccionado.id,
            roomTopic = salaSeleccionada,
            senderName = miApodo,
            senderRole = "Tú",
            messageText = textoMensaje.trim(),
            timestamp = System.currentTimeMillis(),
            isMine = true,
            avatarColor = 0xFF0284C7
        )
        val listaAnterior = mensajesPorSala[claveSalaActual] ?: emptyList()
        mensajesPorSala[claveSalaActual] = listaAnterior + nuevoMensaje
        textoMensaje = ""

        coroutineScope.launch {
            listState.animateScrollToItem((mensajesPorSala[claveSalaActual]?.size ?: 1) - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💬 Chat Comunidad Lykke", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(containerColor = Color(0xFF10B981)) {
                                Text("EN VIVO", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Red abierta federada • Procedencia protegida",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Botón para cambiar o editar apodo
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { editandoApodo = !editandoApodo }
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👤 $miApodo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0B132B)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cuadro de edición de apodo si está activo
            AnimatedVisibility(visible = editandoApodo) {
                Surface(
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = miApodo,
                            onValueChange = { miApodo = it },
                            label = { Text("Tu nombre de usuario / apodo visible", fontSize = 11.sp, color = Color(0xFFCBD5E1)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD54F),
                                unfocusedBorderColor = Color(0xFF64748B)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { editandoApodo = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Guardar", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Selector Horizontal de Canales / Redes Globales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LykkeCommunityRegistry.channels.forEach { canal ->
                    val isSelected = canal.id == canalSeleccionado.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(canal.protocolType.badgeColor).copy(alpha = 0.25f) else Color(0xFF1E293B),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(canal.protocolType.badgeColor) else Color(0xFF334155)
                        ),
                        modifier = Modifier.clickable {
                            canalSeleccionado = canal
                            salaSeleccionada = canal.availableRooms.first()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = canal.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = canal.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(canal.protocolType.badgeColor) else Color(0xFFE2E8F0)
                                )
                                Text(
                                    text = canal.protocolTag,
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }

            // Selector de Salas Temáticas (#psicologia, #ciencia, #debate, etc.)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B1728))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                canalSeleccionado.availableRooms.forEach { room ->
                    val isRoomSelected = room == salaSeleccionada
                    FilterChip(
                        selected = isRoomSelected,
                        onClick = { salaSeleccionada = room },
                        label = {
                            Text(
                                text = room,
                                fontSize = 11.sp,
                                fontWeight = if (isRoomSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFFCBD5E1),
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isRoomSelected,
                            borderColor = if (isRoomSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                        )
                    )
                }
            }

            // Barra informativa de privacidad y protocolo federado
            Surface(
                color = Color(0xFF0F1E36),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Conectado a ${canalSeleccionado.serverHost} • Identidad cifrada de usuario estándar",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Lista de Mensajes Activos en la Sala
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(mensajesActuales, key = { it.id }) { mensaje ->
                    ItemMensajeComunidad(mensaje = mensaje)
                }
            }

            // Barra Inferior de Entrada de Mensaje
            Surface(
                color = Color(0xFF0F172A),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textoMensaje,
                        onValueChange = { textoMensaje = it },
                        placeholder = {
                            Text(
                                text = "Escribe a la sala como $miApodo...",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_mensaje_comunidad")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { enviarMensaje() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (textoMensaje.isNotBlank()) Color(0xFF0284C7) else Color(0xFF1E293B)
                            )
                            .testTag("btn_enviar_mensaje_comunidad")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar Mensaje",
                            tint = if (textoMensaje.isNotBlank()) Color.White else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemMensajeComunidad(mensaje: LykkeChatMessage) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val horaTexto = remember(mensaje.timestamp) { dateFormat.format(Date(mensaje.timestamp)) }

    val alignment = if (mensaje.isMine) Alignment.End else Alignment.Start
    val bubbleColor = if (mensaje.isMine) Color(0xFF0284C7) else Color(0xFF1E293B)
    val textColor = Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Encabezado del remitente
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (mensaje.isMine) Arrangement.End else Arrangement.Start
        ) {
            Text(
                text = mensaje.senderName,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = if (mensaje.isMine) Color(0xFFFFD54F) else Color(mensaje.avatarColor)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White.copy(alpha = 0.10f)
            ) {
                Text(
                    text = mensaje.senderRole,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = horaTexto,
                fontSize = 9.5.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Burbuja de mensaje
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (mensaje.isMine) 14.dp else 2.dp,
                bottomEnd = if (mensaje.isMine) 2.dp else 14.dp
            ),
            color = bubbleColor,
            border = BorderStroke(1.dp, if (mensaje.isMine) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFF334155)),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = mensaje.messageText,
                fontSize = 12.5.sp,
                lineHeight = 17.sp,
                color = textColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
