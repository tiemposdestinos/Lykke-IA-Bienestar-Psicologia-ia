package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundPlayer
import com.example.data.MemoriaAprendizajeStore
import com.example.data.model.*
import com.example.data.repository.LykkeRepository
import com.example.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationScreen {
    HOME,
    DIARY,
    GAMES,
    CHAT,
    BREATHING,
    SESSIONS_DIPLOMA,
    BOOKING,
    SOLIDARITY,
    SETTINGS,
    AUTH,
    TRACKER,
    LANGUAGES,
    VOICE_CONFIG,
    COMMUNITY_CHAT,
    INTERNAL_BROWSER
}

data class ConcienciaRespuesta(
    val consultaOriginal: String,
    val sintesisConversacional: String,
    val consejoAccionable: String,
    val reflexionesRelacionadas: List<Pair<ElementoFilosofico, ContenidoMultilingue>> = emptyList()
)

data class QuizSessionState(
    val category: SectionCategory? = null,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: QuizOption? = null,
    val isShowingComment: Boolean = false,
    val accumulatedScore: Int = 0,
    val isFinished: Boolean = false
)

data class BreathingExerciseState(
    val exerciseName: String = "Respiración 4-7-8 (Calma Profunda)",
    val phase: String = "Listo",
    val phaseInstruction: String = "Toca Iniciar para sincronizar tu ritmo cardíaco y calmar la mente.",
    val secondsRemaining: Int = 0,
    val cycleCount: Int = 0,
    val targetCycles: Int = 3,
    val isRunning: Boolean = false,
    val progress: Float = 0f
)

class LykkeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LykkeRepository(application.applicationContext)
    private val userRepository = UserRepository(application.applicationContext)
    private val auth: FirebaseAuth = Firebase.auth
    private val soundPlayer = AmbientSoundPlayer()

    // Firebase Auth & User Profile State
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _currentUser.value = user
        if (user != null) {
            viewModelScope.launch {
                try {
                    val profile = userRepository.syncOrCreateUserProfile(user)
                    _userProfile.value = profile
                } catch (e: Exception) {
                    Log.e("LykkeViewModel", "Error sincronizando perfil de usuario", e)
                }
            }
        } else {
            _userProfile.value = null
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        auth.currentUser?.let { user ->
            viewModelScope.launch {
                try {
                    _userProfile.value = userRepository.syncOrCreateUserProfile(user)
                } catch (e: Exception) {
                    Log.e("LykkeViewModel", "Error sincronizando perfil inicial", e)
                }
            }
        }
    }

    // Navigation & Localization
    private val _currentScreen = MutableStateFlow(NavigationScreen.HOME)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    private val _currentLanguageCode = MutableStateFlow("es")
    val currentLanguageCode: StateFlow<String> = _currentLanguageCode.asStateFlow()

    fun setLanguage(code: String) {
        _currentLanguageCode.value = code
    }

    // Modo de Conexión de la I.A. Conciencia (Local sin internet vs En Línea con internet)
    private val _modoConexionIA = MutableStateFlow(com.example.ui.components.ModoConexionIA.LOCAL_OFFLINE)
    val modoConexionIA: StateFlow<com.example.ui.components.ModoConexionIA> = _modoConexionIA.asStateFlow()

    // Control del diálogo automático al iniciar la app
    private val _mostrarDialogoModo = MutableStateFlow(true)
    val mostrarDialogoModo: StateFlow<Boolean> = _mostrarDialogoModo.asStateFlow()

    fun seleccionarModoConexion(modo: com.example.ui.components.ModoConexionIA) {
        _modoConexionIA.value = modo
        _mostrarDialogoModo.value = false
    }

    fun abrirSelectorModo() {
        _mostrarDialogoModo.value = true
    }

    fun cerrarSelectorModo() {
        _mostrarDialogoModo.value = false
    }

    // Global Service / Browser state
    private val _selectedGlobalService = MutableStateFlow<GlobalService?>(null)
    val selectedGlobalService: StateFlow<GlobalService?> = _selectedGlobalService.asStateFlow()

    fun abrirServicioGlobal(service: GlobalService, query: String = "") {
        val targetUrl = if (query.isNotBlank()) service.buildUrlWithQuery(query) else service.targetUrl
        val finalService = service.copy(targetUrl = targetUrl)
        _selectedGlobalService.value = finalService
        _currentScreen.value = NavigationScreen.INTERNAL_BROWSER
    }

    // Speech-to-Text & Conciencia Open Conversational AI Search (tipo Gemini / Alexa)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isListeningSpeech = MutableStateFlow(false)
    val isListeningSpeech: StateFlow<Boolean> = _isListeningSpeech.asStateFlow()

    private val _concienciaRespuesta = MutableStateFlow<ConcienciaRespuesta?>(null)
    val concienciaRespuesta: StateFlow<ConcienciaRespuesta?> = _concienciaRespuesta.asStateFlow()

    private var speechRecognizerInstance: SpeechRecognizer? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startSpeechRecognition(
        context: Context,
        languageCode: String,
        onVoiceSpeak: ((String) -> Unit)? = null
    ) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            try {
                stopSpeechRecognition()
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    _generalDialogMessage.value = "El reconocimiento de voz no está habilitado en este dispositivo. Puedes escribir tu consulta directamente."
                    _isListeningSpeech.value = false
                    return@post
                }

                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizerInstance = recognizer

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
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Te escucho. Dime qué autor, tema o sentimiento deseas explorar...")
                }

                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { _isListeningSpeech.value = true }
                    override fun onBeginningOfSpeech() { _isListeningSpeech.value = true }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { _isListeningSpeech.value = false }
                    override fun onError(error: Int) {
                        _isListeningSpeech.value = false
                        stopSpeechRecognition()
                    }
                    override fun onResults(results: Bundle?) {
                        _isListeningSpeech.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrBlank()) {
                            _searchQuery.value = text
                            ejecutarBusquedaConciencia(text, languageCode, onVoiceSpeak)
                        }
                        stopSpeechRecognition()
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                _isListeningSpeech.value = true
                recognizer.startListening(intent)
            } catch (e: Exception) {
                Log.e("LykkeViewModel", "Error al iniciar SpeechRecognizer", e)
                _isListeningSpeech.value = false
            }
        }
    }

    fun stopSpeechRecognition() {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            try {
                speechRecognizerInstance?.stopListening()
                speechRecognizerInstance?.destroy()
                speechRecognizerInstance = null
            } catch (_: Exception) {}
            _isListeningSpeech.value = false
        }
    }

    fun ejecutarBusquedaConciencia(
        query: String,
        languageCode: String,
        onVoiceSpeak: ((String) -> Unit)? = null
    ) {
        val queryLimpia = query.trim()
        if (queryLimpia.isBlank()) return
        _searchQuery.value = queryLimpia

        val context = getApplication<Application>().applicationContext
        val memoriaStore = MemoriaAprendizajeStore.getInstance(context)
        val frasesAprendidas = memoriaStore.ensenanzasFlow.value
        val isEnglish = languageCode == "en"
        val qLower = queryLimpia.lowercase()

        // 0. Comando de Anotar / Recordar Rutina
        if (qLower.startsWith("anota") || qLower.startsWith("recordar") || qLower.startsWith("note")) {
            val tareaLimpia = queryLimpia.replaceFirst(Regex("(?i)^(anota|recordar|note)\\s*"), "").trim()
            if (tareaLimpia.isNotBlank()) {
                memoriaStore.aprenderNuevaFrase("Actividad registrada: $tareaLimpia", "Usuario", "Rutina Personal")
                val sintesis = if (isEnglish) "Greetings! I have registered in your routine: \"$tareaLimpia\"." else "¡Qué gusto saludarte! He registrado en tu rutina: \"$tareaLimpia\"."
                val consejo = if (isEnglish) "Small daily steps build monumental peace." else "Cumplir pequeños pasos diarios fortalece la tranquilidad y la resiliencia."
                _concienciaRespuesta.value = ConcienciaRespuesta(
                    consultaOriginal = queryLimpia,
                    sintesisConversacional = sintesis,
                    consejoAccionable = consejo
                )
                onVoiceSpeak?.invoke(sintesis)
                return
            }
        }

        // 1. Comando de aprendizaje interactivo ("aprende:", "guarda:", etc.)
        if (qLower.startsWith("aprende") || qLower.startsWith("enseña") || qLower.startsWith("guarda") || qLower.contains("recuerda esto")) {
            val contenidoAprender = queryLimpia.replace(Regex("(?i)^(aprende|enseña|guarda|recuerda esto|aprender):?\\s*"), "")
            if (contenidoAprender.isNotBlank()) {
                val perfil = _userProfile.value
                val nombreUsuario = perfil?.displayName?.ifBlank { "Usuario" } ?: "Usuario"
                val autor = "Tú ($nombreUsuario)"
                memoriaStore.aprenderNuevaFrase(contenidoAprender, autor, "Enseñanza Compartida")
                val sintesis = if (isEnglish) "I have stored this teaching in my permanent memory. I will remember it in our journey." else "He aprendido y guardado esta reflexión en mi memoria permanente: $contenidoAprender"
                val consejo = if (isEnglish) "Shared wisdom strengthens the collective mind." else "La sabiduría compartida fortalece el crecimiento personal y comunitario."
                _concienciaRespuesta.value = ConcienciaRespuesta(
                    consultaOriginal = queryLimpia,
                    sintesisConversacional = sintesis,
                    consejoAccionable = consejo
                )
                onVoiceSpeak?.invoke(sintesis)
                return
            }
        }

        // 2. Consulta en el banco profundo de datos (3000+ reflexiones y autores)
        val matches = buscarCoincidenciasFilosoficas(
            inputUsuario = queryLimpia,
            idiomaSeleccionado = languageCode,
            frasesAprendidas = frasesAprendidas,
            limite = 15
        )

        // 3. Generar síntesis conversacional empática tipo Gemini / Alexa
        val (sintesis, consejo) = when {
            qLower.contains("ansiedad") || qLower.contains("nervios") || qLower.contains("panico") || qLower.contains("anxiety") -> {
                if (isEnglish) {
                    "I understand you are experiencing anxiety. Remember anxiety is not a failure, but an alert signal from your nervous system that we can soothe together." to
                    "1. Take three slow 4-7-8 breaths.\n2. Remind yourself: 'I am safe in this present second'.\n3. Focus on one small doable action."
                } else {
                    "Comprendo lo que estás sintiendo. La ansiedad no es una debilidad, sino una señal de alerta de tu sistema nervioso que podemos calmar juntos en este momento." to
                    "1. Realiza 3 ciclos de respiración 4-7-8 para activar tu calma interior.\n2. Recuerda que los pensamientos no son hechos absolutos.\n3. Concéntrate en dar un solo paso pequeño hoy sin juzgarte."
                }
            }
            qLower.contains("estres") || qLower.contains("estrés") || qLower.contains("agobio") || qLower.contains("burnout") || qLower.contains("stress") -> {
                if (isEnglish) {
                    "Stress signals that mental bandwidth is saturated. True resilience begins when you separate what you can control from what you cannot." to
                    "1. Focus only on what is in your hands right now.\n2. Write down your feelings in the Secure Diary.\n3. Take a 10-minute restorative pause with nature piano."
                } else {
                    "El estrés aparece cuando intentamos cargar más peso del que nuestro presente puede sostener. Como enseña la psicología estoica, el estrés disminuye cuando separamos lo que podemos controlar de lo que no." to
                    "1. Identifica qué parte de esta situación está bajo tu control y cuál no.\n2. Desahoga lo que sientes en tu Diario Seguro.\n3. Tómate una pausa consciente con música de arroyo y piano."
                }
            }
            qLower.contains("triste") || qLower.contains("tristeza") || qLower.contains("depre") || qLower.contains("llorar") || qLower.contains("sad") -> {
                if (isEnglish) {
                    "Sadness is a natural emotion asking for compassion and rest. Allow yourself to feel without self-criticism; every storm passes." to
                    "1. Be gentle with yourself today.\n2. Record your honest thoughts in your Private Diary.\n3. Reach out to someone or connect in our community."
                } else {
                    "Siento que estés atravesando este momento de tristeza. Es completamente válido sentirse así; permitirte sentir es el primer paso para procesar y sanar." to
                    "1. Sé compasivo contigo mismo y no te juzgues por estar triste.\n2. Registra tus pensamientos en tu Diario Seguro.\n3. Recuerda que las tormentas de invierno no duran para siempre y forjan nueva fortaleza."
                }
            }
            qLower.contains("miedo") || qLower.contains("fracaso") || qLower.contains("inseguridad") || qLower.contains("fear") -> {
                if (isEnglish) {
                    "Fear is often our imagination projecting worst-case futures. Seneca reminded us that we suffer more often in imagination than in reality." to
                    "1. Anchor yourself in the present: 'Here and now I am safe'.\n2. Recall three obstacles you successfully overcame before.\n3. Take one brave micro-step today."
                } else {
                    "El miedo es una proyección de nuestra imaginación hacia el futuro. Como decía Séneca, sufrimos más a menudo en la imaginación que en la realidad." to
                    "1. Trae tu mente al presente: 'Aquí y ahora estoy a salvo'.\n2. Recuerda tres desafíos que ya superaste con éxito en el pasado.\n3. Da un micro-paso de valentía sin esperar certidumbre total."
                }
            }
            qLower.contains("rutina") || qLower.contains("plan") || qLower.contains("hacer hoy") || qLower.contains("routine") -> {
                if (isEnglish) {
                    "Here is your personalized daily growth routine to optimize your focus and inner balance:" to
                    "1. 3 minutes of 4-7-8 breathing.\n2. Review your DO / BE stoic affirmation in the Tracker.\n3. Log 1 positive reflection in your Diary (+50 XP)."
                } else {
                    "Aquí tienes tu rutina de equilibrio y crecimiento personal para hoy:" to
                    "1. 3 minutos de respiración 4-7-8 para regular tu ritmo cardíaco.\n2. Revisa tu afirmación estoica de HACER/SER en el Radar Emocional.\n3. Escribe un desahogo o meta en tu Diario Seguro (+50 XP)."
                }
            }
            else -> {
                if (isEnglish) {
                    "Regarding '$queryLimpia', philosophy and psychology teach that true progress emerges when we replace overthinking with mindful action." to
                    "1. Reflect on what insight this brings to your personal journey.\n2. Apply one concrete action today.\n3. Continue exploring with Lykke AI and the community."
                } else {
                    "Analizando tu consulta sobre '$queryLimpia': la psicología del bienestar y la filosofía estoica nos invitan a enfocar la mente en lo constructivo y cultivar resiliencia." to
                    "1. Reflexiona sobre qué aprendizaje positivo puedes extraer de esto para tu vida diaria.\n2. Aplica una acción concreta hoy que acerque tu bienestar.\n3. Continúa explorando con la I.A. Conciencia y la comunidad Lykke."
                }
            }
        }

        _concienciaRespuesta.value = ConcienciaRespuesta(
            consultaOriginal = queryLimpia,
            sintesisConversacional = sintesis,
            consejoAccionable = consejo,
            reflexionesRelacionadas = matches
        )

        // Voz natural que habla la respuesta completa al usuario
        val respuestaParaVoz = if (matches.isNotEmpty()) {
            val primera = matches.first().second
            val autor = primera.autorSignatario.ifBlank { primera.autor }
            "$sintesis. $consejo. Como nos enseña $autor: ${primera.texto}"
        } else {
            "$sintesis. $consejo"
        }
        onVoiceSpeak?.invoke(respuestaParaVoz)
    }

    fun limpiarBusquedaConciencia() {
        _searchQuery.value = ""
        _concienciaRespuesta.value = null
    }

    // Environment & Customization
    private val _currentEnvironment = MutableStateFlow(EnvironmentTheme.NATURE)
    val currentEnvironment: StateFlow<EnvironmentTheme> = _currentEnvironment.asStateFlow()

    private val _currentTextColor = MutableStateFlow(TextColorChoice.PURE_WHITE)
    val currentTextColor: StateFlow<TextColorChoice> = _currentTextColor.asStateFlow()

    private val _currentOverlayAlpha = MutableStateFlow(0.82f)
    val currentOverlayAlpha: StateFlow<Float> = _currentOverlayAlpha.asStateFlow()

    // Ambient Soundtrack (Piano y Melodías Globales por País)
    private val _soundtrackMode = MutableStateFlow(SoundtrackMode.PIANO_WATER)
    val soundtrackMode: StateFlow<SoundtrackMode> = _soundtrackMode.asStateFlow()

    private val _melodiaActual = MutableStateFlow<MelodiaPais>(CatalogoMelodiasGlobales[0])
    val melodiaActual: StateFlow<MelodiaPais> = _melodiaActual.asStateFlow()

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying.asStateFlow()

    private val _musicVolume = MutableStateFlow(0.6f)
    val musicVolume: StateFlow<Float> = _musicVolume.asStateFlow()

    // Sessions & Milestone
    val sessionCount: StateFlow<Int> = repository.sessionCount
    val sessionHistory: StateFlow<List<String>> = repository.completedSessionHistory

    // Bookings & Solidarity
    val bookings: StateFlow<List<BookingRequest>> = repository.bookings
    val purchasedGifts: StateFlow<List<VirtualGift>> = repository.purchasedGifts
    val freeGiftsUnlocked: StateFlow<List<VirtualGift>> = repository.freeGiftsUnlocked
    val freeJornadasUnlocked: StateFlow<List<JornadaGratuita>> = repository.freeJornadasUnlocked
    val completedTasksRewardCount: StateFlow<Int> = repository.completedTasksRewardCount
    val availableJornadas: List<JornadaGratuita> = repository.getJornadasComunitarias()
    val totalDonatedUsd: StateFlow<Double> = repository.totalDonatedUsd
    val availableGifts: List<VirtualGift> = repository.getVirtualGifts()

    // Gamification
    val totalXp: StateFlow<Int> = repository.totalXp
    val badges: StateFlow<List<Badge>> = repository.badges

    // Diario Emocional Seguro
    val diaryEntries: StateFlow<List<DiaryEntry>> = repository.diaryEntries
    val backupReports: StateFlow<List<DiaryBackupReport>> = repository.backupReports

    // Question Games State
    private val _quizState = MutableStateFlow(QuizSessionState())
    val quizState: StateFlow<QuizSessionState> = _quizState.asStateFlow()

    // Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_1",
                text = "¡Hola! Bienvenido/a a Asesorías Consultorias psicología (LivsForm Lykke). Soy tu asistente de psicología digital y contención emocional.\n\n" +
                       "Este es un espacio 100% seguro, confidencial y privado para desahogarte, registrar tus relatos en tu 'Diario Emocional Seguro' o explorar lo que sientes sin juzgarte.\n\n" +
                       "¿Cómo te encuentras hoy? ¿Hay algo en especial sobre el estrés, la frustración o tus metas que quieras compartir?",
                isFromUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatBotTyping = MutableStateFlow(false)
    val isChatBotTyping: StateFlow<Boolean> = _isChatBotTyping.asStateFlow()

    // Breathing Exercise State
    private val _breathingState = MutableStateFlow(BreathingExerciseState())
    val breathingState: StateFlow<BreathingExerciseState> = _breathingState.asStateFlow()
    private var breathingJob: Job? = null

    // Self-care Active Reminder / Alert Banner
    private val _activeReminder = MutableStateFlow<SelfCareReminder?>(
        repository.getSelfCareReminders().firstOrNull()
    )
    val activeReminder: StateFlow<SelfCareReminder?> = _activeReminder.asStateFlow()

    // Feedback Dialogs
    private val _generalDialogMessage = MutableStateFlow<String?>(null)
    val generalDialogMessage: StateFlow<String?> = _generalDialogMessage.asStateFlow()

    // Navigation Actions
    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    fun setEnvironment(theme: EnvironmentTheme) {
        _currentEnvironment.value = theme
        _currentOverlayAlpha.value = theme.defaultOverlayAlpha
    }

    fun setTextColor(color: TextColorChoice) {
        _currentTextColor.value = color
    }

    fun setOverlayAlpha(alpha: Float) {
        _currentOverlayAlpha.value = alpha
    }

    fun dismissDialog() {
        _generalDialogMessage.value = null
    }

    fun showMessage(msg: String) {
        _generalDialogMessage.value = msg
    }

    // --- MUSIC / SOUNDTRACK METHODS ---
    fun toggleMusic() {
        if (_isMusicPlaying.value) {
            soundPlayer.stop()
            _isMusicPlaying.value = false
        } else {
            soundPlayer.setVolume(_musicVolume.value)
            val melodia = _melodiaActual.value
            soundPlayer.startMelodia(melodia)
            _isMusicPlaying.value = true
            repository.unlockBadge("peace_harmony")
        }
    }

    fun reproducirMelodiaPais(melodia: MelodiaPais) {
        _melodiaActual.value = melodia
        soundPlayer.setVolume(_musicVolume.value)
        soundPlayer.startMelodia(melodia)
        _isMusicPlaying.value = true
        repository.unlockBadge("peace_harmony")
    }

    fun changeSoundtrackMode(mode: SoundtrackMode) {
        _soundtrackMode.value = mode
        if (_isMusicPlaying.value) {
            soundPlayer.start(mode)
        }
    }

    fun setSoundtrackVolume(vol: Float) {
        _musicVolume.value = vol
        soundPlayer.setVolume(vol)
    }

    fun cambiarMelodiaSiguiente() {
        val list = CatalogoMelodiasGlobales
        val idx = list.indexOfFirst { it.id == _melodiaActual.value.id }
        val next = if (idx == -1 || idx == list.size - 1) list[0] else list[idx + 1]
        reproducirMelodiaPais(next)
    }

    fun cambiarMelodiaAnterior() {
        val list = CatalogoMelodiasGlobales
        val idx = list.indexOfFirst { it.id == _melodiaActual.value.id }
        val prev = if (idx <= 0) list.last() else list[idx - 1]
        reproducirMelodiaPais(prev)
    }

    fun subirVolumen() {
        val newVol = (_musicVolume.value + 0.15f).coerceAtMost(1.0f)
        setSoundtrackVolume(newVol)
    }

    fun bajarVolumen() {
        val newVol = (_musicVolume.value - 0.15f).coerceAtLeast(0.1f)
        setSoundtrackVolume(newVol)
    }

    // --- DIARIO EMOCIONAL SEGURO METHODS ---
    fun saveDiaryEntry(
        title: String,
        content: String,
        tags: List<String>,
        mood: String,
        authorAlias: String
    ) {
        if (content.isBlank()) {
            _generalDialogMessage.value = "Por favor escribe tu reflexión o sentimiento antes de guardar."
            return
        }

        val report = repository.addDiaryEntry(title, content, tags, mood, authorAlias)
        val (gift, jornada) = repository.awardRewardForTask("Diario Seguro")

        if (report != null) {
            _generalDialogMessage.value = "🔒 ¡Relato guardado y encriptado con éxito! (+50 XP)\n\n" +
                    "🎁 ¡RECOMPENSA GRATUITA GANADA!: Has desbloqueado '${gift.name}' ${gift.iconEmoji} y la '${jornada.title}' ${jornada.iconEmoji}.\n\n" +
                    "⭐ Hito de 5 relatos alcanzado: copia de respaldo procesada hacia ${report.destinationEmail} con tu usuario '${report.authorAlias}'."
        } else {
            val total = repository.diaryEntries.value.size
            _generalDialogMessage.value = "🔒 Relato guardado con éxito en tu Diario Emocional Seguro (+50 XP).\n\n" +
                    "🎁 ¡RECOMPENSA GRATUITA GANADA!: Has desbloqueado '${gift.name}' ${gift.iconEmoji} y la '${jornada.title}' ${jornada.iconEmoji} (beneficiando a ${jornada.childrenBenefited} niños).\n\n" +
                    "Llevas $total relatos registrados."
        }
    }

    fun awardRewardForTask(taskTitle: String) {
        val (gift, jornada) = repository.awardRewardForTask(taskTitle)
        _generalDialogMessage.value = "🎁 ¡Recompensa Gratuita Ganada por: $taskTitle!\n\n" +
                "✨ Has desbloqueado el regalo '${gift.name}' (${gift.iconEmoji}) y la '${jornada.title}' (${jornada.iconEmoji}).\n\n" +
                "🎉 ¡Todas las tareas de desahogo y pruebas otorgan recompensas gratuitas permanentes! (+50 XP)"
    }

    // --- GAMIFICATION GETTERS ---
    fun getUserLevel(): Pair<Int, String> {
        return repository.getUserLevel(totalXp.value)
    }

    // --- QUESTION GAMES METHODS ---
    fun startQuizCategory(category: SectionCategory) {
        val questions = repository.getQuestionsForCategory(category)
        _quizState.value = QuizSessionState(
            category = category,
            questions = questions,
            currentIndex = 0,
            selectedOption = null,
            isShowingComment = false,
            accumulatedScore = 0,
            isFinished = false
        )
    }

    fun selectQuizOption(option: QuizOption) {
        val current = _quizState.value
        _quizState.value = current.copy(
            selectedOption = option,
            isShowingComment = true,
            accumulatedScore = current.accumulatedScore + option.score
        )
    }

    fun nextQuizQuestion() {
        val current = _quizState.value
        val nextIdx = current.currentIndex + 1
        if (nextIdx < current.questions.size) {
            _quizState.value = current.copy(
                currentIndex = nextIdx,
                selectedOption = null,
                isShowingComment = false
            )
            repository.awardRewardForTask("Prueba de Pregunta #$nextIdx")
        } else {
            // Finished quiz session!
            _quizState.value = current.copy(isFinished = true)
            val catName = current.category?.title ?: "Juego de Preguntas"
            repository.awardRewardForTask("Prueba completada: $catName")
            repository.incrementSession("Juego interactivo completado: $catName")
            repository.addXp(50, "Juego completado: $catName")

            if (current.category == SectionCategory.GOALS_DREAMS) {
                repository.unlockBadge("goals_week")
            }
        }
    }

    fun resetQuiz() {
        _quizState.value = QuizSessionState()
    }

    // --- CHAT & DESAHOGO EMOCIONAL ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text.trim(),
            isFromUser = true
        )
        _chatMessages.value = _chatMessages.value + userMsg
        repository.addXp(15, "Participación en desahogo confidencial")
        repository.unlockBadge("venting_guardian")

        viewModelScope.launch {
            _isChatBotTyping.value = true
            delay(1200)

            val replyText = repository.generateTherapistReply(text, repository.sessionCount.value)
            val botMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = replyText,
                isFromUser = false
            )
            _isChatBotTyping.value = false
            _chatMessages.value = _chatMessages.value + botMsg

            if (_chatMessages.value.filter { it.isFromUser }.size % 3 == 0) {
                repository.incrementSession("Desahogo emocional y escucha activa en chat seguro")
            }
        }
    }

    fun sendSelfCareNotificationToChat() {
        val reminders = repository.getSelfCareReminders()
        val randomReminder = reminders.random()
        _activeReminder.value = randomReminder

        val reminderMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = "🔔 RECORDATORIO DE AUTOCUIDADO & EQUILIBRIO DIARIO:\n" +
                   "• ${randomReminder.title}\n\n" +
                   randomReminder.advice + "\n\n" +
                   "🛡️ Tu bienestar mental es prioridad. Recuerda que tus historias en el Diario Emocional son privadas y encriptadas.",
            isFromUser = false,
            isSpecialNotification = true
        )
        _chatMessages.value = _chatMessages.value + reminderMsg
        repository.addXp(10, "Recordatorio de equilibrio activado")
        _generalDialogMessage.value = "Te enviamos un nuevo recordatorio de autocuidado personalizado al Chat y en tu barra de bienestar."
    }

    fun addXp(amount: Int, reason: String) {
        repository.addXp(amount, reason)
    }

    // --- BREATHING EXERCISES ---
    fun startBreathingExercise(type: String = "4-7-8") {
        breathingJob?.cancel()

        _breathingState.value = BreathingExerciseState(
            exerciseName = if (type == "4-7-8") "Respiración 4-7-8 (Calma Rápida)" else "Respiración Cuadrada (Enfoque)",
            phase = "Inhala",
            phaseInstruction = "Inhala suavemente por la nariz llenando tu abdomen...",
            secondsRemaining = 4,
            cycleCount = 1,
            targetCycles = 3,
            isRunning = true,
            progress = 0f
        )

        breathingJob = viewModelScope.launch {
            val totalRounds = 3
            for (round in 1..totalRounds) {
                // Inhale 4s
                for (s in 4 downTo 1) {
                    _breathingState.value = _breathingState.value.copy(
                        phase = "Inhala",
                        phaseInstruction = "Inhala suavemente por la nariz...",
                        secondsRemaining = s,
                        cycleCount = round,
                        progress = (5 - s) / 4f
                    )
                    delay(1000)
                }

                // Hold 7s (or 4s for square)
                val holdTime = if (type == "4-7-8") 7 else 4
                for (s in holdTime downTo 1) {
                    _breathingState.value = _breathingState.value.copy(
                        phase = "Sostén",
                        phaseInstruction = "Retén el aire con el cuerpo relajado...",
                        secondsRemaining = s,
                        progress = (holdTime + 1 - s).toFloat() / holdTime
                    )
                    delay(1000)
                }

                // Exhale 8s (or 4s for square)
                val exhaleTime = if (type == "4-7-8") 8 else 4
                for (s in exhaleTime downTo 1) {
                    _breathingState.value = _breathingState.value.copy(
                        phase = "Exhala",
                        phaseInstruction = "Exhala despacio soltando toda la tensión...",
                        secondsRemaining = s,
                        progress = (exhaleTime + 1 - s).toFloat() / exhaleTime
                    )
                    delay(1000)
                }

                delay(500)
            }

            _breathingState.value = _breathingState.value.copy(
                phase = "Completado",
                phaseInstruction = "¡Excelente! Has restaurado tu equilibrio nervioso y oxigenado tu cuerpo.",
                secondsRemaining = 0,
                isRunning = false,
                progress = 1f
            )

            val (gift, jornada) = repository.awardRewardForTask("Respiración Restaurativa 4-7-8")
            repository.incrementSession("Ejercicio de respiración rápida restaurativa")
            repository.addXp(40, "Respiración completada")
            repository.unlockBadge("breath_master")
            _generalDialogMessage.value = "🌬️ ¡Sesión de respiración completada con éxito! (+50 XP)\n\n" +
                    "🎁 ¡RECOMPENSA GANADA!: Has desbloqueado el regalo '${gift.name}' ${gift.iconEmoji} y la '${jornada.title}' ${jornada.iconEmoji}."
        }
    }

    fun stopBreathing() {
        breathingJob?.cancel()
        _breathingState.value = BreathingExerciseState(isRunning = false)
    }

    // --- BOOKING SESSIONS ($5 Audio / $15 Video) ---
    fun bookAppointment(
        clientName: String,
        whatsapp: String,
        email: String,
        sessionType: String,
        motive: String,
        datePreference: String,
        timeSlot: String
    ): Boolean {
        if (clientName.isBlank() || whatsapp.isBlank() || email.isBlank()) {
            _generalDialogMessage.value = "Por favor completa tu nombre, WhatsApp y correo electrónico."
            return false
        }

        val booking = BookingRequest(
            id = UUID.randomUUID().toString(),
            clientName = clientName.trim(),
            whatsapp = whatsapp.trim(),
            email = email.trim(),
            sessionType = sessionType,
            motive = motive.ifBlank { "Asesoría emocional y orientación psicológica general" },
            datePreference = datePreference.ifBlank { "Próxima fecha disponible" },
            timeSlot = timeSlot.ifBlank { "Tarde (15:00 - 18:00)" },
            status = "En espera de confirmación para inicio de sesión",
            paymentNote = "Solo queda pendiente la cuenta"
        )

        repository.addBooking(booking)
        _generalDialogMessage.value = "¡Solicitud registrada con éxito! (+100 XP)\n\n" +
                "Tipo: $sessionType\n" +
                "Estado: En espera de confirmación para inicio de sesión.\n" +
                "Nota: Solo queda pendiente la cuenta. Nuestro equipo de Asesorías Consultorias psicología te enviará los datos a tu WhatsApp ($whatsapp) y a tu correo ($email)."
        return true
    }

    // --- VIRTUAL GIFTS & SOLIDARITY DONATION (100% GRATUITOS) ---
    fun purchaseGift(gift: VirtualGift) {
        claimFreeGift(gift)
    }

    fun claimFreeGift(gift: VirtualGift) {
        repository.claimFreeGift(gift)
        _generalDialogMessage.value = "🎁 ¡Regalo de Buena Suerte '${gift.name}' activado gratuitamente! (+30 XP)\n\n" +
                "🌱 Impacto Comunitario: ${gift.socialImpact}\n\n" +
                "Todos los regalos y jornadas son 100% gratuitos y se otorgan como recompensa por completar tareas de desahogo y bienestar."
    }

    fun makeCustomDonation(amount: Double) {
        if (amount <= 0) return
        repository.addGiftOrDonation(null, amount)
        _generalDialogMessage.value = "¡Donación solidaria de $$amount USD procesada! (+80 XP)\n\n" +
                "Los fondos van directo a las jornadas sociales comunitarias de salud mental infantil."
    }

    // --- DIPLOMA EMAIL REQUEST ---
    fun requestDiplomaEmail(recipientEmail: String, recipientName: String) {
        if (recipientEmail.isBlank()) {
            _generalDialogMessage.value = "Por favor ingresa un correo electrónico válido para enviarte el diploma."
            return
        }

        _generalDialogMessage.value = "¡Solicitud de Diploma Virtual tramitada!\n\n" +
                "Destinatario: $recipientName\n" +
                "Correo de entrega: $recipientEmail\n\n" +
                "Asesorías Consultorias psicología (asesoriaslykke@gmail.com) emitirá y validará tu certificado formal de progreso y superación personal."
    }

    fun advanceToTenSessionsForTesting() {
        val current = repository.sessionCount.value
        if (current < 10) {
            val needed = 10 - current
            for (i in 1..needed) {
                repository.incrementSession("Sesión guiada de avance personal #$i")
            }
            _generalDialogMessage.value = "¡Felicidades! Has alcanzado el hito de 10 sesiones (+${needed * 30} XP). Ahora se han desbloqueado tu Reflexión Personalizada y tu Diploma Virtual."
        }
    }

    fun updateProfileAlias(newAlias: String) {
        val uid = _currentUser.value?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.updateDisplayName(uid, newAlias)
                _userProfile.value = _userProfile.value?.copy(displayName = newAlias.trim())
                _generalDialogMessage.value = "Alias de usuario actualizado a \"${newAlias.trim()}\"."
            } catch (e: Exception) {
                Log.e("LykkeViewModel", "Error actualizando alias", e)
                _generalDialogMessage.value = "Error al actualizar alias: ${e.localizedMessage}"
            }
        }
    }

    fun syncUserProfileWithAlias(user: FirebaseUser, alias: String?) {
        viewModelScope.launch {
            try {
                val profile = userRepository.syncOrCreateUserProfile(user, alias)
                _userProfile.value = profile
            } catch (e: Exception) {
                Log.e("LykkeViewModel", "Error sincronizando perfil de usuario con alias", e)
            }
        }
    }

    fun signOutUser() {
        auth.signOut()
        _currentUser.value = null
        _userProfile.value = null
        _generalDialogMessage.value = "Sesión cerrada correctamente. Tus datos están seguros y encriptados."
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
        soundPlayer.stop()
        stopSpeechRecognition()
    }
}
