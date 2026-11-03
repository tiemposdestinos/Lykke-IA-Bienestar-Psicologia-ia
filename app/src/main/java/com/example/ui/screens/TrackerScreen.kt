package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemoriaAprendizajeStore
import com.example.data.ModoTracker
import com.example.data.PerfilUsuario
import com.example.data.model.FraseEstoica
import com.example.data.model.TextColorChoice
import com.example.data.model.buscarRespuestaFilosofica
import com.example.data.model.listaDeFrases
import com.example.ui.NavigationScreen
import com.example.ui.components.LykkeSynapseAnimation
import com.example.ui.components.MascotaLykke
import com.example.ui.theme.AzulPlatinadoBg
import com.example.ui.theme.AzulPlatinadoBorder
import com.example.ui.theme.AzulPlatinadoCard
import kotlinx.coroutines.launch

@Composable
fun TrackerScreen(
    sessionCount: Int,
    totalXp: Int,
    textColor: TextColorChoice,
    perfil: PerfilUsuario = PerfilUsuario(),
    onVoiceSpeak: ((String) -> Unit)? = null,
    onAddXp: (Int, String) -> Unit,
    onNavigate: (NavigationScreen) -> Unit,
    onStartVoiceInput: ((String) -> Unit) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modoTracker = remember { ModoTracker(context) }
    val memoriaStore = remember { MemoriaAprendizajeStore.getInstance(context) }
    val frasesAprendidas by memoriaStore.ensenanzasFlow.collectAsState()
    val rachaActual by modoTracker.rachaActualFlow.collectAsState(initial = 1)
    val maximaRacha by modoTracker.maximaRachaFlow.collectAsState(initial = 1)

    // Al abrir el tracker, verifica si ya pasó más de un día sin actividad para actualizar racha
    LaunchedEffect(Unit) {
        modoTracker.verificarSiRachaSeRompio()
    }

    // Estado de filtro de enfoque de la afirmación: "TODOS", "HACER", "SER"
    var selectedEnfoque by remember { mutableStateOf("TODOS") }
    var currentFraseIndex by remember { mutableIntStateOf(0) }

    // Estado para búsqueda inteligente por audio y texto
    var busquedaInput by remember { mutableStateOf("") }
    var idiomaSeleccionado by remember { mutableStateOf("es") }

    val filteredFrases = remember(selectedEnfoque) {
        if (selectedEnfoque == "TODOS") listaDeFrases
        else listaDeFrases.filter { it.enfoque.equals(selectedEnfoque, ignoreCase = true) }
    }

    val currentFrase: FraseEstoica = remember(filteredFrases, currentFraseIndex) {
        if (filteredFrases.isNotEmpty()) {
            filteredFrases[currentFraseIndex % filteredFrases.size]
        } else {
            listaDeFrases.first()
        }
    }

    // Estado del estado de ánimo diario
    var selectedMood by remember { mutableStateOf<String?>("😌 En Paz") }
    var energyLevel by remember { mutableFloatStateOf(7f) }
    var gratitudeText by remember { mutableStateOf("") }
    var isGratitudeSaved by remember { mutableStateOf(false) }

    // Hábitos completados hoy
    var habitBreath by remember { mutableStateOf(false) }
    var habitDiary by remember { mutableStateOf(false) }
    var habitMusic by remember { mutableStateOf(true) }
    var habitWater by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tarjeta Hero del Radar Emocional (Vibe Check) con Animación de Sinapsis y Semillas
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(AzulPlatinadoBorder, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tracker_hero_banner")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RADAR EMOCIONAL (VIBE CHECK) 🧭",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp,
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Check-in Psicológico & Bienestar Lykke",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Monitorea tu evolución mental, fortalece tus hábitos y recibe tu afirmación diaria.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        // Animación de la sinapsis y las 2 semillas (verde y amarilla)
                        LykkeSynapseAnimation(
                            size = 72.dp,
                            showAura = false
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$sessionCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                            Text(text = "Sesiones", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFF334155))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$totalXp XP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                            Text(text = "Bienestar", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFF334155))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$rachaActual Días",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(text = "Racha Activa", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }

        // Banner de Límite de Día Superado & Ciclo de Hábitos
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2B24)),
                border = BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.65f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tracker_day_limit_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🌅", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Límite de Día: Nuevo Día Activo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "El límite de día ha pasado. Cada nuevo día renueva tus metas y te permite sumar un nuevo eslabón a tu racha de hábitos.",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // Mascota Lykke con Animación que salta al completar hábitos o hablar
        item {
            MascotaLykke(
                perfil = perfil,
                languageCode = idiomaSeleccionado,
                isTaskCompletedTrigger = habitBreath || habitDiary || habitMusic || habitWater,
                isVoiceInteractingTrigger = busquedaInput.isNotBlank(),
                onVoiceSpeak = onVoiceSpeak,
                onOpenVoiceConfig = { onNavigate(NavigationScreen.VOICE_CONFIG) }
            )
        }

        // 2. Afirmación Diaria (Daily Affirmation) con Selector HACER / SER
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFD54F).copy(alpha = 0.6f), AzulPlatinadoBorder)
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_affirmation_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Afirmación Diaria",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        // Filtro de Enfoque: HACER / SER
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("TODOS", "HACER", "SER").forEach { enfoque ->
                                val isSelected = selectedEnfoque == enfoque
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color(0xFFFFD54F) else Color(0xFF0F172A)
                                        )
                                        .clickable {
                                            selectedEnfoque = enfoque
                                            currentFraseIndex = 0
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = enfoque,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Caja con la frase estoica activa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "\"${currentFrase.texto}\"",
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "— ${currentFrase.autor}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                                Badge(
                                    containerColor = if (currentFrase.enfoque == "HACER") Color(0xFF0284C7) else Color(0xFF7C3AED)
                                ) {
                                    Text(
                                        text = "ENFOQUE: ${currentFrase.enfoque}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón para cambiar afirmación
                    OutlinedButton(
                        onClick = {
                            currentFraseIndex++
                            onAddXp(5, "Reflexión con afirmación diaria")
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFD54F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Nueva Afirmación de Inspiración", fontSize = 12.sp, color = Color(0xFFFFD54F))
                    }
                }
            }
        }

        // ==========================================
        // BÚSQUEDA INTELIGENTE: AUDIO (SPEECH-TO-TEXT) & LECTURA DE TEXTO
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(AzulPlatinadoBorder, Color(0xFF38BDF8).copy(alpha = 0.55f))
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_smart_search_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎙️", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Búsqueda por Voz & Lectura",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "La app escucha tu voz o lee lo que escribes",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Botón de micrófono (Speech-to-Text)
                        Button(
                            onClick = {
                                onStartVoiceInput { spokenText ->
                                    if (spokenText.isNotBlank()) {
                                        busquedaInput = spokenText
                                        onAddXp(15, "Consulta por audio/voz en banco filosófico")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Escuchar audio", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hablar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Campo para leer lo que escribe el usuario
                    OutlinedTextField(
                        value = busquedaInput,
                        onValueChange = { busquedaInput = it },
                        placeholder = {
                            Text(
                                "Escribe o habla: ej. continuar, enfermedad, estrés, ataraxia...",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        trailingIcon = {
                            if (busquedaInput.isNotBlank()) {
                                IconButton(onClick = { busquedaInput = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = Color(0xFF94A3B8))
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector de Idiomas (Internacionales y Mayas Kaqchiquel / K'iche')
                    Text(
                        text = "Idioma de Traducción & Sabiduría:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val idiomas = listOf(
                        "es" to "Español 🇪🇸",
                        "en" to "English 🇺🇸",
                        "da" to "Danés 🇩🇰",
                        "fr" to "Français 🇫🇷",
                        "it" to "Italiano 🇮🇹",
                        "zh" to "中文 🇨🇳",
                        "ru" to "Русский 🇷🇺",
                        "cak" to "🏛️ Kaqchiquel",
                        "quc" to "🌽 Quiché",
                        "kek" to "🌿 Q'eqchi'",
                        "gar" to "Garífuna",
                        "xin" to "Xinka"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        idiomas.forEach { (codigo, label) ->
                            val isSelected = idiomaSeleccionado == codigo
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) Color(0xFF38BDF8) else Color(0xFF0F172A)
                                    )
                                    .clickable { idiomaSeleccionado = codigo }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onNavigate(NavigationScreen.LANGUAGES) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD54F)),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Abrir Pantalla con Botón Languages y Checklist Completo", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ==========================================
                    // COMPONENTE: TARJETA FILOSÓFICA (CARD)
                    // ==========================================
                    val respuesta = remember(busquedaInput, idiomaSeleccionado, frasesAprendidas) {
                        if (busquedaInput.isNotBlank()) {
                            val tag = com.example.data.model.MotorWellnessLocal.clasificarEntrada(busquedaInput)
                            if (tag != null) {
                                com.example.data.model.MotorWellnessLocal.obtenerContenidoUI(tag, idiomaSeleccionado)
                            } else {
                                buscarRespuestaFilosofica(busquedaInput, idiomaSeleccionado, frasesAprendidas)
                            }
                        } else {
                            buscarRespuestaFilosofica("continuar", idiomaSeleccionado, frasesAprendidas)
                        }
                    }

                    val textoMostrar = respuesta?.texto
                        ?: "Debes continuar en la vida, debes de reinventarte."
                    val autorMostrar = if (!respuesta?.autorSignatario.isNullOrBlank()) respuesta?.autorSignatario
                        else if (!respuesta?.autor.isNullOrBlank()) respuesta?.autor
                        else "Estoicismo y Bienestar"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "“",
                                fontSize = 60.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF8FA88B),
                                modifier = Modifier.height(40.dp)
                            )

                            Text(
                                text = textoMostrar,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF333333),
                                lineHeight = 26.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "— $autorMostrar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }
        }

        // 3. Registro de Estado de Ánimo & Nivel de Energía
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AzulPlatinadoBorder, Color(0xFF38BDF8).copy(alpha = 0.3f)))),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mood_tracker_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "¿Cómo te sientes en este instante?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val moods = listOf("😄 Radiante", "😌 En Paz", "😐 Neutro", "😔 Agobiado", "😣 Ansioso")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        moods.forEach { mood ->
                            val isSelected = selectedMood == mood
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF0F172A))
                                    .clickable {
                                        selectedMood = mood
                                        onAddXp(10, "Registro de estado de ánimo")
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mood.split(" ")[0],
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Nivel de Energía Mental: ${energyLevel.toInt()} / 10",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Slider(
                        value = energyLevel,
                        onValueChange = { energyLevel = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF38BDF8),
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }

        // 4. Hábitos de Autocuidado Diario (Checklist de Bienestar)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AzulPlatinadoBorder, Color(0xFF4ADE80).copy(alpha = 0.3f)))),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habits_checklist_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Hábitos de Autocuidado Diario",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ADE80)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    HabitCheckItem("🌬️ Ejercicio de Respiración 4-7-8", habitBreath) {
                        habitBreath = !habitBreath
                        if (habitBreath) {
                            onAddXp(20, "Hábito de respiración cumplido")
                            coroutineScope.launch { modoTracker.registrarActividadCompletada() }
                        }
                    }
                    HabitCheckItem("📖 Anotación en Diario Emocional Seguro", habitDiary) {
                        habitDiary = !habitDiary
                        if (habitDiary) {
                            onAddXp(20, "Hábito de diario cumplido")
                            coroutineScope.launch { modoTracker.registrarActividadCompletada() }
                        }
                    }
                    HabitCheckItem("🎧 10 Minutos de Música Relajante de Fondo", habitMusic) {
                        habitMusic = !habitMusic
                        if (habitMusic) {
                            onAddXp(15, "Hábito de música relajante")
                            coroutineScope.launch { modoTracker.registrarActividadCompletada() }
                        }
                    }
                    HabitCheckItem("💧 Pausa Consciente e Hidratación", habitWater) {
                        habitWater = !habitWater
                        if (habitWater) {
                            onAddXp(10, "Hábito de hidratación")
                            coroutineScope.launch { modoTracker.registrarActividadCompletada() }
                        }
                    }
                }
            }
        }

        // 5. Espacio de Gratitud Rápida
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AzulPlatinadoBorder, Color(0xFFFFD54F).copy(alpha = 0.3f)))),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_gratitude_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "🌱 Micro-Gratitud del Día",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Escribe una cosa sencilla por la que estés agradecido/a hoy.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = gratitudeText,
                        onValueChange = {
                            gratitudeText = it
                            isGratitudeSaved = false
                        },
                        placeholder = { Text("Ej. El café de la mañana, un momento de silencio...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD54F),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (gratitudeText.isNotBlank()) {
                                isGratitudeSaved = true
                                onAddXp(25, "Registro de gratitud en Modo Tracker")
                            }
                        },
                        enabled = gratitudeText.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isGratitudeSaved) "✓ Gratitud Guardada (+25 XP)" else "Guardar Gratitud",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitCheckItem(
    title: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF4ADE80),
                uncheckedColor = Color(0xFF64748B)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            color = if (isChecked) Color.White else Color(0xFFCBD5E1),
            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
