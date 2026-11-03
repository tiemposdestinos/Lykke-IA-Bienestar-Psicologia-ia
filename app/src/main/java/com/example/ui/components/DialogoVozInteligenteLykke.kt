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
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.audio.LykkeVoiceManager
import com.example.audio.TipoVozMascota
import com.example.data.MemoriaAprendizajeStore
import com.example.data.PerfilUsuario
import com.example.data.model.BuscadorFilosofico
import com.example.data.model.ContenidoMultilingue
import com.example.data.model.ElementoFilosofico
import com.example.data.model.buscarCoincidenciasFilosoficas
import com.example.data.model.buscarRespuestaFilosofica
import java.util.Locale

/**
 * Diálogo interactivo de la I.A. Conciencia Lykke.
 * Ofrece integración de voz robusta (Speech-to-Text), entrada por texto directa,
 * ejecución automática de consultas y búsqueda profunda en el banco de datos interno (3000+ citas y autores).
 * Responde hablando por voz de forma natural en español y mostrando el autor exacto y citas encontradas.
 */
@Composable
fun DialogoVozInteligenteLykke(
    perfil: PerfilUsuario,
    languageCode: String,
    voiceManager: LykkeVoiceManager,
    onDismiss: () -> Unit,
    onActionCompleted: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val memoriaStore = remember { MemoriaAprendizajeStore.getInstance(context) }
    val frasesAprendidas by memoriaStore.ensenanzasFlow.collectAsState()

    var estaEscuchando by remember { mutableStateOf(false) }
    var micDisponible by remember { mutableStateOf(true) }
    var textoComando by remember { mutableStateOf("") }
    var ultimoComandoEjecutado by remember { mutableStateOf("") }
    var respuestaIA by remember { mutableStateOf<ContenidoMultilingue?>(null) }
    var listaCoincidencias by remember { mutableStateOf<List<Pair<ElementoFilosofico, ContenidoMultilingue>>>(emptyList()) }
    var mostrarMasResultados by remember { mutableStateOf(false) }
    var mensajeFeedbackCopia by remember { mutableStateOf<String?>(null) }

    var mensajeEstado by remember {
        mutableStateOf("¿Qué quieres que hagamos hoy? ¿Qué quieres que busque, o estás listo para tu rutina? Presiona el micrófono o escribe tu orden.")
    }

    // Permisos de micrófono
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Función interna para ejecutar cualquier comando (por voz, texto o sugerencia)
    fun ejecutarComando(orden: String) {
        val ordenLimpia = orden.trim()
        if (ordenLimpia.isBlank()) return
        ultimoComandoEjecutado = ordenLimpia
        textoComando = ordenLimpia

        procesarComandoVoz(
            texto = ordenLimpia,
            languageCode = languageCode,
            perfil = perfil,
            memoriaStore = memoriaStore,
            frasesAprendidas = frasesAprendidas,
            voiceManager = voiceManager
        ) { principal, estado, coincidencias ->
            respuestaIA = principal
            mensajeEstado = estado
            listaCoincidencias = coincidencias
            onActionCompleted?.invoke(ordenLimpia)
        }
    }

    // Launcher del sistema para Speech-to-Text (Intent ACTION_RECOGNIZE_SPEECH estándar)
    val systemVoiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        estaEscuchando = false
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val vocalText = matches?.firstOrNull()?.trim()
            if (!vocalText.isNullOrBlank()) {
                ejecutarComando(vocalText)
            }
        }
    }

    // Activa la escucha de voz asegurando que la síntesis de voz no bloquee el micrófono
    fun activarEscuchaVoz() {
        voiceManager.detenerHabla()

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
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Te escucho. Dime qué autor, tema o rutina buscas...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            estaEscuchando = true
            systemVoiceLauncher.launch(intent)
        } catch (_: Exception) {
            // Fallback a SpeechRecognizer in-app si no hay Activity para el Intent
            iniciarEscuchaVoz(
                context = context,
                languageCode = languageCode,
                onResult = { texto ->
                    ejecutarComando(texto)
                },
                onListeningChanged = { escuchando ->
                    estaEscuchando = escuchando
                },
                onErrorOcurrido = {
                    estaEscuchando = false
                    micDisponible = false
                }
            )
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        tienePermiso = concedido
        if (concedido) {
            activarEscuchaVoz()
        }
    }

    // Saludo de bienvenida por voz al abrir el diálogo
    LaunchedEffect(Unit) {
        val saludoVocal = when (languageCode) {
            "en" -> "Hello ${perfil.nombre.ifBlank { "friend" }}. What would you like us to do today? Speak to me or type your order."
            "cak" -> "¡Alli ${perfil.nombre.ifBlank { "achib'il" }}! ¿Achike nawajo' nqab'an vacami?"
            else -> "¡Hola ${perfil.nombre.ifBlank { "amigo" }}! ¿Qué quieres que busquemos hoy o qué orden tienes para mí? Toca el micrófono o escribe."
        }
        voiceManager.hablar(saludoVocal, TipoVozMascota.FEMENINA, languageCode)
    }

    // Animación de pulso del aura del micrófono
    val infiniteTransition = rememberInfiniteTransition(label = "VoicePulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (estaEscuchando) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScalePulse"
    )

    Dialog(
        onDismissRequest = {
            voiceManager.detenerHabla()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF0)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .border(2.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(26.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra superior del diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧠", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = perfil.apodoConciencia.ifBlank { "Lykke Conciencia I.A." },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Búsqueda por Voz & Base de Datos Interna",
                                fontSize = 10.5.sp,
                                color = Color(0xFFB45309),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            voiceManager.detenerHabla()
                            onDismiss()
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón interactivo de Micrófono con Pulso
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scalePulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (estaEscuchando) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    Color(0xFF38BDF8).copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                        .clickable {
                            if (!tienePermiso) {
                                launcherPermiso.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                activarEscuchaVoz()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (estaEscuchando) Icons.Default.Mic else Icons.Default.GraphicEq,
                        contentDescription = "Hablar al Micrófono",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (estaEscuchando) "🎙️ Escuchando tu voz... Habla ahora" else "Toca el micrófono para hablar o escribe abajo tu orden",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (estaEscuchando) Color(0xFF047857) else Color(0xFFB45309),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo de entrada de texto para buscar o dar órdenes por escrito
                OutlinedTextField(
                    value = textoComando,
                    onValueChange = { textoComando = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_comando_voz_dialogo"),
                    placeholder = {
                        Text(
                            text = "Ej: Walter Gilbert, K. Adam Medina, rutina...",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFFD97706)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (textoComando.isNotBlank()) {
                                IconButton(onClick = { textoComando = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Borrar",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    if (textoComando.isNotBlank()) {
                                        ejecutarComando(textoComando)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Enviar",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (textoComando.isNotBlank()) {
                            ejecutarComando(textoComando)
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFF59E0B),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF0F172A)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón principal de Ejecutar Orden / Buscar
                Button(
                    onClick = {
                        if (textoComando.isNotBlank()) {
                            ejecutarComando(textoComando)
                        } else {
                            ejecutarComando("dame un consejo")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("btn_ejecutar_orden_ia")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (textoComando.isNotBlank()) "Ejecutar Consulta: \"${textoComando.take(20)}\"" else "Consultar Sabiduría a la I.A. 🚀",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // SI SE HA EJECUTADO UNA CONSULTA: Mostrar resultados reales primero
                if (respuestaIA != null) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Indicador de consulta ejecutada
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎯", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Consulta ejecutada: \"$ultimoComandoEjecutado\"",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tarjeta de Respuesta Principal de la I.A.
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFF38BDF8)))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "💬 Respuesta Principal:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Botón repetir por voz
                                    IconButton(
                                        onClick = {
                                            val t = "${respuestaIA?.texto} — ${respuestaIA?.autorSignatario?.ifBlank { respuestaIA?.autor }}"
                                            voiceManager.hablar(t, TipoVozMascota.FEMENINA, languageCode)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar", tint = Color(0xFF0284C7))
                                    }

                                    // Botón copiar
                                    IconButton(
                                        onClick = {
                                            val t = "\"${respuestaIA?.texto}\" — ${respuestaIA?.autorSignatario?.ifBlank { respuestaIA?.autor }}"
                                            clipboardManager.setText(AnnotatedString(t))
                                            mensajeFeedbackCopia = "¡Cita copiada al portapapeles!"
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = Color(0xFF64748B))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = mensajeEstado,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp,
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "\"${respuestaIA?.texto}\"",
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A),
                                fontStyle = FontStyle.Italic
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "— ${respuestaIA?.autorSignatario?.ifBlank { respuestaIA?.autor } ?: "Sabiduría Lykke"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (mensajeFeedbackCopia != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mensajeFeedbackCopia ?: "",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Otras citas encontradas para esta consulta
                    if (listaCoincidencias.size > 1) {
                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = { mostrarMasResultados = !mostrarMasResultados },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (mostrarMasResultados) "Ocultar otras citas (${listaCoincidencias.size - 1}) ▲" else "Ver otras ${listaCoincidencias.size - 1} citas encontradas ▼",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }

                        AnimatedVisibility(visible = mostrarMasResultados) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listaCoincidencias.drop(1).take(10).forEach { (_, contenido) ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = CardDefaults.outlinedCardBorder().copy(
                                            brush = Brush.horizontalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFF1F5F9)))
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "\"${contenido.texto}\"",
                                                fontSize = 12.sp,
                                                fontStyle = FontStyle.Italic,
                                                color = Color(0xFF1E293B)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "— ${contenido.autorSignatario.ifBlank { contenido.autor }}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFD97706)
                                                )

                                                IconButton(
                                                    onClick = {
                                                        val audioTexto = "${contenido.texto} — ${contenido.autorSignatario.ifBlank { contenido.autor }}"
                                                        voiceManager.hablar(audioTexto, TipoVozMascota.FEMENINA, languageCode)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar", tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fila de sugerencias y comandos rápidos
                Text(
                    text = if (respuestaIA != null) "Otras consultas que puedes probar:" else "Órdenes y autores populares (toca para ejecutar):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val comandosRapidos = listOf(
                        "Walter Gilbert" to "🧬 Walter Gilbert",
                        "K. Adam Medina" to "📜 K. Adam Medina",
                        "Diego Ruzzarin" to "🤔 Diego Ruzzarin",
                        "David J. Gross" to "🌌 David J. Gross",
                        "Carlos Blanco" to "🧠 Carlos Blanco",
                        "Dr. Raymond Moody" to "✨ Raymond Moody",
                        "Pérmico" to "🌋 Extinción Pérmico",
                        "rutina" to "📋 Mi Rutina",
                        "dame un consejo para la ansiedad" to "💡 Consejo",
                        "aprende: La gratitud transforma el corazón humano" to "🎓 Enseñar a la IA"
                    )

                    comandosRapidos.forEach { (cmd, etiqueta) ->
                        SuggestionChip(
                            onClick = {
                                textoComando = cmd
                                ejecutarComando(cmd)
                            },
                            label = { Text(etiqueta, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = Color(0xFFF59E0B).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Inicia la captura de voz usando SpeechRecognizer in-app con manejo de errores robusto.
 */
private fun iniciarEscuchaVoz(
    context: Context,
    languageCode: String,
    onResult: (String) -> Unit,
    onListeningChanged: (Boolean) -> Unit,
    onErrorOcurrido: () -> Unit = {}
) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        onListeningChanged(false)
        onErrorOcurrido()
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
            override fun onReadyForSpeech(params: Bundle?) { onListeningChanged(true) }
            override fun onBeginningOfSpeech() { onListeningChanged(true) }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { onListeningChanged(false) }
            override fun onError(error: Int) {
                onListeningChanged(false)
                onErrorOcurrido()
                try { recognizer.destroy() } catch (_: Exception) {}
            }
            override fun onResults(results: Bundle?) {
                onListeningChanged(false)
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0])
                }
                try { recognizer.destroy() } catch (_: Exception) {}
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    } catch (_: Exception) {
        onListeningChanged(false)
        onErrorOcurrido()
    }
}

/**
 * Procesa cualquier comando de voz o escrito: búsqueda, rutina, consejo o aprendizaje interactivo.
 */
private fun procesarComandoVoz(
    texto: String,
    languageCode: String,
    perfil: PerfilUsuario,
    memoriaStore: MemoriaAprendizajeStore,
    frasesAprendidas: List<ElementoFilosofico>,
    voiceManager: LykkeVoiceManager,
    onResultado: (ContenidoMultilingue?, String, List<Pair<ElementoFilosofico, ContenidoMultilingue>>) -> Unit
) {
    val comandoMinusculas = texto.lowercase().trim()

    // 1. Comando de Rutina
    if (comandoMinusculas.contains("rutina") || comandoMinusculas.contains("hacer hoy") || comandoMinusculas.contains("plan")) {
        val mensajeRutina = "Tu rutina diaria recomendada: Dedica ${perfil.tiempoReinvencionMinutos} minutos a reinventarte, haz 3 minutos de respiración 4-7-8 para calmar tu mente, y enfócate en tu jornada de ${perfil.jornadaPrincipal}."
        val respuesta = ContenidoMultilingue(
            titulo = "Tu Rutina Diaria de Crecimiento",
            texto = mensajeRutina,
            autorSignatario = perfil.apodoConciencia.ifBlank { "Lykke Conciencia" }
        )
        onResultado(respuesta, "Aquí tienes tu rutina de hoy:", emptyList())
        voiceManager.hablar(mensajeRutina, TipoVozMascota.FEMENINA, languageCode)
        return
    }

    // 1.1. Comando Anota / Recordar Tarea de Rutina
    if (comandoMinusculas.startsWith("anota") || comandoMinusculas.startsWith("recordar") || comandoMinusculas.startsWith("note")) {
        val tareaLimpia = texto.replaceFirst(Regex("(?i)^(anota|recordar|note)\\s*"), "").trim()
        if (tareaLimpia.isNotBlank()) {
            memoriaStore.aprenderNuevaFrase("Actividad registrada: $tareaLimpia", "Usuario", "Rutina Personal")
            val mensajeConfirmacion = "¡Qué gusto saludarte! He registrado en tu rutina: \"$tareaLimpia\"."
            val respuesta = ContenidoMultilingue(
                titulo = "Tarea Registrada en Rutina",
                texto = mensajeConfirmacion,
                autorSignatario = perfil.apodoConciencia.ifBlank { "Lykke Conciencia" }
            )
            onResultado(respuesta, "Actividad guardada en tu rutina:", emptyList())
            voiceManager.hablar(mensajeConfirmacion, TipoVozMascota.FEMENINA, languageCode)
            return
        }
    }

    // 2. Comando de Enseñar / Aprender
    if (comandoMinusculas.startsWith("aprende") || comandoMinusculas.startsWith("enseña") || comandoMinusculas.contains("recuerda esto") || comandoMinusculas.startsWith("guarda")) {
        val textoAprender = texto.replace(Regex("(?i)^(aprende|enseña|recuerda esto|guarda|aprender):?\\s*"), "")
        if (textoAprender.isNotBlank()) {
            val autorUsuario = "Tú (${perfil.nombre.ifBlank { "Usuario" }})"
            memoriaStore.aprenderNuevaFrase(textoAprender, autorUsuario, "Enseñanza Compartida")
            val respuesta = ContenidoMultilingue(
                titulo = "¡Nueva Sabiduría Guardada!",
                texto = textoAprender,
                autorSignatario = autorUsuario
            )
            val ack = "He aprendido y guardado esto en mi memoria permanente: $textoAprender"
            onResultado(respuesta, "¡Guardado exitosamente en mi memoria permanente! Lo recordaré siempre.", emptyList())
            voiceManager.hablar(ack, TipoVozMascota.FEMENINA, languageCode)
            return
        }
    }

    // 3. Consulta de búsqueda en el banco completo (3000+ autores y citas) y frases aprendidas
    val coincidencias = buscarCoincidenciasFilosoficas(
        inputUsuario = texto,
        idiomaSeleccionado = languageCode,
        frasesAprendidas = frasesAprendidas,
        limite = 20
    )

    if (coincidencias.isNotEmpty()) {
        val mejor = coincidencias.first().second
        val autorStr = mejor.autorSignatario.ifBlank { mejor.autor }
        val estado = if (coincidencias.size > 1) {
            "Encontré ${coincidencias.size} reflexiones sobre '$texto'. Escucha la principal:"
        } else {
            "Encontré esta reflexión en nuestra base de datos para ti:"
        }

        val textoParaHablar = "${mejor.texto} — $autorStr"
        onResultado(mejor, estado, coincidencias)
        voiceManager.hablar(textoParaHablar, TipoVozMascota.FEMENINA, languageCode)
    } else {
        // Fallback enriquecido
        val fallback = buscarRespuestaFilosofica(texto, languageCode, frasesAprendidas)
        val autorStr = fallback.autorSignatario.ifBlank { fallback.autor }
        val textoParaHablar = "${fallback.texto} — $autorStr"
        onResultado(fallback, "No encontré coincidencia exacta para '$texto', pero te comparto esta reflexión:", emptyList())
        voiceManager.hablar(textoParaHablar, TipoVozMascota.FEMENINA, languageCode)
    }
}
