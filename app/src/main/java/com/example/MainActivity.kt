package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.LykkeVoiceManager
import com.example.audio.TipoVozMascota
import com.example.data.PerfilUsuario
import com.example.data.UserProfileStore
import com.example.ui.LykkeViewModel
import com.example.ui.NavigationScreen
import com.example.ui.components.LykkeBackground
import com.example.ui.components.LykkeBottomNavigation
import com.example.ui.components.LykkeTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private var onSpeechResultListener: ((String) -> Unit)? = null

    private val speechRecognitionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                onSpeechResultListener?.invoke(matches[0])
            }
        }
    }

    fun launchSpeechRecognition(onResult: (String) -> Unit) {
        onSpeechResultListener = onResult
        val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
            putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Te escucho. Háblame de cómo te sientes hoy...")
        }
        try {
            speechRecognitionLauncher.launch(intent)
        } catch (_: Exception) {
            android.widget.Toast.makeText(
                this,
                "Micrófono no disponible en este entorno. Puedes escribir tus órdenes directamente en el buscador de la IA.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        val targetScreen = intent?.getStringExtra("SCREEN_TARGET")
        setContent {
            MyApplicationTheme {
                val viewModel: LykkeViewModel = viewModel()
                var showSplash by rememberSaveable { mutableStateOf(true) }

                LaunchedEffect(targetScreen) {
                    when (targetScreen) {
                        "DIARY" -> viewModel.navigateTo(NavigationScreen.DIARY)
                        "CHAT" -> viewModel.navigateTo(NavigationScreen.CHAT)
                        "DIPLOMA" -> viewModel.navigateTo(NavigationScreen.SESSIONS_DIPLOMA)
                    }
                }

                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else {
                    LykkeAppRoot(
                        viewModel = viewModel,
                        onStartVoiceInput = { callback -> launchSpeechRecognition(callback) }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun LykkeAppRoot(
    viewModel: LykkeViewModel,
    onStartVoiceInput: ((String) -> Unit) -> Unit = {}
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val environment by viewModel.currentEnvironment.collectAsStateWithLifecycle()
    val textColor by viewModel.currentTextColor.collectAsStateWithLifecycle()
    val overlayAlpha by viewModel.currentOverlayAlpha.collectAsStateWithLifecycle()
    val sessionCount by viewModel.sessionCount.collectAsStateWithLifecycle()
    val sessionHistory by viewModel.sessionHistory.collectAsStateWithLifecycle()
    val activeReminder by viewModel.activeReminder.collectAsStateWithLifecycle()
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatTyping by viewModel.isChatBotTyping.collectAsStateWithLifecycle()
    val breathingState by viewModel.breathingState.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val totalDonated by viewModel.totalDonatedUsd.collectAsStateWithLifecycle()
    val purchasedGifts by viewModel.purchasedGifts.collectAsStateWithLifecycle()
    val dialogMessage by viewModel.generalDialogMessage.collectAsStateWithLifecycle()
    val completedTasksCount by viewModel.completedTasksRewardCount.collectAsStateWithLifecycle()

    // Diario & Gamificación & Música
    val diaryEntries by viewModel.diaryEntries.collectAsStateWithLifecycle()
    val backupReports by viewModel.backupReports.collectAsStateWithLifecycle()
    val totalXp by viewModel.totalXp.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsStateWithLifecycle()
    val melodiaActual by viewModel.melodiaActual.collectAsStateWithLifecycle()
    val musicVolume by viewModel.musicVolume.collectAsStateWithLifecycle()
    val currentLanguageCode by viewModel.currentLanguageCode.collectAsStateWithLifecycle()

    // Firebase Auth State
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val (userLevel, userLevelTitle) = viewModel.getUserLevel()
    var showContactModal by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val voiceManager = remember { LykkeVoiceManager(context) }
    val profileStore = remember { UserProfileStore(context) }
    val perfilUsuario by profileStore.perfilFlow.collectAsState(initial = PerfilUsuario())

    // Modo de Conexión de la I.A. Conciencia
    val modoConexionIA by viewModel.modoConexionIA.collectAsStateWithLifecycle()
    val mostrarDialogoModo by viewModel.mostrarDialogoModo.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.liberarRecursos()
        }
    }

    // Back handler to navigate back to Home if not on Home
    BackHandler(enabled = currentScreen != NavigationScreen.HOME) {
        if (currentScreen == NavigationScreen.GAMES && quizState.category != null && !quizState.isFinished) {
            viewModel.resetQuiz()
        } else {
            viewModel.navigateTo(NavigationScreen.HOME)
        }
    }

    LykkeBackground(
        environment = environment,
        overlayAlpha = overlayAlpha
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                LykkeTopBar(
                    sessionCount = sessionCount,
                    userLevel = userLevel,
                    userXp = totalXp,
                    isMusicPlaying = isMusicPlaying,
                    textColor = textColor,
                    userProfile = userProfile,
                    languageCode = currentLanguageCode,
                    apodoConciencia = perfilUsuario.apodoConciencia,
                    modoConexion = modoConexionIA,
                    onOpenSelectorModo = { viewModel.abrirSelectorModo() },
                    onToggleMusic = { viewModel.toggleMusic() },
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onTriggerSelfCare = { viewModel.sendSelfCareNotificationToChat() },
                    onShowContact = { showContactModal = true },
                    onDonationClick = { com.example.data.ModuloAccessManager.abrirPasarelaPago(context, "donacion") },
                    onSignOut = { viewModel.signOutUser() }
                )
            },
            bottomBar = {
                LykkeBottomNavigation(
                    currentScreen = currentScreen,
                    onSelectScreen = { screen ->
                        if (screen != currentScreen) {
                            if (currentScreen == NavigationScreen.GAMES) {
                                viewModel.resetQuiz()
                            }
                            viewModel.navigateTo(screen)
                        }
                    },
                    languageCode = currentLanguageCode
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    NavigationScreen.HOME -> {
                        HomeScreen(
                            sessionCount = sessionCount,
                            userLevel = userLevel,
                            userLevelTitle = userLevelTitle,
                            userXp = totalXp,
                            isMusicPlaying = isMusicPlaying,
                            melodiaActual = melodiaActual,
                            musicVolume = musicVolume,
                            onToggleMusic = { viewModel.toggleMusic() },
                            onSelectMelodia = { melodia -> viewModel.reproducirMelodiaPais(melodia) },
                            onNextMelodia = { viewModel.cambiarMelodiaSiguiente() },
                            onPrevMelodia = { viewModel.cambiarMelodiaAnterior() },
                            onSubirVolumen = { viewModel.subirVolumen() },
                            onBajarVolumen = { viewModel.bajarVolumen() },
                            activeReminder = activeReminder,
                            textColor = textColor,
                            languageCode = currentLanguageCode,
                            perfil = perfilUsuario,
                            userProfile = userProfile,
                            modoConexion = modoConexionIA,
                            onCambiarModo = { viewModel.abrirSelectorModo() },
                            onSeleccionarModoConexion = { nuevoModo -> viewModel.seleccionarModoConexion(nuevoModo) },
                            onVoiceSpeak = { text -> voiceManager.hablar(text, TipoVozMascota.FEMENINA, currentLanguageCode) },
                            onStartVoiceRecognition = onStartVoiceInput,
                            onNavigateToService = { service -> viewModel.abrirServicioGlobal(service) },
                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                            onStartBreathing = {
                                viewModel.navigateTo(NavigationScreen.BREATHING)
                                viewModel.startBreathingExercise("4-7-8")
                            }
                        )
                    }
                    NavigationScreen.DIARY -> {
                        DiaryScreen(
                            entries = diaryEntries,
                            backupReports = backupReports,
                            textColor = textColor,
                            onSaveEntry = { title, content, tags, mood, authorAlias ->
                                viewModel.saveDiaryEntry(title, content, tags, mood, authorAlias)
                            }
                        )
                    }
                    NavigationScreen.GAMES -> {
                        QuestionGamesScreen(
                            quizState = quizState,
                            textColor = textColor,
                            onSelectCategory = { cat -> viewModel.startQuizCategory(cat) },
                            onSelectOption = { opt -> viewModel.selectQuizOption(opt) },
                            onNextQuestion = { viewModel.nextQuizQuestion() },
                            onResetQuiz = { viewModel.resetQuiz() }
                        )
                    }
                    NavigationScreen.CHAT -> {
                        ChatScreen(
                            messages = chatMessages,
                            isTyping = isChatTyping,
                            textColor = textColor,
                            onSendMessage = { text -> viewModel.sendChatMessage(text) },
                            onTriggerSelfCare = { viewModel.sendSelfCareNotificationToChat() },
                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                            onStartVoiceInput = onStartVoiceInput
                        )
                    }
                    NavigationScreen.BREATHING -> {
                        BreathingScreen(
                            state = breathingState,
                            textColor = textColor,
                            onStartExercise = { type -> viewModel.startBreathingExercise(type) },
                            onStopExercise = { viewModel.stopBreathing() }
                        )
                    }
                    NavigationScreen.SESSIONS_DIPLOMA -> {
                        DiplomaProgressScreen(
                            sessionCount = sessionCount,
                            sessionHistory = sessionHistory,
                            userLevel = userLevel,
                            userLevelTitle = userLevelTitle,
                            userXp = totalXp,
                            badges = badges,
                            textColor = textColor,
                            onRequestDiplomaEmail = { email, name ->
                                viewModel.requestDiplomaEmail(email, name)
                            },
                            onFastTrackTenSessions = { viewModel.advanceToTenSessionsForTesting() }
                        )
                    }
                    NavigationScreen.BOOKING -> {
                        BookingScreen(
                            bookings = bookings,
                            textColor = textColor,
                            onBookAppointment = { name, whatsapp, email, type, motive, datePref, timeSlot ->
                                viewModel.bookAppointment(name, whatsapp, email, type, motive, datePref, timeSlot)
                            }
                        )
                    }
                    NavigationScreen.SOLIDARITY -> {
                        SolidarityScreen(
                            gifts = viewModel.availableGifts,
                            purchasedGifts = purchasedGifts,
                            totalDonatedUsd = totalDonated,
                            textColor = textColor,
                            jornadas = viewModel.availableJornadas,
                            completedTasksCount = completedTasksCount,
                            onPurchaseGift = { gift -> viewModel.purchaseGift(gift) },
                            onMakeDonation = { amt -> viewModel.makeCustomDonation(amt) }
                        )
                    }
                    NavigationScreen.SETTINGS -> {
                        EnvironmentSettingsScreen(
                            currentEnvironment = environment,
                            currentTextColor = textColor,
                            currentOverlayAlpha = overlayAlpha,
                            onSelectEnvironment = { env -> viewModel.setEnvironment(env) },
                            onSelectTextColor = { color -> viewModel.setTextColor(color) },
                            onSetOverlayAlpha = { alpha -> viewModel.setOverlayAlpha(alpha) }
                        )
                    }
                    NavigationScreen.AUTH -> {
                        AuthScreen(
                            currentUser = currentUser,
                            userProfile = userProfile,
                            onAuthSuccess = { user, alias ->
                                viewModel.syncUserProfileWithAlias(user, alias)
                                viewModel.navigateTo(NavigationScreen.HOME)
                            },
                            onSignOut = {
                                viewModel.signOutUser()
                            },
                            onUpdateAlias = { newAlias ->
                                viewModel.updateProfileAlias(newAlias)
                            },
                            onNavigateBack = {
                                viewModel.navigateTo(NavigationScreen.HOME)
                            }
                        )
                    }
                    NavigationScreen.TRACKER -> {
                        TrackerScreen(
                            sessionCount = sessionCount,
                            totalXp = totalXp,
                            textColor = textColor,
                            perfil = perfilUsuario,
                            onVoiceSpeak = { text -> voiceManager.hablar(text, TipoVozMascota.FEMENINA) },
                            onAddXp = { amount, reason -> viewModel.addXp(amount, reason) },
                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                            onStartVoiceInput = onStartVoiceInput
                        )
                    }
                    NavigationScreen.LANGUAGES -> {
                        PantallaIdiomasChecklist(
                            currentLanguageCode = currentLanguageCode,
                            onSelectLanguage = { code -> viewModel.setLanguage(code) },
                            onNavigateBack = { viewModel.navigateTo(NavigationScreen.HOME) },
                            onStartVoiceInput = onStartVoiceInput
                        )
                    }
                    NavigationScreen.VOICE_CONFIG -> {
                        PantallaConfiguracionVozLykke(
                            voiceManager = voiceManager,
                            profileStore = profileStore,
                            onNavigateBack = { viewModel.navigateTo(NavigationScreen.HOME) }
                        )
                    }
                    NavigationScreen.COMMUNITY_CHAT -> {
                        ChatComunidadLykkeScreen(
                            perfil = perfilUsuario,
                            onNavigateBack = { viewModel.navigateTo(NavigationScreen.HOME) }
                        )
                    }
                    NavigationScreen.INTERNAL_BROWSER -> {
                        val selectedService by viewModel.selectedGlobalService.collectAsStateWithLifecycle()
                        LykkeInternalWebViewScreen(
                            service = selectedService,
                            onNavigateBack = { viewModel.navigateTo(NavigationScreen.HOME) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Selección de Modo de Operación de la I.A. (Local vs Internet)
    if (mostrarDialogoModo) {
        com.example.ui.components.DialogoModoConexion(
            modoActual = modoConexionIA,
            onSeleccionarModo = { nuevoModo ->
                viewModel.seleccionarModoConexion(nuevoModo)
                val saludoVocal = when (nuevoModo) {
                    com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE ->
                        "He iniciado en Modo Local con la base de datos recopilada sin internet. Privacidad total activada. ¿Qué quieres que busquemos hoy o estás listo para tu rutina?"
                    com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED ->
                        "He conectado a internet para ampliar mi capacidad de búsqueda y síntesis. ¿Qué deseas explorar hoy?"
                }
                voiceManager.hablar(saludoVocal, TipoVozMascota.FEMENINA, currentLanguageCode)
            },
            onAbrirServicioGlobal = { service ->
                viewModel.seleccionarModoConexion(com.example.ui.components.ModoConexionIA.INTERNET_EXPANDED)
                viewModel.abrirServicioGlobal(service)
            },
            onDismiss = { viewModel.cerrarSelectorModo() }
        )
    }

    // General Dialog for alerts, feedback & confirmations
    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = {
                Text(
                    text = "Asesorías Consultorias psicología",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = dialogMessage ?: "",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF102838),
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Contact & Institutional Information Modal
    if (showContactModal) {
        AlertDialog(
            onDismissRequest = { showContactModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF80CBC4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Asesorías Consultorias psicología",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Plataforma integral de asesoría psicológica en línea especializada en manejo del estrés, frustración, cumplimiento de metas y proyectos de vida.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "asesoriaslykke@gmail.com",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF80DEEA)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Supervisión clínica: tiemposdestinos@gmail.com",
                            fontSize = 12.sp,
                            color = Color(0xFFA5D6A7)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF80CBC4), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Atención profesional y coordinación vía WhatsApp",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Sesión Audio Psicólogo Digital: $5 USD\n• Videollamada Programada: $15 USD\n• Solo queda pendiente la cuenta para inicio de sesión.\n• Emisión de diploma virtual al completar 10 sesiones.",
                        fontSize = 11.sp,
                        color = Color(0xFFFFE082),
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showContactModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("Entendido", color = Color.White)
                }
            },
            containerColor = Color(0xFF102838),
            shape = RoundedCornerShape(18.dp)
        )
    }
}

private const val TAG_APP_CHECK = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG_APP_CHECK, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG_APP_CHECK, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG_APP_CHECK, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG_APP_CHECK, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG_APP_CHECK, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG_APP_CHECK, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG_APP_CHECK, "Failed to initialize Firebase App Check", e)
    }
}

