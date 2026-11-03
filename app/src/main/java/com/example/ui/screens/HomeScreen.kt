package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ModuloAccessManager
import com.example.data.PerfilUsuario
import com.example.data.model.CatalogoMelodiasGlobales
import com.example.data.model.MelodiaPais
import com.example.data.model.SelfCareReminder
import com.example.data.model.UserProfile
import com.example.audio.LykkeVoiceManager
import com.example.data.model.TextColorChoice
import com.example.ui.NavigationScreen
import com.example.ui.activities.CodigoActivacionActivity
import com.example.ui.components.DialogoBuildApkYGoogleServices
import com.example.ui.components.DialogoLecturasFamilia
import com.example.ui.components.DialogoRegaloAmorPropio
import com.example.ui.components.DialogoVozInteligenteLykke
import com.example.ui.components.LykkeSynapseAnimation
import com.example.ui.components.MascotaLykke
import com.example.ui.theme.*
import com.example.data.PreferenciasPersistentes

@Composable
fun HomeScreen(
    sessionCount: Int,
    userLevel: Int,
    userLevelTitle: String,
    userXp: Int,
    isMusicPlaying: Boolean,
    melodiaActual: MelodiaPais = CatalogoMelodiasGlobales[0],
    musicVolume: Float = 0.6f,
    onToggleMusic: () -> Unit,
    onSelectMelodia: (MelodiaPais) -> Unit = {},
    onNextMelodia: () -> Unit = {},
    onPrevMelodia: () -> Unit = {},
    onSubirVolumen: () -> Unit = {},
    onBajarVolumen: () -> Unit = {},
    activeReminder: SelfCareReminder?,
    textColor: TextColorChoice,
    languageCode: String = "es",
    perfil: PerfilUsuario = PerfilUsuario(),
    userProfile: UserProfile? = null,
    modoConexion: com.example.ui.components.ModoConexionIA = com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE,
    onCambiarModo: () -> Unit = {},
    onSeleccionarModoConexion: ((com.example.ui.components.ModoConexionIA) -> Unit)? = null,
    onVoiceSpeak: ((String) -> Unit)? = null,
    onStartVoiceRecognition: (((String) -> Unit) -> Unit)? = null,
    onNavigateToService: ((com.example.data.model.GlobalService) -> Unit)? = null,
    onNavigate: (NavigationScreen) -> Unit,
    onStartBreathing: () -> Unit
) {
    val context = LocalContext.current
    val stringsUI = remember(languageCode) { com.example.data.model.Localizer.get(languageCode) }
    val prefs = remember { context.getSharedPreferences(ModuloAccessManager.PREFS_NAME, Context.MODE_PRIVATE) }
    var esMiembro by remember { mutableStateOf(prefs.getBoolean(ModuloAccessManager.KEY_MIEMBRO_CLUB, false)) }
    var usosGratis by remember { mutableStateOf(prefs.getInt(ModuloAccessManager.KEY_TOTAL_USOS, 0)) }
    var showClubConsultoriaDialog by remember { mutableStateOf(false) }
    var nuevoDiaDetectado by remember { mutableStateOf(false) }
    var mostrarDialogoVozGlobal by remember { mutableStateOf(false) }
    var mostrarSubmenuConexiones by remember { mutableStateOf(false) }
    var mostrarCatalogoMelodias by remember { mutableStateOf(false) }
    var mostrarDialogoBaseCompletaHome by remember { mutableStateOf(false) }
    var mostrarDialogoRegaloAmorPropio by remember { mutableStateOf(false) }
    var mostrarDialogoLecturasFamilia by remember { mutableStateOf(false) }
    var mostrarDialogoBuildApk by remember { mutableStateOf(false) }
    var climaSensacionTermica by remember { mutableStateOf<String?>(null) }
    val persistentes = remember { PreferenciasPersistentes.getInstance(context) }
    var totalRegalosPersistentes by remember { mutableIntStateOf(persistentes.contadorRegalos) }
    val voiceManager = remember { LykkeVoiceManager(context) }
    val networkMonitor = remember { com.example.data.network.LykkeNetworkMonitor(context) }
    val tieneInternet by networkMonitor.estaConectado.collectAsState(initial = true)

    val refreshAccessState = {
        esMiembro = prefs.getBoolean(ModuloAccessManager.KEY_MIEMBRO_CLUB, false)
        usosGratis = prefs.getInt(ModuloAccessManager.KEY_TOTAL_USOS, 0)
    }

    // Refrescar estado de acceso y comprobar si ya pasó el límite de día (medianoche)
    LaunchedEffect(Unit) {
        val seReseteo = ModuloAccessManager.verificarYResetearLimiteDiario(context)
        if (seReseteo) {
            nuevoDiaDetectado = true
        }
        refreshAccessState()
        try {
            val clima = com.example.data.network.ClimaPorIpService.obtenerSensacionTermica()
            climaSensacionTermica = clima
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de Alerta de Conexión si no hay red
        item {
            com.example.ui.components.LykkeConexionStatusBanner(tieneInternet = tieneInternet)
        }

        // Banner de Estado de Autenticación con Google y Firebase Firestore
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (userProfile != null) Color(0xFF0D2820) else Color(0xFF102838)
                ),
                border = BorderStroke(
                    1.dp,
                    if (userProfile != null) Color(0xFF4ADE80).copy(alpha = 0.6f) else Color(0xFF80CBC4).copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(NavigationScreen.AUTH) }
                    .testTag("home_auth_status_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (userProfile != null) Color(0xFF00796B) else Color(0xFF004D40))
                            .border(1.5.dp, if (userProfile != null) Color(0xFF81C784) else Color(0xFF80CBC4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfile != null) {
                            Text(
                                text = userProfile.displayName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF80CBC4),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (userProfile != null) "Cuenta Sincronizada" else "Iniciar Sesión con Google",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userProfile != null) Color(0xFFE8F5E9) else Color.White
                            )
                            if (userProfile != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (userProfile != null)
                                "${userProfile.displayName} • Respaldo activo en Firestore"
                            else
                                "Sincroniza tu Diario Emocional y sesiones • Sin pedir edad ni género",
                            fontSize = 11.5.sp,
                            color = if (userProfile != null) Color(0xFFA5D6A7) else Color(0xFF80DEEA),
                            maxLines = 2
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Abrir autenticación",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Banner de Bienvenida y Ancla Emocional en Azul Platinado & Amarillo
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AzulPlatinadoCard
                ),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        colors = listOf(AzulPlatinadoBorder, Color(0xFF38BDF8).copy(alpha = 0.6f))
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_banner")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            LykkeSynapseAnimation(
                                size = 52.dp,
                                showAura = false
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringsUI.tituloEspacio,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = stringsUI.nombreApp,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        // Botón de Donaciones en Forma de Cápsula Anaranjado (Esquina Superior Derecha)
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFFF97316), // Naranja vibrante
                            border = BorderStroke(1.2.dp, Color(0xFFFFEDD5)),
                            shadowElevation = 5.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable {
                                    ModuloAccessManager.abrirPasarelaPago(context, "donacion")
                                }
                                .testTag("btn_donaciones_capsula_header")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Donaciones",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Donar",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tu refugio digital para aliviar el estrés, transformar la frustración en resiliencia y guiarte en tus metas con diario emocional seguro y psicología profesional.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }
            }
        }

        // Banner informativo de Límite de Día Superado / Renovación Diaria
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2B24)),
                border = BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.65f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("day_limit_passed_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4ADE80).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌅", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Límite de Día Superado • Nuevo Ciclo Activo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tus 5 accesos gratuitos diarios se han renovado y tu racha de hábitos está lista para sumar hoy.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // Mascota Lykke Interactiva con Animación de Salto, Voz y Tareas
        item {
            MascotaLykke(
                perfil = perfil,
                languageCode = languageCode,
                isTaskCompletedTrigger = sessionCount > 0,
                onVoiceSpeak = onVoiceSpeak,
                onOpenVoiceConfig = { onNavigate(NavigationScreen.VOICE_CONFIG) }
            )
        }

        // Selector Principal de Modos de Operación: Modo Local (Sin Internet) vs Con Internet
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE)
                            listOf(Color(0xFF059669), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_selector_modo_principal")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE) "🛡️" else "🌐",
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Modo de Conexión de la App",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        TextButton(
                            onClick = onCambiarModo,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Opciones",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. MODO LOCAL (Sin Internet / 100% Offline)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE)
                                Color(0xFF064E3B) else Color(0xFF0F172A),
                            border = BorderStroke(
                                if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE) 2.dp else 1.dp,
                                if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE) Color(0xFF34D399) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onSeleccionarModoConexion?.invoke(com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE)
                                    onVoiceSpeak?.invoke("Modo Local activado. Privacidad absoluta sin internet.")
                                }
                                .testTag("btn_home_modo_local")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🛡️", fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Modo Local",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE)
                                            Color(0xFF4ADE80) else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sin Internet • 100% Offline",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                                if (modoConexion == com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "● ACTIVO",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF4ADE80)
                                    )
                                }
                            }
                        }

                        // 2. MODO CON INTERNET
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (modoConexion == com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED)
                                Color(0xFF0C4A6E) else Color(0xFF0F172A),
                            border = BorderStroke(
                                if (modoConexion == com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED) 2.dp else 1.dp,
                                if (modoConexion == com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED) Color(0xFF38BDF8) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onSeleccionarModoConexion?.invoke(com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED)
                                    onVoiceSpeak?.invoke("Modo con Internet activado. Red mundial y satélite disponibles.")
                                }
                                .testTag("btn_home_modo_internet")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🌐", fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Con Internet",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (modoConexion == com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED)
                                            Color(0xFF38BDF8) else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Google, Baidu, Satélite",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                                if (modoConexion == com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "● ACTIVO",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Buscador Compacto con título "Búsqueda" y entrada rápida
        item {
            com.example.ui.components.BuscadorSabiduriaIA(
                languageCode = languageCode,
                modoConexion = modoConexion,
                onCambiarModo = onCambiarModo,
                onSeleccionarModo = onSeleccionarModoConexion,
                onVoiceSpeak = onVoiceSpeak,
                onStartVoiceRecognition = onStartVoiceRecognition,
                onNavigateToService = onNavigateToService,
                onOpenGlobalConnections = { mostrarSubmenuConexiones = true }
            )
        }

        // Botón Dropdown Único de Conexiones Globales (Encapsulado en un solo botón con estado de bloqueo)
        item {
            com.example.ui.components.BotonConexionesGlobalesDropdown(
                esMiembro = esMiembro,
                onNavigateToService = { service -> onNavigateToService?.invoke(service) },
                onRedeemKey = {
                    val intent = Intent(context, CodigoActivacionActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        // Botón para Abrir Toda la Base de Datos Completa Sin Interrupciones
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF063B2B)),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF34D399), Color(0xFFFFD54F).copy(alpha = 0.5f)))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDialogoBaseCompletaHome = true }
                    .testTag("btn_home_abrir_base_datos_completa")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📚", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Base de Datos Completa (Sin Interrupciones)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ver y copiar todas las reflexiones filosóficas y humanistas en 1 bloque",
                                fontSize = 10.5.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Abrir",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Botón de Estado del Build: Build ➔ Build App Bundle(s)/APK(s) ➔ Build APK(s) & google-services.json
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D253A)),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF34D399)))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDialogoBuildApk = true }
                    .testTag("btn_home_build_apk_google_services")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Build APK & Google Services",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF064E3B)
                                ) {
                                    Text(
                                        text = "google-services.json ✓",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Build ➔ Build App Bundle(s)/APK(s) ➔ Build APK(s) (app-debug.apk listo)",
                                fontSize = 10.5.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Abrir",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 🎁 Regalo para el Alma (Amor Propio) & 📖 Sabiduría Familiar, Perdón y Cápsulas
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFB8860B), Color(0xFF38BDF8)))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_ecosistema_amor_propio_familia")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎁", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Amor Propio & Sabiduría Familiar",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        if (totalRegalosPersistentes > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFB8860B).copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color(0xFFFFD54F))
                            ) {
                                Text(
                                    text = "$totalRegalosPersistentes detalles",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { mostrarDialogoRegaloAmorPropio = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB8860B),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_abrir_regalo_alma")
                        ) {
                            Text("🎁 Regalo Diario", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { mostrarDialogoLecturasFamilia = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_abrir_lecturas_familia")
                        ) {
                            Text("📖 Familia & Cápsulas", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (climaSensacionTermica != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "🌡️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = climaSensacionTermica ?: "",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }

        // Radar Emocional & Vibe Check (Botón más pequeño y compacto)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(AzulPlatinadoBorder, Color(0xFFFFD54F).copy(alpha = 0.5f)))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(NavigationScreen.TRACKER) }
                    .testTag("btn_modo_tracker")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD54F).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧭", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Radar Emocional & Vibe Check",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFFFD54F)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(containerColor = Color(0xFF0284C7)) {
                                Text("NUEVO", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Afirmación estoica (HACER/SER), ánimo y racha.",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Ir al Tracker",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Música Relajante e Internacional (Alegre, estéreo potente, selector de países, flechas y volumen)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMusicPlaying) Color(0xFF14382E).copy(alpha = 0.95f) else Color(0xFF102824).copy(alpha = 0.85f)
                ),
                border = BorderStroke(1.dp, if (isMusicPlaying) Color(0xFF4ADE80).copy(alpha = 0.7f) else Color(0xFF38BDF8).copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("music_controller_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Cabecera interactiva con Melodía y selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarCatalogoMelodias = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isMusicPlaying) Color(0xFFFFD54F).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = melodiaActual.bandera, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = melodiaActual.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${melodiaActual.pais} • ${melodiaActual.genero}${if (isMusicPlaying) " • Estéreo 🔊" else " • Pausado"}",
                                fontSize = 11.sp,
                                color = if (isMusicPlaying) Color(0xFF4ADE80) else Color(0xFF94A3B8)
                            )
                        }

                        OutlinedButton(
                            onClick = { mostrarCatalogoMelodias = true },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("25 Melodías", fontSize = 10.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Controles de Música: Flechitas Anterior / Siguiente, Play/Pausa y Volumen Subir/Bajar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Navegación de pistas con flechas y Play/Pausa
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onPrevMelodia,
                                modifier = Modifier.size(40.dp).testTag("btn_melodia_prev")
                            ) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Melodía Anterior", tint = Color(0xFFFFD54F))
                            }

                            FilledTonalButton(
                                onClick = onToggleMusic,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isMusicPlaying) Color(0xFF10B981) else Color(0xFF1E3A8A),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_music_play_pause")
                            ) {
                                Icon(
                                    imageVector = if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isMusicPlaying) "Pausar" else "Sonar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = onNextMelodia,
                                modifier = Modifier.size(40.dp).testTag("btn_melodia_next")
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente Melodía", tint = Color(0xFFFFD54F))
                            }
                        }

                        // Controles de volumen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = onBajarVolumen,
                                modifier = Modifier.size(34.dp).testTag("btn_volumen_bajar")
                            ) {
                                Icon(Icons.Default.VolumeDown, contentDescription = "Bajar Volumen", tint = Color(0xFFE2E8F0), modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = "${(musicVolume * 100).toInt()}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )

                            IconButton(
                                onClick = onSubirVolumen,
                                modifier = Modifier.size(34.dp).testTag("btn_volumen_subir")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Subir Volumen", tint = Color(0xFF4ADE80), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Progreso de Gamificación y Puntos de Bienestar
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
                border = BorderStroke(1.dp, AzulPlatinadoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(NavigationScreen.SESSIONS_DIPLOMA) }
                    .testTag("gamification_progress_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Nivel $userLevel: $userLevelTitle",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFFFD54F)
                                )
                                Text(
                                    text = "$userXp Puntos de Bienestar acumulados",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4ADE80)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F382E))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Ver Insignias", fontSize = 11.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    val currentLevelBase = (userLevel - 1) * 120
                    val levelProgress = ((userXp - currentLevelBase).toFloat() / 120f).coerceIn(0.05f, 1f)

                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFFFD54F),
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gana XP escribiendo en tu diario (+40), respondiendo preguntas (+50) o respirando (+40).",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // Título de Sección de Módulos
        item {
            Text(
                text = "Módulos de Acompañamiento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Fila 1: Diario Seguro y Chat de Desahogo (Cuadros Directos)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = stringsUI.menuDiario,
                    subtitle = stringsUI.subtituloDiario,
                    icon = Icons.Default.MenuBook,
                    tint = Color(0xFF4ADE80),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_diario",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.DIARY) }
                )
                ActionCard(
                    title = stringsUI.menuChat,
                    subtitle = stringsUI.subtituloChat,
                    icon = Icons.Default.ChatBubble,
                    tint = Color(0xFF38BDF8),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_chat",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.CHAT) }
                )
            }
        }

        // Fila 2: Languages / Idiomas & Modo Tracker
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = "${stringsUI.botonIdiomas} (12)",
                    subtitle = "Checklist Danés, Mayas & Global",
                    icon = Icons.Default.Translate,
                    tint = Color(0xFFFFD54F),
                    containerColor = AzulPlatinadoCard,
                    borderColor = Color(0xFFFFD54F).copy(alpha = 0.5f),
                    testTag = "btn_menu_languages",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.LANGUAGES) }
                )
                ActionCard(
                    title = stringsUI.menuTracker,
                    subtitle = stringsUI.subtituloTracker,
                    icon = Icons.Default.SelfImprovement,
                    tint = Color(0xFF4ADE80),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_tracker",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.TRACKER) }
                )
            }
        }

        // Fila 3: Juegos de Preguntas y Respiración 4-7-8
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = stringsUI.menuJuegos,
                    subtitle = stringsUI.subtituloJuegos,
                    icon = Icons.Default.Quiz,
                    tint = Color(0xFFFFB74D),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_games",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.GAMES) }
                )
                ActionCard(
                    title = stringsUI.menuRespira,
                    subtitle = stringsUI.subtituloRespira,
                    icon = Icons.Default.Air,
                    tint = Color(0xFF38BDF8),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_breathing",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.BREATHING) }
                )
            }
        }

        // Fila 4: Citas con Psicólogo y Regalos & Jornadas Gratuitas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = stringsUI.menuCitas,
                    subtitle = stringsUI.subtituloCitas,
                    icon = Icons.Default.VideoCall,
                    tint = Color(0xFFBA68C8),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_booking",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.BOOKING) }
                )
                ActionCard(
                    title = stringsUI.menuRegalos,
                    subtitle = stringsUI.subtituloRegalos,
                    icon = Icons.Default.CardGiftcard,
                    tint = Color(0xFF38BDF8),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_solidarity",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.SOLIDARITY) }
                )
            }
        }

        // Fila 5: Insignias & Diplomas y Club de asesoría ilimitado (CORREGIDO OFICIAL)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = stringsUI.menuDiplomas,
                    subtitle = stringsUI.subtituloDiplomas,
                    icon = Icons.Default.WorkspacePremium,
                    tint = Color(0xFFFFD54F),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btnInsigniasDiplomas",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.SESSIONS_DIPLOMA) }
                )
                ActionCard(
                    title = stringsUI.botonClubAsesoria, // "Club de asesoría ilimitado"
                    subtitle = if (esMiembro) "Acceso Vitalicio Activo ✨" else stringsUI.subtituloClub,
                    icon = if (esMiembro) Icons.Default.Verified else Icons.Default.LockOpen,
                    tint = if (esMiembro) Color(0xFFFFD54F) else Color(0xFF38BDF8),
                    containerColor = AzulPlatinadoCard,
                    borderColor = if (esMiembro) Color(0xFFFFD54F).copy(alpha = 0.6f) else AzulPlatinadoBorder,
                    testTag = "btnClubAsesoriaIlimitado",
                    modifier = Modifier.weight(1f),
                    onClick = { showClubConsultoriaDialog = true }
                )
            }
        }

        // Fila 6: Personalización de Ambiente & Trámite de Diploma
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = stringsUI.menuAmbiente,
                    subtitle = stringsUI.subtituloAmbiente,
                    icon = Icons.Default.Palette,
                    tint = Color(0xFF80DEEA),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btn_menu_settings",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavigationScreen.SETTINGS) }
                )
                ActionCard(
                    title = "Trámite Diploma",
                    subtitle = stringsUI.subtituloTramiteDiploma,
                    icon = Icons.Default.School,
                    tint = Color(0xFFFFD54F),
                    containerColor = AzulPlatinadoCard,
                    borderColor = AzulPlatinadoBorder,
                    testTag = "btnTramiteDiplomaPasarela",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        ModuloAccessManager.abrirPasarelaPago(context, "diploma")
                    }
                )
            }
        }

        // Fila 7: Canjear Clave VIP & Donaciones Solidarias (Ambos Sobresalen)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = stringsUI.menuCanjearClave,
                    subtitle = stringsUI.subtituloCanjear,
                    icon = Icons.Default.Key,
                    tint = Color(0xFFFFD54F),
                    containerColor = CanjearClaveVipBg,
                    borderColor = CanjearClaveVipBorder,
                    vipStripes = true,
                    testTag = "btn_menu_canjear_clave",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(context, CodigoActivacionActivity::class.java)
                        context.startActivity(intent)
                    }
                )
                ActionCard(
                    title = stringsUI.menuAportacion,
                    subtitle = stringsUI.subtituloAportacion,
                    icon = Icons.Default.Favorite,
                    tint = Color(0xFFF97316),
                    containerColor = Color(0xFF2C1408),
                    borderColor = Color(0xFFEA580C).copy(alpha = 0.7f),
                    friendshipStripes = true,
                    testTag = "btnDonarSoporte",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        ModuloAccessManager.abrirPasarelaPago(context, "donacion")
                    }
                )
            }
        }

        // Botón de Prueba Diaria: Delgado y Pequeño (debajo de Canjear Clave y Donaciones)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E2E)),
                border = BorderStroke(
                    1.dp,
                    if (esMiembro) Color(0xFFFFD54F).copy(alpha = 0.6f) else Color(0xFF38BDF8).copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!esMiembro) {
                            val intent = Intent(context, CodigoActivacionActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            showClubConsultoriaDialog = true
                        }
                    }
                    .testTag("membership_status_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (esMiembro) "✨" else "🔑",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (esMiembro) "Prueba Diaria: Pase Vitalicio Activo" else "Prueba Diaria: $usosGratis de 5 usos gratuitos usados hoy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (esMiembro) Color(0xFFFFD54F) else Color(0xFFE2E8F0)
                            )
                            if (!esMiembro) {
                                Spacer(modifier = Modifier.height(2.dp))
                                LinearProgressIndicator(
                                    progress = { (usosGratis / 5f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (usosGratis >= 5) Color(0xFFEF4444) else Color(0xFFFFD54F),
                                    trackColor = Color.White.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (esMiembro) "Ilimitado" else "${5 - usosGratis} restantes",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esMiembro) Color(0xFFFFD54F) else if (usosGratis >= 5) Color(0xFFFF8A80) else Color(0xFF4ADE80)
                    )
                }
            }
        }

        // Logo en Pequeño de LivsForm Lykke al Final y Tarjeta de Marca Institucional (Marco en Azul Platinado)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AzulPlatinadoCard
                ),
                border = BorderStroke(1.dp, AzulPlatinadoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("livsform_footer_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo oficial de LivsForm Lykke con aura animada viva y pulso armónico
                    val infiniteLogoTransition = rememberInfiniteTransition(label = "footer_logo_anim")
                    val logoPulseScale by infiniteLogoTransition.animateFloat(
                        initialValue = 0.96f,
                        targetValue = 1.04f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2400, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "logo_scale_pulse"
                    )
                    val logoAuraAlpha by infiniteLogoTransition.animateFloat(
                        initialValue = 0.25f,
                        targetValue = 0.65f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2400, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "logo_aura_pulse"
                    )

                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Halo de luz y aura viva estilo PlayStation / neón cian
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = logoPulseScale * 1.15f
                                    scaleY = logoPulseScale * 1.15f
                                    alpha = logoAuraAlpha
                                }
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF38BDF8),
                                            Color(0xFF0284C7).copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        // Emblema circular oficial
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F1E36),
                            border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.8f)),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(62.dp)
                                .graphicsLayer {
                                    scaleX = logoPulseScale
                                    scaleY = logoPulseScale
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_app_icon_circular),
                                contentDescription = "Logo LivsForm Lykke",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Text(
                        text = "LivsForm Lykke • Asesorías Consultorias psicología",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFFFD54F),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Acompañamiento profesional, confidencialidad médica y salud emocional.",
                        fontSize = 12.sp,
                        color = Color(0xFF38BDF8),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Supervisión clínica y base de datos: tiemposdestinos@gmail.com",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Modal interactivo de Voz y Órdenes de la IA Conciencia
    if (mostrarDialogoVozGlobal) {
        DialogoVozInteligenteLykke(
            perfil = perfil,
            languageCode = languageCode,
            voiceManager = voiceManager,
            onDismiss = { mostrarDialogoVozGlobal = false }
        )
    }

    // Modal Diálogo: Base de Datos Completa (Sin Interrupciones)
    if (mostrarDialogoBaseCompletaHome) {
        com.example.ui.components.DialogoBaseDeDatosCompleta(
            languageCode = languageCode,
            onDismiss = { mostrarDialogoBaseCompletaHome = false },
            onVoiceSpeak = onVoiceSpeak
        )
    }

    // Modal Diálogo: Regalo para el Alma (Detalles de Amor Propio)
    if (mostrarDialogoRegaloAmorPropio) {
        DialogoRegaloAmorPropio(
            onDismiss = {
                mostrarDialogoRegaloAmorPropio = false
                totalRegalosPersistentes = persistentes.contadorRegalos
            }
        )
    }

    // Modal Diálogo: Sabiduría Familiar, Perdón, Cápsulas y Terapias
    if (mostrarDialogoLecturasFamilia) {
        DialogoLecturasFamilia(
            onDismiss = { mostrarDialogoLecturasFamilia = false }
        )
    }

    // Modal Diálogo: Build APK & Google Services (Firebase)
    if (mostrarDialogoBuildApk) {
        DialogoBuildApkYGoogleServices(
            onDismiss = { mostrarDialogoBuildApk = false }
        )
    }

    // Modal Sub-Menú Conexiones Globales
    if (mostrarSubmenuConexiones) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { mostrarSubmenuConexiones = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF070F1E)),
                border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.90f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌐", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Conexiones Globales",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                                Text(
                                    text = "Pasarela a los principales motores mundiales",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        IconButton(
                            onClick = { mostrarSubmenuConexiones = false },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    com.example.ui.components.MenuConectividadGlobal(
                        onNavigateToService = { service ->
                            mostrarSubmenuConexiones = false
                            onNavigateToService?.invoke(service)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Modal Dialog: Servicio Asesoría Lykke Ilimitado & Comunidad Lykke (Todo bloqueado con candado hasta canjear clave)
    if (showClubConsultoriaDialog) {
        AlertDialog(
            onDismissRequest = { showClubConsultoriaDialog = false },
            containerColor = AzulPlatinadoCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (esMiembro) Icons.Default.Verified else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Asesoría Ilimitada & Comunidad Lykke",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Banner de estado
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esMiembro) Color(0xFF0F382E) else Color(0xFF381414)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (esMiembro) Color(0xFF4ADE80) else Color(0xFFEF4444)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (esMiembro) "✨" else "🔒",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (esMiembro)
                                    "¡Membresía Vitalicia Activa! Tienes acceso ilimitado y confidencial a todas las funciones."
                                else
                                    "FUNCIONES BLOQUEADAS: Para acceder a estos servicios debes comprarlos y canjear tu clave en la opción 'Canjear Clave'.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = if (esMiembro) Color(0xFFE2E8F0) else Color(0xFFFFCDD2),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Text(
                        text = "Funciones incluidas en este servicio:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )

                    // 1. Diario Seguro
                    ItemServicioClub(
                        icono = Icons.Default.MenuBook,
                        titulo = "Diario Emocional Seguro",
                        descripcion = "Escritura encriptada, reflexiones privadas y anónimas",
                        esMiembro = esMiembro,
                        testTag = "item_club_diario",
                        onClick = {
                            if (esMiembro) {
                                showClubConsultoriaDialog = false
                                onNavigate(NavigationScreen.DIARY)
                            } else {
                                val intent = Intent(context, CodigoActivacionActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    )

                    // 2. Chat de Desahogo
                    ItemServicioClub(
                        icono = Icons.Default.ChatBubble,
                        titulo = "Chat de Desahogo Confidencial",
                        descripcion = "Contención y escucha activa con psicología clínica",
                        esMiembro = esMiembro,
                        testTag = "item_club_chat",
                        onClick = {
                            if (esMiembro) {
                                showClubConsultoriaDialog = false
                                onNavigate(NavigationScreen.CHAT)
                            } else {
                                val intent = Intent(context, CodigoActivacionActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    )

                    // 3. Chat Comunidad Lykke
                    ItemServicioClub(
                        icono = Icons.Default.Groups,
                        titulo = "Chat Comunidad Lykke",
                        descripcion = "Red global abierta: Matrix, IRC, XMPP & Clan (Privacidad)",
                        esMiembro = esMiembro,
                        testTag = "item_club_comunidad",
                        onClick = {
                            if (esMiembro) {
                                showClubConsultoriaDialog = false
                                onNavigate(NavigationScreen.COMMUNITY_CHAT)
                            } else {
                                val intent = Intent(context, CodigoActivacionActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    )

                    // 4. Equilibrio Diario: Pausa de Descompresión Somática
                    ItemServicioClub(
                        icono = Icons.Default.SelfImprovement,
                        titulo = "Equilibrio Diario: Descompresión Somática",
                        descripcion = "Descarga de tensión muscular, calma nerviosa y anclaje",
                        esMiembro = esMiembro,
                        testTag = "item_club_descompresion",
                        onClick = {
                            if (esMiembro) {
                                showClubConsultoriaDialog = false
                                onStartBreathing()
                            } else {
                                val intent = Intent(context, CodigoActivacionActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    )

                    // 5. Consultoría Clínica Personalizada
                    ItemServicioClub(
                        icono = Icons.Default.VideoCall,
                        titulo = "Consultoría y Asesoría Clínica",
                        descripcion = "Sesiones personalizadas y acompañamiento profesional",
                        esMiembro = esMiembro,
                        testTag = "item_club_consultoria",
                        onClick = {
                            if (esMiembro) {
                                showClubConsultoriaDialog = false
                                onNavigate(NavigationScreen.BOOKING)
                            } else {
                                val intent = Intent(context, CodigoActivacionActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    )

                    if (!esMiembro) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    showClubConsultoriaDialog = false
                                    val intent = Intent(context, CodigoActivacionActivity::class.java)
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                                modifier = Modifier.weight(1f).testTag("btn_ir_canjear_clave_desde_club")
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Canjear Clave", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    showClubConsultoriaDialog = false
                                    ModuloAccessManager.abrirPasarelaPago(context, "consultoria")
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Comprar Clave", fontSize = 11.sp, color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showClubConsultoriaDialog = false }) {
                    Text("Cerrar", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal Catálogo de 25 Melodías Globales por País y Género
    if (mostrarCatalogoMelodias) {
        AlertDialog(
            onDismissRequest = { mostrarCatalogoMelodias = false },
            containerColor = AzulPlatinadoCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎶", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Melodías de Alegría & Felicidad",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "25 temas internacionales en estéreo envolvente",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CatalogoMelodiasGlobales.size) { idx ->
                        val melodia = CatalogoMelodiasGlobales[idx]
                        val isSelected = melodia.id == melodiaActual.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF0F382E) else AzulPlatinadoCardHover
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectMelodia(melodia)
                                    mostrarCatalogoMelodias = false
                                }
                                .testTag("melodia_item_${melodia.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = melodia.bandera, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = melodia.nombre,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFFFFD54F) else Color.White
                                    )
                                    Text(
                                        text = "${melodia.pais} • ${melodia.genero}",
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color(0xFF4ADE80) else Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = melodia.descripcion,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                if (isSelected && isMusicPlaying) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "🔊 Sonando", fontSize = 10.sp, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarCatalogoMelodias = false }) {
                    Text("Cerrar", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ItemServicioClub(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    esMiembro: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCardHover),
        border = BorderStroke(
            1.dp,
            if (esMiembro) Color(0xFF38BDF8).copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (esMiembro) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = if (esMiembro) Color(0xFF38BDF8) else Color(0xFFFF8A80),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = descripcion,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Badge(
                containerColor = if (esMiembro) Color(0xFF0F382E) else Color(0xFF3B1515)
            ) {
                Text(
                    text = if (esMiembro) "✨ DISPONIBLE" else "🔒 BLOQUEADO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (esMiembro) Color(0xFF4ADE80) else Color(0xFFFF8A80)
                )
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = AzulPlatinadoCard,
    borderColor: Color? = AzulPlatinadoBorder,
    friendshipStripes: Boolean = false,
    vipStripes: Boolean = false,
    testTag: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = borderColor?.let { BorderStroke(1.dp, it) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .height(if (friendshipStripes || vipStripes) 136.dp else 130.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (friendshipStripes) {
                // Franjas de amistad decorativas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad1))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad2))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad3))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad4))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad5))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaAmistad6))
                }
            } else if (vipStripes) {
                // Franjas VIP doradas y ámbar decorativas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaVipOro))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaVipAmbar))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaVipOro))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FranjaVipAmbar))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                }

                if (friendshipStripes) {
                    Text(
                        text = "🤝 AMISTAD",
                        color = Color(0xFFFFD54F),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (vipStripes) {
                    Text(
                        text = "🔑 VIP CLAVE",
                        color = Color(0xFFFFD54F),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}
