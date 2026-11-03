package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.LykkeVoiceManager
import com.example.audio.TipoVozMascota
import com.example.data.PerfilUsuario
import com.example.data.UserProfileStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracionVozLykke(
    voiceManager: LykkeVoiceManager,
    profileStore: UserProfileStore,
    textoActual: String = "El universo es cambio; nuestra vida es lo que nuestros pensamientos hacen de ella.",
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val datosPerfil by profileStore.perfilFlow.collectAsState(initial = PerfilUsuario())

    var txtNombreUsuario by remember(datosPerfil.nombre) { mutableStateOf(datosPerfil.nombre) }
    var txtApodoConciencia by remember(datosPerfil.apodoConciencia) { mutableStateOf(datosPerfil.apodoConciencia) }

    var tipoVozSeleccionada by remember { mutableStateOf(TipoVozMascota.FEMENINA) }
    var estaGrabando by remember { mutableStateOf(false) }
    var mensajeEstadoGrabacion by remember { mutableStateOf("Mantén presionado para clonar tu voz") }
    var mostrarAlertaExplicativa by remember { mutableStateOf(false) }

    var tienePermisoMicrofono by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherPermisoMicrofono = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoMicrofono = concedido
        if (!concedido) {
            mostrarAlertaExplicativa = true
        }
    }

    val sugerenciasNombres = remember { UserProfileStore.SUGERENCIAS_NOMBRES }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Asesorías y Consultorías Lykke Psicología",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E2B)
                        )
                        Text(
                            text = "Voz & Entidad: ${datosPerfil.apodoConciencia}",
                            fontSize = 12.sp,
                            color = Color(0xFF4A6B48)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF2C3E2B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF4F6F0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configuración del Sistema de Voz",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E2B)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector Checklist de las 4 opciones de voz
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tipo de Voz de la Mascota / Conciencia",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF2C3E2B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TipoVozMascota.values().forEach { tipo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (tipoVozSeleccionada == tipo),
                                onClick = { tipoVozSeleccionada = tipo },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4A6B48))
                            )
                            Text(
                                text = tipo.nombreEs,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de Bautizo y Personalización de la Entidad
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Bautizar tu Entidad / Conciencia 🧠",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF2C3E2B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = txtNombreUsuario,
                        onValueChange = { txtNombreUsuario = it },
                        label = { Text("Tu nombre de usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = txtApodoConciencia,
                        onValueChange = { txtApodoConciencia = it },
                        label = { Text("¿Cómo quieres llamar a tu conciencia?") },
                        placeholder = { Text("Ej: Juan 2, Mamatina, Lykke") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Sugerencias iniciales:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sugerenciasNombres.forEach { sugerencia ->
                            val estaSeleccionado = txtApodoConciencia.trim().equals(sugerencia.trim(), ignoreCase = true)
                            FilterChip(
                                selected = estaSeleccionado,
                                onClick = { 
                                    txtApodoConciencia = sugerencia 
                                },
                                label = { 
                                    Text(
                                        text = sugerencia, 
                                        fontSize = 12.sp,
                                        fontWeight = if (estaSeleccionado) FontWeight.ExtraBold else FontWeight.Medium
                                    ) 
                                },
                                leadingIcon = if (estaSeleccionado) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Nombre seleccionado",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFFFFD54F)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2C3E2B),
                                    selectedLabelColor = Color(0xFFFFD54F),
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = Color(0xFF334155)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = estaSeleccionado,
                                    selectedBorderColor = Color(0xFFFFD54F),
                                    selectedBorderWidth = 2.dp,
                                    borderColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val nombreFinal = txtNombreUsuario.ifEmpty { "Usuario" }
                                val apodoFinal = txtApodoConciencia.ifEmpty { "Conciencia" }
                                profileStore.guardarConfiguracion(nombreFinal, apodoFinal)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6B48)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Vincular Entidad a Lykke", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN CLONACIÓN: Solo visible si se activa el modo Conciencia
            if (tipoVozSeleccionada == TipoVozMascota.CONCIENCIA_CLONADA) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EFE7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Entrenamiento de la Conciencia 🧠",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E2B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lee en voz alta: 'Yo controlo mi mente, soy mi propia fuerza'.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        IconButton(
                            onClick = {
                                if (!tienePermisoMicrofono) {
                                    launcherPermisoMicrofono.launch(Manifest.permission.RECORD_AUDIO)
                                    return@IconButton
                                }
                                if (!estaGrabando) {
                                    voiceManager.iniciarGrabacionDeConciencia()
                                    estaGrabando = true
                                    mensajeEstadoGrabacion = "Grabando... Habla ahora"
                                } else {
                                    voiceManager.detenerGrabacionDeConciencia()
                                    estaGrabando = false
                                    mensajeEstadoGrabacion = "¡Muestra guardada con éxito!"
                                }
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .background(if (estaGrabando) Color.Red else Color(0xFF4A6B48), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (estaGrabando) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Grabar Muestra",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(mensajeEstadoGrabacion, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bloque del Micrófono y Gestión de Permisos en vivo
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (tienePermisoMicrofono) Color(0xFFE8EFE7) else Color(0xFFFCE8E6)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (tienePermisoMicrofono) "Acceso al Micrófono Activado ✓" else "El Micrófono está bloqueado ⚠",
                        fontWeight = FontWeight.SemiBold,
                        color = if (tienePermisoMicrofono) Color(0xFF2C3E2B) else Color(0xFFB3261E)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tu entidad bautizada como '${datosPerfil.apodoConciencia}' necesita oír tu timbre de voz para responder a tu mente.",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!tienePermisoMicrofono) {
                        Button(
                            onClick = { launcherPermisoMicrofono.launch(Manifest.permission.RECORD_AUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                        ) {
                            Text("Dar permiso a ${datosPerfil.apodoConciencia}")
                        }
                    } else {
                        Text(
                            text = "Listo para clonar la voz de ${datosPerfil.nombreUsuario.ifEmpty { "Usuario" }} en el canal de ${datosPerfil.apodoConciencia}.",
                            fontSize = 12.sp,
                            color = Color(0xFF4A6B48),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón para escuchar el texto de bienestar actual hablado
            Button(
                onClick = { voiceManager.hablar(textoActual, tipoVozSeleccionada) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6B48)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escuchar Reflexión en Voz Alta")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Vista previa de interacción en pantalla principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "“Hola ${datosPerfil.nombreUsuario.ifEmpty { "a ti" }}, soy tu entidad '${datosPerfil.apodoConciencia}'. El universo es cambio; nuestra vida es lo que nuestros pensamientos hacen de ella.”",
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF333333),
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }

    if (mostrarAlertaExplicativa) {
        AlertDialog(
            onDismissRequest = { mostrarAlertaExplicativa = false },
            confirmButton = {
                TextButton(onClick = { mostrarAlertaExplicativa = false }) {
                    Text("Entendido", color = Color(0xFF4A6B48))
                }
            },
            title = { Text("Acceso Necesario") },
            text = { Text("Para que la app pueda clonar tu voz y simular la interacción con tu propia conciencia en el canal seleccionado, es obligatorio conceder el permiso de audio.") }
        )
    }
}
