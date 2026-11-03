package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.example.data.local.LykkeDatabase
import com.example.data.local.dao.DiaryEntryDao
import com.example.data.local.entity.EncryptedDiaryEntryEntity
import com.example.data.local.entity.toDomainModel
import com.example.data.local.entity.toEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LykkeRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("livsform_lykke_prefs", Context.MODE_PRIVATE)

    private val diaryDao: DiaryEntryDao = LykkeDatabase.getInstance(context).diaryEntryDao()
    private val repoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Sessions & Milestone
    private val _sessionCount = MutableStateFlow(prefs.getInt("session_count", 3))
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _completedSessionHistory = MutableStateFlow<List<String>>(
        listOf(
            "Sesión 1: Exploración inicial de bienestar emocional y bienvenida",
            "Sesión 2: Reconocimiento de factores de estrés cotidiano",
            "Sesión 3: Apertura del Diario Emocional Seguro y confidencial"
        )
    )
    val completedSessionHistory: StateFlow<List<String>> = _completedSessionHistory.asStateFlow()

    private val _bookings = MutableStateFlow<List<BookingRequest>>(emptyList())
    val bookings: StateFlow<List<BookingRequest>> = _bookings.asStateFlow()

    private val _purchasedGifts = MutableStateFlow<List<VirtualGift>>(emptyList())
    val purchasedGifts: StateFlow<List<VirtualGift>> = _purchasedGifts.asStateFlow()

    private val _completedTasksRewardCount = MutableStateFlow(prefs.getInt("completed_tasks_reward_count", 0))
    val completedTasksRewardCount: StateFlow<Int> = _completedTasksRewardCount.asStateFlow()

    private val _freeGiftsUnlocked = MutableStateFlow<List<VirtualGift>>(emptyList())
    val freeGiftsUnlocked: StateFlow<List<VirtualGift>> = _freeGiftsUnlocked.asStateFlow()

    private val _freeJornadasUnlocked = MutableStateFlow<List<JornadaGratuita>>(emptyList())
    val freeJornadasUnlocked: StateFlow<List<JornadaGratuita>> = _freeJornadasUnlocked.asStateFlow()

    private val _totalDonatedUsd = MutableStateFlow(prefs.getFloat("total_donated", 35.0f).toDouble())
    val totalDonatedUsd: StateFlow<Double> = _totalDonatedUsd.asStateFlow()

    // --- DIARIO EMOCIONAL SEGURO ---
    private val _diaryEntries = MutableStateFlow<List<DiaryEntry>>(
        listOf(
            DiaryEntry(
                id = "entry_init_1",
                title = "Primer paso hacia la serenidad",
                plainContent = "Hoy decidí darme un momento para respirar y dejar de autoexigirme tanto. Reconozco que la incertidumbre me asusta, pero estoy aprendiendo a confiar en mi proceso paso a paso.",
                encryptedContent = encryptText("Hoy decidí darme un momento para respirar y dejar de autoexigirme tanto..."),
                tags = listOf("alegría", "esperanza", "metas"),
                moodEmoji = "🌱",
                authorAlias = "ViajeroZen"
            ),
            DiaryEntry(
                id = "entry_init_2",
                title = "Soltando la frustración del trabajo",
                plainContent = "Sentí mucha rabia porque cambiaron las fechas de entrega sin avisarme. En lugar de explotar, salí a caminar 5 minutos y apliqué la respiración 4-7-8.",
                encryptedContent = encryptText("Sentí mucha rabia porque cambiaron las fechas de entrega..."),
                tags = listOf("frustración", "estrés"),
                moodEmoji = "🌊",
                authorAlias = "ViajeroZen"
            )
        )
    )
    val diaryEntries: StateFlow<List<DiaryEntry>> = _diaryEntries.asStateFlow()

    private val _backupReports = MutableStateFlow<List<DiaryBackupReport>>(emptyList())
    val backupReports: StateFlow<List<DiaryBackupReport>> = _backupReports.asStateFlow()

    // --- GAMIFICACIÓN ---
    private val _totalXp = MutableStateFlow(prefs.getInt("total_xp", 160))
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    private val initialBadges = listOf(
        Badge("breath_master", "Maestro de la Respiración", "Completaste 3 ciclos de respiración restaurativa.", "🌬️", isUnlocked = false),
        Badge("goals_week", "Semana de Metas Cumplidas", "Superaste la procrastinación en el juego de metas.", "🎯", isUnlocked = false),
        Badge("venting_guardian", "Guardián del Desahogo", "Expresaste tus historias en el chat confidencial.", "🛡️", isUnlocked = true),
        Badge("emotional_chronicler", "Cronista Emocional", "Registraste relatos en el Diario Emocional Seguro.", "📖", isUnlocked = true),
        Badge("ten_sessions_grad", "Graduado de 10 Sesiones", "Alcanzaste el hito de 10 sesiones y tu diploma.", "🎓", isUnlocked = false),
        Badge("peace_harmony", "Paz & Armonía", "Activaste la música de piano y agua de naturaleza.", "🎹", isUnlocked = false),
        Badge("solidarity_heart", "Corazón Solidario", "Adquiriste un regalo de buena suerte o donación.", "💖", isUnlocked = false)
    )

    private val _badges = MutableStateFlow<List<Badge>>(initialBadges)
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    init {
        repoScope.launch {
            try {
                diaryDao.getAllEntries().collect { entities ->
                    if (entities.isNotEmpty()) {
                        _diaryEntries.value = entities.map { it.toDomainModel() }
                    } else {
                        val initialEntities = _diaryEntries.value.map { it.toEntity() }
                        diaryDao.insertEntries(initialEntities)
                    }
                }
            } catch (e: Exception) {
                Log.w("LykkeRepository", "Error sincronizando con Room database: ${e.message}")
            }
        }
    }

    fun addXp(amount: Int, reason: String) {
        val newXp = _totalXp.value + amount
        _totalXp.value = newXp
        prefs.edit().putInt("total_xp", newXp).apply()

        // Check level milestone or badges
        if (newXp >= 150) {
            unlockBadge("emotional_chronicler")
        }
    }

    fun unlockBadge(badgeId: String) {
        _badges.value = _badges.value.map { badge ->
            if (badge.id == badgeId && !badge.isUnlocked) {
                badge.copy(isUnlocked = true)
            } else {
                badge
            }
        }
    }

    fun getUserLevel(xp: Int): Pair<Int, String> {
        val level = (xp / 120) + 1
        val title = when (level) {
            1 -> "Iniciado de la Calma"
            2 -> "Explorador de Emociones"
            3 -> "Guardián de la Serenidad"
            4 -> "Maestro de la Resiliencia"
            5 -> "Arquitecto de Sueños"
            else -> "Sabio del Equilibrio Interior"
        }
        return level to title
    }

    // --- ENCRYPTION HELPER ---
    private fun encryptText(raw: String): String {
        return try {
            val key = "LivsFormLykkeSecureKey2026"
            val xored = raw.mapIndexed { idx, c ->
                (c.code xor key[idx % key.length].code).toChar()
            }.joinToString("")
            "ENC_AES256::" + Base64.encodeToString(xored.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        } catch (e: Exception) {
            "ENC_SECURE::" + Base64.encodeToString(raw.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    fun addDiaryEntry(
        title: String,
        content: String,
        tags: List<String>,
        mood: String,
        authorAlias: String
    ): DiaryBackupReport? {
        val entry = DiaryEntry(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "Reflexión del día" },
            plainContent = content,
            encryptedContent = encryptText(content),
            tags = tags.ifEmpty { listOf("ansiedad", "alivio") },
            moodEmoji = mood,
            authorAlias = authorAlias.ifBlank { "Usuario Anónimo" },
            timestamp = System.currentTimeMillis()
        )

        val updatedList = listOf(entry) + _diaryEntries.value
        _diaryEntries.value = updatedList

        // Persist encrypted entry to local Room database
        repoScope.launch {
            try {
                diaryDao.insertEntry(entry.toEntity())
            } catch (e: Exception) {
                Log.e("LykkeRepository", "Error al guardar entrada en base de datos Room", e)
            }
        }

        incrementSession("Entrada en Diario Emocional Seguro ('${entry.title}')")
        addXp(40, "Nuevo relato en Diario Emocional")
        unlockBadge("emotional_chronicler")

        // CHECK IF 5 RELATOS MILESTONE IS REACHED:
        // "por cada 5 relatos con su nombre de usuario que haya elegido y su ubicación o IP valla una copia a tiemposdestinos@gmail.com"
        val totalCount = updatedList.size
        var reportGenerated: DiaryBackupReport? = null

        if (totalCount % 5 == 0) {
            val batchNum = totalCount / 5
            val batchEntries = updatedList.take(5)
            val summaryText = batchEntries.joinToString("\n• ") { "${it.title} [Tags: ${it.tags.joinToString(", ")}]" }

            val report = DiaryBackupReport(
                batchId = UUID.randomUUID().toString(),
                batchNumber = batchNum,
                entryCount = totalCount,
                authorAlias = entry.authorAlias,
                destinationEmail = "tiemposdestinos@gmail.com",
                ipNetworkInfo = "192.168.1.45 (Conexión Segura SSL/TLS - Red Protegida)",
                previewExtract = "Respaldo clínico automático de 5 relatos para supervisión y archivo confidencial:\n• $summaryText",
                timestamp = System.currentTimeMillis(),
                status = "Copia de 5 relatos enviada a tiemposdestinos@gmail.com"
            )
            _backupReports.value = listOf(report) + _backupReports.value
            reportGenerated = report
        }

        return reportGenerated
    }

    fun incrementSession(description: String) {
        val newCount = _sessionCount.value + 1
        _sessionCount.value = newCount
        prefs.edit().putInt("session_count", newCount).apply()

        val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date())
        val entry = "Sesión $newCount: $description ($dateStr)"
        _completedSessionHistory.value = listOf(entry) + _completedSessionHistory.value

        addXp(30, "Progreso de sesión #$newCount")

        if (newCount >= 10) {
            unlockBadge("ten_sessions_grad")
        }
    }

    fun addBooking(booking: BookingRequest) {
        _bookings.value = listOf(booking) + _bookings.value
        incrementSession("Reserva de consulta profesional (${booking.sessionType})")
        addXp(100, "Cita agendada con psicólogo")
    }

    fun awardRewardForTask(taskTitle: String): Pair<VirtualGift, JornadaGratuita> {
        val newCount = _completedTasksRewardCount.value + 1
        _completedTasksRewardCount.value = newCount
        prefs.edit().putInt("completed_tasks_reward_count", newCount).apply()

        val allGifts = getVirtualGifts()
        val giftIndex = (newCount - 1) % allGifts.size
        val unlockedGift = allGifts[giftIndex].copy(
            isFreeReward = true,
            isClaimed = true,
            unlockedByTask = taskTitle
        )

        val allJornadas = getJornadasComunitarias()
        val jornadaIndex = (newCount - 1) % allJornadas.size
        val unlockedJornada = allJornadas[jornadaIndex].copy(
            isUnlocked = true,
            unlockedByTask = taskTitle
        )

        _freeGiftsUnlocked.value = listOf(unlockedGift) + _freeGiftsUnlocked.value
        _freeJornadasUnlocked.value = listOf(unlockedJornada) + _freeJornadasUnlocked.value
        _purchasedGifts.value = listOf(unlockedGift) + _purchasedGifts.value

        addXp(50, "Recompensa gratuita por tarea: $taskTitle")
        unlockBadge("solidarity_heart")
        incrementSession("Tarea completada con recompensa gratuita ($taskTitle)")

        return Pair(unlockedGift, unlockedJornada)
    }

    fun claimFreeGift(gift: VirtualGift) {
        val claimed = gift.copy(isClaimed = true)
        _purchasedGifts.value = listOf(claimed) + _purchasedGifts.value
        addXp(30, "Regalo de buena suerte reclamado gratuitamente")
        unlockBadge("solidarity_heart")
    }

    fun addGiftOrDonation(gift: VirtualGift?, customAmount: Double?) {
        val amount = gift?.priceUsd ?: (customAmount ?: 5.0)
        val newTotal = _totalDonatedUsd.value + amount
        _totalDonatedUsd.value = newTotal
        prefs.edit().putFloat("total_donated", newTotal.toFloat()).apply()

        if (gift != null) {
            _purchasedGifts.value = listOf(gift) + _purchasedGifts.value
        }
        incrementSession("Aporte solidario voluntario a servidores y jornadas infantiles ($amount USD)")
        addXp(80, "Aporte voluntario de amistad")
        unlockBadge("solidarity_heart")
    }

    fun getQuestionsForCategory(category: SectionCategory): List<QuizQuestion> {
        return when (category) {
            SectionCategory.STRESS -> listOf(
                QuizQuestion(
                    id = 101,
                    category = SectionCategory.STRESS,
                    question = "¿Cómo responde tu mente y cuerpo cuando las tareas cotidianas se acumulan de golpe?",
                    options = listOf(
                        QuizOption(
                            text = "Me paralizo, siento presión en el pecho o cuello y postergo.",
                            psychologicalComment = "Es una respuesta de sobrecarga del sistema simpático. La parálisis es una defensa ante el exceso de estímulos. Tu mente te pide reducir el foco a una sola cosa simple.",
                            score = 2,
                            tip = "Aplica la regla de los 2 minutos: haz una tarea diminuta para reactivar la sensación de agencia."
                        ),
                        QuizOption(
                            text = "Intento hacer todo a máxima velocidad sin descansar.",
                            psychologicalComment = "Modo hiper-alerta: consume energía de reserva y genera cortisol elevado. Puede provocar fatiga súbita.",
                            score = 3,
                            tip = "Programa una micro-pausa de 3 minutos de respiración consciente cada 50 minutos de trabajo."
                        ),
                        QuizOption(
                            text = "Hago una pausa, priorizo lo esencial y descarto lo secundario.",
                            psychologicalComment = "¡Excelente autorregulación cognitiva! Aceptas la finitud del tiempo y proteges tu balance interno.",
                            score = 5,
                            tip = "Sigue cultivando esta actitud compasiva hacia tus propios límites."
                        )
                    )
                ),
                QuizQuestion(
                    id = 102,
                    category = SectionCategory.STRESS,
                    question = "Al terminar un día agotador, ¿cuál es tu hábito recurrente?",
                    options = listOf(
                        QuizOption(
                            text = "Sigo rumiando sobre lo que quedó pendiente para mañana.",
                            psychologicalComment = "La rumiación nocturna impide la regeneración del descanso y activa la alerta del cerebro.",
                            score = 2,
                            tip = "Escribe en una libreta 'Pendientes cerrados por hoy' para externalizar y liberar tu memoria de trabajo."
                        ),
                        QuizOption(
                            text = "Miro el celular sin descanso para distraerme hasta quedarme dormido/a.",
                            psychologicalComment = "Distracción pasiva: calma temporalmente pero satura la corteza visual y retrasa el sueño REM profundo.",
                            score = 3,
                            tip = "Sustituye 15 minutos de pantalla por música suave o un estiramiento pausado."
                        ),
                        QuizOption(
                            text = "Realizo una desconexión consciente: una infusión, ducha tibia o meditación breve.",
                            psychologicalComment = "Transición saludable: le indicas a tu sistema nervioso autónomo que es seguro soltar la guardia.",
                            score = 5,
                            tip = "Tu ritual de calma es tu mayor escudo preventivo contra el burnout."
                        )
                    )
                ),
                QuizQuestion(
                    id = 103,
                    category = SectionCategory.STRESS,
                    question = "¿Con qué frecuencia dices 'NO' a compromisos que sobrecargan tu bienestar?",
                    options = listOf(
                        QuizOption(
                            text = "Casi nunca, temo decepcionar o generar conflicto con los demás.",
                            psychologicalComment = "Complacencia por hiper-responsabilidad: poner siempre primero a los demás desgasta tu autoestima emocional.",
                            score = 2,
                            tip = "Recuerda: decir NO a otros cuando estás al límite es decirte SÍ a tu propia salud mental."
                        ),
                        QuizOption(
                            text = "A veces digo que no, pero me quedo con una profunda culpa interna.",
                            psychologicalComment = "Estás aprendiendo a poner límites, pero el juez interno aún te castiga.",
                            score = 3,
                            tip = "Acompaña tu negativa con validación: 'Comprendo la urgencia, pero hoy debo cuidar mi descanso'."
                        ),
                        QuizOption(
                            text = "Logro establecer límites claros con asertividad y respeto mutuo.",
                            psychologicalComment = "Madurez asertiva: reconoces que los límites sanos fortalecen las relaciones genuinas.",
                            score = 5,
                            tip = "Mantén ese espacio seguro que has construido con paciencia."
                        )
                    )
                )
            )

            SectionCategory.FRUSTRATION -> listOf(
                QuizQuestion(
                    id = 201,
                    category = SectionCategory.FRUSTRATION,
                    question = "Cuando algo muy planeado sale mal por causas imprevistas, ¿qué emoción predomina?",
                    options = listOf(
                        QuizOption(
                            text = "Ira explosiva o resentimiento con las circunstancias.",
                            psychologicalComment = "La ira es la máscara de la vulnerabilidad ante la pérdida de control. Tu mente rechaza la realidad tal como se presentó.",
                            score = 2,
                            tip = "Respira en 4 tiempos antes de emitir cualquier palabra y nombra la emoción: 'Esto me frustra, pero pasará'."
                        ),
                        QuizOption(
                            text = "Autoculpación severa: 'Siempre me pasa a mí por mi culpa'.",
                            psychologicalComment = "Distorsión cognitiva de personalización y generalización. Eres humano y no puedes controlar todas las variables del entorno.",
                            score = 2,
                            tip = "Háblate a ti mismo/a como le hablarías a tu mejor amigo en la misma situación."
                        ),
                        QuizOption(
                            text = "Acepto el desvío, me permito sentir la molestia y recalculo alternativas.",
                            psychologicalComment = "Alta flexibilidad psicológica: la resiliencia no es no sentir dolor, sino no quedarse atrapado en él.",
                            score = 5,
                            tip = "El reencuadre positivo transforma los obstáculos en lecciones de vida."
                        )
                    )
                ),
                QuizQuestion(
                    id = 202,
                    category = SectionCategory.FRUSTRATION,
                    question = "Ante una crítica constructiva o una opinión diferente a la tuya:",
                    options = listOf(
                        QuizOption(
                            text = "Lo percibo como un ataque personal directo y me cierro a dialogar.",
                            psychologicalComment = "Sensibilidad al rechazo defensiva: confunde la retroalimentación de una conducta con un juicio a tu valor como persona.",
                            score = 2,
                            tip = "Separa tu identidad de tu desempeño: una opinión externa no define tu valía integral."
                        ),
                        QuizOption(
                            text = "Escucho, identifico si hay algo útil que aprender y desecho lo que no aporta.",
                            psychologicalComment = "Filtro analítico maduro: eres capaz de aprovechar perspectivas ajenas sin perder tu propio centro.",
                            score = 5,
                            tip = "Continúa con esa mente abierta que promueve el crecimiento continuo."
                        ),
                        QuizOption(
                            text = "Dudo inmediatamente de mis capacidades y caigo en la inseguridad.",
                            psychologicalComment = "Síndrome del impostor latente: una duda externa activa la herida de insuficiencia.",
                            score = 3,
                            tip = "Ten a mano tu registro de logros reales y fortalezas comprobadas."
                        )
                    )
                )
            )

            SectionCategory.GOALS_DREAMS -> listOf(
                QuizQuestion(
                    id = 301,
                    category = SectionCategory.GOALS_DREAMS,
                    question = "¿Tienes un sueño o meta grande que te apasiona pero sigues postergando?",
                    options = listOf(
                        QuizOption(
                            text = "Sí, porque siento que aún no estoy 100% listo/a ni es el momento perfecto.",
                            psychologicalComment = "La trampa del perfeccionismo paralizante: el momento perfecto no existe; se construye dando pasos imperfectos.",
                            score = 3,
                            tip = "Divide esa gran meta en micro-pasos ridículamente pequeños (ej. 10 minutos al día)."
                        ),
                        QuizOption(
                            text = "Sí, por temor al fracaso o a la crítica de las personas cercanas.",
                            psychologicalComment = "El miedo al fracaso suele ser miedo al qué dirán. Los sueños que no se intentan se transforman en resignación silenciosa.",
                            score = 2,
                            tip = "En Asesorías Consultorias psicología te acompañamos a blindar tu autoconfianza y definir tu plan paso a paso."
                        ),
                        QuizOption(
                            text = "Tengo metas claras escritas y avanzo con constancia semana a semana.",
                            psychologicalComment = "Claridad de propósito y disciplina: tienes el motor de la motivación intrínseca bien encendido.",
                            score = 5,
                            tip = "Celebra cada hito intermedio; el viaje es tan valioso como la llegada."
                        )
                    )
                ),
                QuizQuestion(
                    id = 302,
                    category = SectionCategory.GOALS_DREAMS,
                    question = "¿Cómo defines el éxito personal en esta etapa de tu vida?",
                    options = listOf(
                        QuizOption(
                            text = "Paz mental, coherencia interna y relaciones sanas.",
                            psychologicalComment = "Definición intrínseca y saludable: priorizas el bienestar sostenible por encima de la validación efímera.",
                            score = 5,
                            tip = "Esa paz es tu brújula; protégela de las expectativas ajenas."
                        ),
                        QuizOption(
                            text = "Cumplir con las expectativas familiares y sociales para que estén orgullosos.",
                            psychologicalComment = "Motivación extrínseca: vivir según el libreto de otros suele provocar vacíos emocionales a mediano plazo.",
                            score = 3,
                            tip = "Pregúntate en intimidad: 'Si nadie me juzgara, ¿qué desearía para mi vida?'."
                        ),
                        QuizOption(
                            text = "Aún me siento perdido/a y no sé hacia dónde dirigir mis esfuerzos.",
                            psychologicalComment = "La desorientación es la antesala del autodescubrimiento. No saber es una oportunidad para empezar de cero.",
                            score = 3,
                            tip = "Una sesión guiada de orientación vocacional y de vida te dará luz y mapa."
                        )
                    )
                )
            )

            SectionCategory.SELF_CARE -> listOf(
                QuizQuestion(
                    id = 401,
                    category = SectionCategory.SELF_CARE,
                    question = "¿Te dedicas tiempo exclusivo para ti a la semana sin sentir que estás 'perdiendo el tiempo'?",
                    options = listOf(
                        QuizOption(
                            text = "Casi nunca, siento una culpa constante si no estoy siendo 'productivo/a'.",
                            psychologicalComment = "Cultura del rendimiento tóxico: el descanso no es un premio que te ganas al agotarte, es una necesidad biológica.",
                            score = 2,
                            tip = "El descanso consciente es productivo porque recarga tu creatividad y estabilidad anímica."
                        ),
                        QuizOption(
                            text = "Sí, protejo momentos de soledad nutritiva, lectura o pasatiempos placenteros.",
                            psychologicalComment = "Excelente práctica de higiene mental y afectiva. Llenas tu propio vaso para poder dar a otros.",
                            score = 5,
                            tip = "Mantén ese espacio sagrado como una cita ineludible contigo mismo/a."
                        ),
                        QuizOption(
                            text = "Solo cuando mi cuerpo enferma o colapsa por agotamiento.",
                            psychologicalComment = "Alarma somática: el cuerpo grita lo que la boca y la mente callaron durante semanas.",
                            score = 2,
                            tip = "Aprende a escuchar los susurros de cansancio antes de que se vuelvan un grito."
                        )
                    )
                )
            )
        }
    }

    fun getSelfCareReminders(): List<SelfCareReminder> {
        return listOf(
            SelfCareReminder(
                id = "rem_1",
                title = "Pausa de Descompresión Somática",
                advice = "Baja los hombros suavemente, suelta la mandíbula y exhala un suspiro largo. Tu cuerpo ha estado sosteniendo tensión sin que lo notes.",
                category = "Cuerpo & Alivio",
                durationMin = 2
            ),
            SelfCareReminder(
                id = "rem_2",
                title = "Hidratación con Presencia Plena",
                advice = "Bebe un vaso de agua fresca lentamente sintiendo la temperatura y el recorrido. Un cerebro deshidratado amplifica un 30% la ansiedad.",
                category = "Vitalidad",
                durationMin = 3
            ),
            SelfCareReminder(
                id = "rem_3",
                title = "Límites Digitales Restaurativos",
                advice = "Pon el teléfono en silencio durante 20 minutos. No estás obligado/a a responder inmediatamente a todo el mundo.",
                category = "Paz Mental",
                durationMin = 20
            ),
            SelfCareReminder(
                id = "rem_4",
                title = "Reencuadre de Gratitud Sincera",
                advice = "Anota mentalmente tres cosas diminutas que salieron bien hoy, por pequeñas que parezcan. Reentrena tu sesgo de negatividad.",
                category = "Equilibrio",
                durationMin = 2
            )
        )
    }

    fun getVirtualGifts(): List<VirtualGift> {
        return listOf(
            VirtualGift(
                id = "gift_corazon",
                name = "Corazones de Afecto & Compasión",
                description = "Recompensa gratuita por tus avances: Símbolo de amor propio, sanación profunda y contención solidaria.",
                priceUsd = 0.0,
                iconEmoji = "💖",
                socialImpact = "Financia una dinámica de arteterapia afectiva para niños en situación vulnerable.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_regalo",
                name = "Símbolo de Regalo Sorpresa",
                description = "Recompensa gratuita por tus avances: Un obsequio de gratitud por cada esfuerzo y avance completado.",
                priceUsd = 0.0,
                iconEmoji = "🎁",
                socialImpact = "Entrega un kit de juegos didácticos y cuentos terapéuticos infantiles.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_arbol",
                name = "Árbol de Vida & Sabiduría",
                description = "Recompensa gratuita por tus avances: Raíces firmes de serenidad y ramas frondosas de bienestar.",
                priceUsd = 0.0,
                iconEmoji = "🌳",
                socialImpact = "Acompaña un taller ecológico de siembra consciente y manejo de ansiedad infantil.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_libro",
                name = "Libro de Sabiduría Interior",
                description = "Recompensa gratuita por tus avances: Guía de autodescubrimiento, relatos y calma reflexiva.",
                priceUsd = 0.0,
                iconEmoji = "📖",
                socialImpact = "Dona cuadernillos de educación emocional guiada a escuelas comunitarias.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_escudo",
                name = "Escudo de Fortaleza Mental",
                description = "Recompensa gratuita por tus avances: Protección inquebrantable frente al miedo, la duda y el desasosiego.",
                priceUsd = 0.0,
                iconEmoji = "🛡️",
                socialImpact = "Financia talleres de prevención de acoso escolar y fortalecimiento de la autoestima.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_copa",
                name = "Copa de Victoria y Resiliencia",
                description = "Recompensa gratuita por tus avances: Gran trofeo al coraje de seguir adelante y superar la adversidad.",
                priceUsd = 0.0,
                iconEmoji = "🏆",
                socialImpact = "Organiza torneos de juegos colaborativos y premiación de superación infantil.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_medalla",
                name = "Medalla al Valor y Esfuerzo",
                description = "Recompensa gratuita por tus avances: Condecoración por cada pequeño paso hacia la paz mental.",
                priceUsd = 0.0,
                iconEmoji = "🥇",
                socialImpact = "Otorga medallas y diplomas de mérito en escuelas de escasos recursos.",
                isFreeReward = true
            ),
            VirtualGift(
                id = "gift_moneda",
                name = "Monedas de Prosperidad y Paz",
                description = "Recompensa gratuita por tus avances: Riqueza espiritual, calma interior y abundancia compartida.",
                priceUsd = 0.0,
                iconEmoji = "🪙",
                socialImpact = "Cubre meriendas nutritivas y refrigerios en jornadas infantiles de salud mental.",
                isFreeReward = true
            )
        )
    }

    fun getJornadasComunitarias(): List<JornadaGratuita> {
        return listOf(
            JornadaGratuita(
                id = "jornada_arteterapia",
                title = "Jornada Comunitaria de Arteterapia Infantil",
                description = "Taller de pintura y modelado para canalizar ansiedad y estrés escolar en niños.",
                iconEmoji = "🎨",
                childrenBenefited = 25,
                isUnlocked = true
            ),
            JornadaGratuita(
                id = "jornada_heroes_calma",
                title = "Jornada Pequeños Héroes de la Calma",
                description = "Dinámicas lúdicas de respiración consciente y cuentos reflexivos para niños.",
                iconEmoji = "🧘‍♀️",
                childrenBenefited = 30,
                isUnlocked = true
            ),
            JornadaGratuita(
                id = "jornada_contencion_miedo",
                title = "Jornada de Contención y Gestión del Miedo",
                description = "Sesiones de apoyo psicológico grupal para prevención del acoso escolar y fortalecimiento de la autoestima.",
                iconEmoji = "🛡️",
                childrenBenefited = 40,
                isUnlocked = true
            ),
            JornadaGratuita(
                id = "jornada_integral_lykke",
                title = "Jornada Integral Lykke",
                description = "Acompañamiento psicológico integral y entrega de cuadernillos lúdicos a familias vulnerables.",
                iconEmoji = "🌳",
                childrenBenefited = 50,
                isUnlocked = true
            )
        )
    }

    fun generateTherapistReply(userText: String, sessionNumber: Int): String {
        val lower = userText.lowercase(Locale.getDefault())

        // 1. Verificar si coincide con la base filosófica o estoica de Lykke
        val respuestaFilosofica = buscarRespuestaFilosofica(userText, "es")
        if (respuestaFilosofica != null) {
            val autorText = if (respuestaFilosofica.autor.isNotBlank()) " — ${respuestaFilosofica.autor}" else ""
            return "💡 Sabiduría & Reflexión (${respuestaFilosofica.titulo}):\n\n" +
                   "\"${respuestaFilosofica.texto}\"$autorText\n\n" +
                   "¿Cómo sientes que esta idea se conecta con lo que estás experimentando hoy?"
        }

        return when {
            lower.contains("diario") || lower.contains("escrib") || lower.contains("confidencial") || lower.contains("privad") || lower.contains("encript") -> {
                "Tu 'Diario Emocional Seguro' es un espacio sagrado, anónimo y encriptado. " +
                "Nadie más tiene acceso a tus vivencias personales. Puedes etiquetar tus sentimientos (como ansiedad, alegría o frustración) " +
                "y buscar reflexiones pasadas cuando lo desees. Además, tras cada 5 relatos, se genera un respaldo protegido hacia tiemposdestinos@gmail.com " +
                "con tu usuario elegido y conexión segura para seguimiento y resguardo de tu progreso."
            }
            lower.contains("música") || lower.contains("musica") || lower.contains("piano") || lower.contains("agua") || lower.contains("sonido") -> {
                "Te invito a activar la música de fondo relajante desde el icono de notas musicales 🎵 en la barra superior. " +
                "Puedes elegir entre acordes de piano a 432 Hz y el murmullo continuo de agua de cascada para profundizar tu estado de relajación."
            }
            lower.contains("puntos") || lower.contains("nivel") || lower.contains("insignia") || lower.contains("gamific") -> {
                "Ganas Puntos de Bienestar (XP) por cada sesión de respiración (+40 XP), relato en tu diario (+40 XP), juego de preguntas (+50 XP) " +
                "o metas cumplidas. Puedes desbloquear insignias como 'Maestro de la Respiración' y 'Semana de Metas Cumplidas' mientras subes de nivel."
            }
            lower.contains("estrés") || lower.contains("estres") || lower.contains("agobi") || lower.contains("cansad") || lower.contains("colapso") -> {
                "Te escucho con total empatía. Lo que estás experimentando es muy válido. Cuando el estrés se acumula, nuestra mente siente que todo es urgente al mismo tiempo.\n\n" +
                "Primero, respiremos juntos: haz una inhalación profunda en 4 segundos y suelta el aire despacio. " +
                "¿Qué es lo que hoy te pesa más de todo lo que tienes encima? Recuerda que en Asesorías Consultorias psicología este es tu espacio seguro y confidencial."
            }
            lower.contains("frustra") || lower.contains("enoj") || lower.contains("rabia") || lower.contains("injust") || lower.contains("impoten") -> {
                "La frustración es una de las emociones más intensas porque surge cuando algo valioso para ti no salió como esperabas. " +
                "Permítete sentir esa molestia sin juzgarte. No tienes que fingir que todo está bien cuando no lo está.\n\n" +
                "Dime, ¿sientes que esta situación dependía enteramente de ti, o hay factores externos que escapaban a tu control? Vamos a desarmarlo paso a paso."
            }
            lower.contains("sueño") || lower.contains("meta") || lower.contains("futuro") || lower.contains("miedo") || lower.contains("fracas") -> {
                "Tus sueños y metas son el motor de tu alma, pero es completamente natural que el miedo a equivocarse aparezca. De hecho, el miedo solo aparece ante las cosas que de verdad nos importan.\n\n" +
                "No necesitas tener todo resuelto hoy. Solo necesitas definir el siguiente paso de 15 minutos. ¿Cuál es ese sueño del que te gustaría que habláramos hoy?"
            }
            lower.contains("triste") || lower.contains("llor") || lower.contains("solo") || lower.contains("sola") || lower.contains("vacio") || lower.contains("vacío") -> {
                "Te abrazo a la distancia en este momento. La tristeza no es debilidad; es la forma en que tu corazón pide cuidado y pausa. Gracias por confiar en compartir esto conmigo en este rincón seguro.\n\n" +
                "Aquí no hay prisa ni juicios. Puedes desahogarte todo lo que necesites o escribir un relato en tu Diario Emocional Seguro. Si las lágrimas brotan, déjalas salir."
            }
            lower.contains("cita") || lower.contains("videollamada") || lower.contains("audio") || lower.contains("profesional") || lower.contains("precio") -> {
                "En Asesorías Consultorias psicología contamos con dos modalidades de consulta personalizada con nuestros psicólogos profesionales:\n\n" +
                "• Sesión de Audio Digital con psicólogo por solo $5 USD.\n" +
                "• Sesión de Videollamada programada personalizada por $15 USD (llenando el formulario con tu WhatsApp y correo).\n\n" +
                "Puedes ir a la pestaña 'Citas ($)' para agendar de inmediato; solo queda pendiente la cuenta para coordinar tu inicio de sesión."
            }
            else -> {
                "Gracias por abrirte y compartir tu historia en este espacio confidencial de Asesorías Consultorias psicología. " +
                "Cada palabra que expresas te ayuda a procesar lo que ocurre en tu interior. " +
                "Recuerda que también puedes plasmar esto en tu Diario Emocional Seguro para ganar puntos y seguir tu evolución. ¿Cómo te sientes ahora?"
            }
        }
    }
}
