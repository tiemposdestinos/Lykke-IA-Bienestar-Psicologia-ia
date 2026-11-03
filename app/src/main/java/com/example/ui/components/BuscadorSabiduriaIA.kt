package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.MemoriaAprendizajeStore
import com.example.data.model.ContenidoMultilingue
import com.example.data.model.ElementoFilosofico
import com.example.data.model.GlobalNetworkRegistry
import com.example.data.model.GlobalService
import com.example.data.model.ServiceType
import com.example.data.model.bancoDeDatosCompleto
import com.example.data.model.buscarCoincidenciasFilosoficas
import com.example.data.model.buscarRespuestaFilosofica
import com.example.ui.theme.AzulPlatinadoBorder
import com.example.ui.theme.AzulPlatinadoCard

/**
 * Componente de Búsqueda Compacto y Universal.
 * Título limpio "Búsqueda", cuadro de entrada pequeño y directo,
 * reconocimiento de voz automático estilo Gemini / Alexa que habla de inmediato,
 * búsqueda de cualquier tema en internet (Google, Baidu, Yandex, Maps Satelital)
 * y botón para abrir toda la base de datos completa sin interrupciones.
 */
@Composable
fun BuscadorSabiduriaIA(
    languageCode: String = "es",
    modoConexion: ModoConexionIA,
    onCambiarModo: () -> Unit,
    onSeleccionarModo: ((ModoConexionIA) -> Unit)? = null,
    onVoiceSpeak: ((String) -> Unit)? = null,
    onStartVoiceRecognition: (((String) -> Unit) -> Unit)? = null,
    onNavigateToService: ((GlobalService) -> Unit)? = null,
    onOpenGlobalConnections: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val memoriaStore = remember { MemoriaAprendizajeStore.getInstance(context) }
    val frasesAprendidas by memoriaStore.ensenanzasFlow.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var mensajeFeedback by remember { mutableStateOf<String?>(null) }
    var resultadosVisibles by remember { mutableStateOf(false) }
    var estaEscuchandoVoz by remember { mutableStateOf(false) }
    var respuestaSintesisVoz by remember { mutableStateOf<String?>(null) }
    var mostrarModalVoz by remember { mutableStateOf(false) }
    var mostrarModalBaseCompleta by remember { mutableStateOf(false) }

    fun ejecutarBusquedaAutomatica(consulta: String) {
        val consultaLimpia = com.example.data.model.BuscadorFilosofico.extraerConsultaDeComando(consulta)
        textoBusqueda = consultaLimpia.ifBlank { consulta }
        resultadosVisibles = true

        val matchesFound = buscarCoincidenciasFilosoficas(
            inputUsuario = consulta,
            idiomaSeleccionado = languageCode,
            frasesAprendidas = frasesAprendidas,
            limite = 35
        )

        val qLower = consulta.lowercase().trim()
        val respuestaParaVoz: String = if (matchesFound.isNotEmpty()) {
            val mejor = matchesFound.first().second
            val nombreAutor = mejor.autorSignatario.ifBlank { mejor.autor }.ifBlank { "Sabiduría Lykke" }
            val sintesisGemini = when {
                qLower.contains("ansiedad") || qLower.contains("nervios") -> "Para la ansiedad, recuerda respirar profundo y anclarte al presente."
                qLower.contains("estres") || qLower.contains("estrés") -> "El estrés se disuelve cuando te enfocas únicamente en lo que puedes controlar hoy."
                qLower.contains("triste") || qLower.contains("depre") -> "Date permiso de sentir; las emociones difíciles también forjan tu nueva resiliencia."
                qLower.contains("k. ada") || qLower.contains("ada medina") -> "Aquí tienes la reflexión de K. Ada Medina:"
                qLower.contains("berganza") || qLower.contains("maynor") -> "Aquí tienes la reflexión de Maynor Berganza sobre reinvención y liderazgo:"
                qLower.contains("kierkegaard") -> "Kierkegaard nos recuerda el equilibrio entre lo ético y lo estético:"
                qLower.contains("rutina") || qLower.contains("plan") -> "Tu rutina recomendada: 3 minutos de respiración, escribir en tu diario y avanzar paso a paso."
                else -> "Encontré esta reflexión en nuestra base de datos para ti:"
            }
            "$sintesisGemini \"${mejor.texto}\" — $nombreAutor"
        } else {
            val fallback = buscarRespuestaFilosofica(consulta, languageCode, frasesAprendidas)
            val nombreAutor = fallback.autorSignatario.ifBlank { fallback.autor }.ifBlank { "Sabiduría Lykke" }
            "Sobre tu consulta '$consulta', como enseña $nombreAutor: ${fallback.texto}"
        }

        respuestaSintesisVoz = respuestaParaVoz
        onVoiceSpeak?.invoke(respuestaParaVoz)
        mensajeFeedback = "🎙️ I.A. Conciencia: respuesta lista y reproducida por voz."
    }

    // In-app SpeechRecognizer con escucha directa
    fun iniciarEscuchaDirecta() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            mostrarModalVoz = true
            return
        }

        try {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                val localeStr = when (languageCode) {
                    "en" -> "en-US"
                    "fr" -> "fr-FR"
                    "it" -> "it-IT"
                    "da" -> "da-DK"
                    "zh" -> "zh-CN"
                    "ru" -> "ru-RU"
                    else -> "es-ES"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeStr)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { estaEscuchandoVoz = true }
                override fun onBeginningOfSpeech() { estaEscuchandoVoz = true }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { estaEscuchandoVoz = false }
                override fun onError(error: Int) {
                    estaEscuchandoVoz = false
                    try { recognizer.destroy() } catch (_: Exception) {}
                    // Si ocurre error de reconocimiento en emulador, abrir diálogo interactivo
                    mostrarModalVoz = true
                }
                override fun onResults(results: Bundle?) {
                    estaEscuchandoVoz = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val resultadoVoz = matches[0].trim()
                        if (resultadoVoz.isNotBlank()) {
                            ejecutarBusquedaAutomatica(resultadoVoz)
                        }
                    }
                    try { recognizer.destroy() } catch (_: Exception) {}
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            recognizer.startListening(intent)
        } catch (_: Exception) {
            estaEscuchandoVoz = false
            mostrarModalVoz = true
        }
    }

    val launcherPermisoMic = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            iniciarEscuchaDirecta()
        } else {
            mostrarModalVoz = true
        }
    }

    fun presionarMicrofono() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            iniciarEscuchaDirecta()
        } else {
            launcherPermisoMic.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Coincidencias en tiempo real
    val coincidencias = remember(textoBusqueda, languageCode, frasesAprendidas) {
        if (textoBusqueda.trim().isNotBlank()) {
            buscarCoincidenciasFilosoficas(
                inputUsuario = textoBusqueda,
                idiomaSeleccionado = languageCode,
                frasesAprendidas = frasesAprendidas,
                limite = 35
            )
        } else {
            emptyList()
        }
    }

    LaunchedEffect(textoBusqueda) {
        resultadosVisibles = textoBusqueda.trim().isNotBlank()
    }

    // Modal de Entrada y Comandos por Voz Directos (estilo Alexa/Gemini)
    if (mostrarModalVoz) {
        AlertDialog(
            onDismissRequest = { mostrarModalVoz = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎙️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I.A. Conciencia (Voz y Órdenes)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Di cualquier palabra, tema o pregunta a la I.A. Conciencia (responderá con síntesis de voz en automático):",
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val consultasRapidas = listOf(
                        "K. Ada Medina",
                        "Maynor Berganza",
                        "Cómo calmar la ansiedad",
                        "Ético y estético en la religión",
                        "Michael Persinger y el cerebro",
                        "Mi rutina diaria",
                        "Walter Gilbert",
                        "Experiencias paranormales y cognitivas"
                    )

                    consultasRapidas.forEach { frase ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    mostrarModalVoz = false
                                    ejecutarBusquedaAutomatica(frase)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💬", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = frase,
                                    fontSize = 11.5.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarModalVoz = false }) {
                    Text("Cerrar", color = Color(0xFF38BDF8))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(AzulPlatinadoBorder, Color(0xFF38BDF8).copy(alpha = 0.4f)))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("buscador_sabiduria_card")
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Título Pequeño y Limpio: "Búsqueda"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔍",
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Búsqueda",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }

                // Selector Dual de Modos: Modo Local (Sin Internet) vs Con Internet
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Modo Local
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (modoConexion == ModoConexionIA.LOCAL_OFFLINE) Color(0xFF064E3B) else Color(0xFF1E293B),
                        border = BorderStroke(1.2.dp, if (modoConexion == ModoConexionIA.LOCAL_OFFLINE) Color(0xFF34D399) else Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                onSeleccionarModo?.invoke(ModoConexionIA.LOCAL_OFFLINE) ?: onCambiarModo()
                                onVoiceSpeak?.invoke("Modo Local activado. Búsqueda y base de datos 100% offline.")
                            }
                            .testTag("btn_buscador_modo_local")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🛡️", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Modo Local",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (modoConexion == ModoConexionIA.LOCAL_OFFLINE) Color(0xFF4ADE80) else Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Modo con Internet
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (modoConexion == ModoConexionIA.INTERNET_EXPANDED) Color(0xFF0C4A6E) else Color(0xFF1E293B),
                        border = BorderStroke(1.2.dp, if (modoConexion == ModoConexionIA.INTERNET_EXPANDED) Color(0xFF38BDF8) else Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                onSeleccionarModo?.invoke(ModoConexionIA.INTERNET_EXPANDED) ?: onCambiarModo()
                                onVoiceSpeak?.invoke("Modo con Internet activado. Conectado a servidores mundiales y satélite.")
                            }
                            .testTag("btn_buscador_modo_internet")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌐", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Con Internet",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (modoConexion == ModoConexionIA.INTERNET_EXPANDED) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                            )
                        }
                    }

                    if (onOpenGlobalConnections != null) {
                        IconButton(
                            onClick = onOpenGlobalConnections,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text(text = "⚡", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Indicador de Estado del Modo Seleccionado
            if (modoConexion == ModoConexionIA.LOCAL_OFFLINE) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF064E3B).copy(alpha = 0.5f),
                    border = BorderStroke(0.8.dp, Color(0xFF34D399).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🛡️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Modo Local: Buscando 100% offline en el núcleo interno (Sin internet).",
                            fontSize = 10.sp,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0C4A6E).copy(alpha = 0.5f),
                    border = BorderStroke(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🌐", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Modo Internet: Conectado a servidores mundiales y mapas satelitales.",
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de Búsqueda Pequeña y Directa
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_buscador_letras"),
                placeholder = {
                    Text(
                        text = "Escribe cualquier palabra o tema (ej: calma, Séneca, Medina, Varela...)",
                        fontSize = 11.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (textoBusqueda.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    textoBusqueda = ""
                                    resultadosVisibles = false
                                    respuestaSintesisVoz = null
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Botón de Micrófono para Ordenar y Hablar de Inmediato
                        IconButton(
                            onClick = { presionarMicrofono() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_mic_orden_ia")
                        ) {
                            Icon(
                                imageVector = if (estaEscuchandoVoz) Icons.Default.GraphicEq else Icons.Default.Mic,
                                contentDescription = "Dictar y Buscar por Voz",
                                tint = if (estaEscuchandoVoz) Color(0xFF4ADE80) else Color(0xFFFFD54F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (textoBusqueda.isNotBlank()) {
                        ejecutarBusquedaAutomatica(textoBusqueda)
                    }
                }),
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

            // Barra de Búsqueda según el Modo Activo (Modo Local vs Modo con Internet)
            if (textoBusqueda.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                if (modoConexion == ModoConexionIA.INTERNET_EXPANDED) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Google Search
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E3A8A),
                            border = BorderStroke(1.dp, Color(0xFF60A5FA)),
                            modifier = Modifier
                                .clickable {
                                    val s = GlobalNetworkRegistry.googleSearch
                                    onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                }
                                .testTag("btn_buscar_google_directo")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🌐", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Google: \"$textoBusqueda\"",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Google Maps Satélite
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF064E3B),
                            border = BorderStroke(1.dp, Color(0xFF34D399)),
                            modifier = Modifier
                                .clickable {
                                    val s = GlobalNetworkRegistry.googleMapsSat
                                    onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                }
                                .testTag("btn_buscar_maps_satelite_directo")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🛰️", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Maps Satélite: \"$textoBusqueda\"",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        // Baidu Search
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF7F1D1D),
                            border = BorderStroke(1.dp, Color(0xFFF87171)),
                            modifier = Modifier
                                .clickable {
                                    val s = GlobalNetworkRegistry.baiduSearch
                                    onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🇨🇳", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Baidu: \"$textoBusqueda\"",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Yandex Satélite
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4C1D95),
                            border = BorderStroke(1.dp, Color(0xFFA78BFA)),
                            modifier = Modifier
                                .clickable {
                                    val s = GlobalNetworkRegistry.yandexMapsSat
                                    onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🛰️", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Yandex Satélite",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE68A)
                                )
                            }
                        }

                        // DuckDuckGo
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF78350F),
                            border = BorderStroke(1.dp, Color(0xFFFBBF24)),
                            modifier = Modifier
                                .clickable {
                                    val s = GlobalNetworkRegistry.duckDuckGo
                                    onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🦆", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "DuckDuckGo",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    // En MODO LOCAL: Acceso directo a base local offline con opción de activar Internet
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF064E3B),
                            border = BorderStroke(1.dp, Color(0xFF34D399)),
                            modifier = Modifier.clickable { mostrarModalBaseCompleta = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📚", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Buscar en Base Local Completa",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0369A1),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier.clickable {
                                onSeleccionarModo?.invoke(ModoConexionIA.INTERNET_EXPANDED) ?: onCambiarModo()
                                onVoiceSpeak?.invoke("Modo con Internet activado. Ya puedes explorar la red mundial.")
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🌐", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Activar Modo con Internet para buscar afuera",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Temas sugeridos, acceso a Base de Datos Completa y Conexiones Globales
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón destacado: Base de Datos Completa (Sin Interrupciones)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF047857),
                    border = BorderStroke(1.dp, Color(0xFF34D399)),
                    modifier = Modifier
                        .clickable { mostrarModalBaseCompleta = true }
                        .testTag("btn_abrir_base_datos_completa")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📚", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Base de Datos Completa",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Botón de acceso directo al menú de Conexiones Globales
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F2644),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onOpenGlobalConnections?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🌐", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Conexiones Globales",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }

                // Autores y temas rápidos para probar
                listOf("K. Ada Medina", "Maynor Berganza", "Séneca", "Varela", "Oliver Sacks", "Kierkegaard").forEach { chip ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        modifier = Modifier.clickable {
                            textoBusqueda = chip
                            ejecutarBusquedaAutomatica(chip)
                        }
                    ) {
                        Text(
                            text = chip,
                            fontSize = 10.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Síntesis de Voz Activa estilo Alexa / Gemini
            if (respuestaSintesisVoz != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF062A38)),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Audio de Voz",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Respuesta I.A. Conciencia (Voz):",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = respuestaSintesisVoz ?: "",
                                fontSize = 11.5.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Resultados Animados de Búsqueda
            AnimatedVisibility(visible = resultadosVisibles) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (coincidencias.isNotEmpty()) "✨ ${coincidencias.size} reflexiones encontradas:" else "Explorar '$textoBusqueda' en internet y satélite:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (coincidencias.isNotEmpty()) Color(0xFF4ADE80) else Color(0xFFFFD54F)
                        )

                        Text(
                            text = "Ver todo",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { mostrarModalBaseCompleta = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (coincidencias.isEmpty() && textoBusqueda.isNotBlank()) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2038)),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🌐 No hay citas locales exactas para '$textoBusqueda'. Toca abajo para consultar directamente los servidores mundiales y satélite en vivo:",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val s = GlobalNetworkRegistry.googleSearch
                                            onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "🌐 Buscar en Google", fontSize = 11.sp, color = Color.White)
                                    }

                                    Button(
                                        onClick = {
                                            val s = GlobalNetworkRegistry.googleMapsSat
                                            onNavigateToService?.invoke(s.copy(targetUrl = s.buildUrlWithQuery(textoBusqueda)))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "🛰️ Ver Satélite", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    coincidencias.take(10).forEach { (_, contenido) ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, AzulPlatinadoBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "\"${contenido.texto}\"",
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val autorFinal = contenido.autorSignatario.ifBlank { contenido.autor }.ifBlank { "Sabiduría Lykke" }
                                    Text(
                                        text = "— $autorFinal",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("${contenido.texto} — $autorFinal"))
                                            },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copiar",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }

                                        if (onVoiceSpeak != null) {
                                            IconButton(
                                                onClick = {
                                                    onVoiceSpeak("${contenido.texto} — $autorFinal")
                                                },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.VolumeUp,
                                                    contentDescription = "Escuchar",
                                                    tint = Color(0xFFFFD54F),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Diálogo: Base de Datos Completa en un Solo Bloque Único Sin Interrupciones
    if (mostrarModalBaseCompleta) {
        DialogoBaseDeDatosCompleta(
            languageCode = languageCode,
            onDismiss = { mostrarModalBaseCompleta = false },
            onVoiceSpeak = onVoiceSpeak
        )
    }
}

/**
 * Diálogo de Base de Datos Completa en un Solo Bloque Único Sin Interrupciones.
 * Despliega todas las enseñanzas registradas (Michael Persinger, Francisco Varela, Oliver Sacks,
 * Kierkegaard, Mircea Eliade, Maynor Berganza, Eleanor Roosevelt, Wayne Dyer, Dalai Lama,
 * Nelson Mandela, Martin Luther King Jr., Mahatma Gandhi, Daniel Goleman, Maya Angelou,
 * Carl Jung, Walter Riso, Marshall Rosenberg, Virginia Satir, Thomas d'Ansembourg, etc.)
 * en una sola lista continua, con opción de copiar toda la base de datos íntegra en un clic.
 */
@Composable
fun DialogoBaseDeDatosCompleta(
    languageCode: String = "es",
    onDismiss: () -> Unit,
    onVoiceSpeak: ((String) -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current
    var filtroBusqueda by remember { mutableStateOf("") }
    var mensajeCopiado by remember { mutableStateOf<String?>(null) }

    val listaElementos = remember { bancoDeDatosCompleto }
    val elementosFiltrados = remember(filtroBusqueda, listaElementos) {
        if (filtroBusqueda.isBlank()) {
            listaElementos
        } else {
            val q = filtroBusqueda.lowercase().trim()
            listaElementos.filter { elem ->
                elem.autorOrigen.lowercase().contains(q) ||
                elem.tags.any { it.lowercase().contains(q) } ||
                elem.traducciones.values.any { it.texto.lowercase().contains(q) || it.titulo.lowercase().contains(q) }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070F1E)),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.65f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Encabezado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF047857).copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📚", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Base de Datos Completa",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "Un solo bloque único sin interrupciones • ${listaElementos.size} citas",
                                fontSize = 10.5.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón destacado: Copiar Toda la Base de Datos Íntegra en 1 Clic
                Button(
                    onClick = {
                        val textoCompleto = buildString {
                            appendLine("==========================================================")
                            appendLine("📚 BASE DE DATOS COMPLETA DE SABIDURÍA Y NEUROFILOSOFÍA LYKKE")
                            appendLine("Total de reflexiones y enseñanzas: ${listaElementos.size}")
                            appendLine("==========================================================\n")

                            listaElementos.forEachIndexed { idx, elem ->
                                val cont = elem.traducciones[languageCode] ?: elem.traducciones["es"] ?: elem.traducciones.values.firstOrNull()
                                val titulo = cont?.titulo ?: "Reflexión #${idx + 1}"
                                val texto = cont?.texto ?: ""
                                val autor = cont?.autorSignatario?.ifBlank { cont.autor } ?: elem.autorOrigen
                                appendLine("${idx + 1}. [$titulo]")
                                appendLine("\"$texto\"")
                                appendLine("— Autor: $autor")
                                appendLine("Tags: ${elem.tags.joinToString(", ")}")
                                appendLine("----------------------------------------------------------")
                            }
                        }
                        clipboardManager.setText(AnnotatedString(textoCompleto))
                        mensajeCopiado = "¡Toda la base de datos (${listaElementos.size} citas) fue copiada al portapapeles!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📋 Copiar Toda la Base de Datos Completa",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (mensajeCopiado != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = mensajeCopiado ?: "",
                        fontSize = 11.sp,
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de Búsqueda Rápida dentro del bloque
                OutlinedTextField(
                    value = filtroBusqueda,
                    onValueChange = { filtroBusqueda = it },
                    placeholder = { Text("Filtrar por autor o palabra clave...", fontSize = 11.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F1E36),
                        unfocusedContainerColor = Color(0xFF0B1728),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = AzulPlatinadoBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bloque Único Continuo Sin Interrupciones
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(elementosFiltrados, key = { it.id }) { elem ->
                        val cont = elem.traducciones[languageCode] ?: elem.traducciones["es"] ?: elem.traducciones.values.firstOrNull()
                        val autor = cont?.autorSignatario?.ifBlank { cont.autor } ?: elem.autorOrigen

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            border = BorderStroke(1.dp, AzulPlatinadoBorder.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (cont?.titulo?.isNotBlank() == true) {
                                    Text(
                                        text = cont.titulo,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                }

                                Text(
                                    text = "\"${cont?.texto ?: ""}\"",
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "— $autor",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("\"${cont?.texto}\" — $autor"))
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                                        }

                                        if (onVoiceSpeak != null) {
                                            IconButton(
                                                onClick = {
                                                    onVoiceSpeak("${cont?.texto} — $autor")
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar", tint = Color(0xFFFFD54F), modifier = Modifier.size(15.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
