package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class EnvironmentTheme(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int?,
    val defaultOverlayAlpha: Float
) {
    CLINIC(
        title = "Clínica de Psicología",
        subtitle = "Ambiente cálido, profesional y terapéutico",
        imageRes = R.drawable.img_clinic_bg,
        defaultOverlayAlpha = 0.82f
    ),
    PLAYFUL(
        title = "Área Lúdica y Juegos",
        subtitle = "Espacio creativo, suave y reconfortante",
        imageRes = R.drawable.img_ludic_bg,
        defaultOverlayAlpha = 0.80f
    ),
    NATURE(
        title = "Paisaje con Árboles",
        subtitle = "Bosque sereno y naturaleza reconfortante",
        imageRes = R.drawable.img_nature_bg,
        defaultOverlayAlpha = 0.82f
    ),
    MINIMAL_ZEN(
        title = "Modo Zen Minimalista",
        subtitle = "Tonos suaves y descanso visual puro",
        imageRes = null,
        defaultOverlayAlpha = 1.0f
    )
}

enum class TextColorChoice(
    val label: String,
    val color: Color
) {
    PURE_WHITE("Blanco Puro", Color(0xFFFFFFFF)),
    WARM_IVORY("Marfil Cálido", Color(0xFFFFF9E6)),
    CALM_TEAL("Menta Serena", Color(0xFFD0F0EE)),
    GOLDEN_SUN("Dorado Suave", Color(0xFFFFECC4)),
    SKY_BLUE("Azul Cielo", Color(0xFFE2F1FF)),
    LAVENDER_MIST("Lavanda Calma", Color(0xFFF3E8FF))
}

enum class SectionCategory(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String
) {
    STRESS(
        id = "stress",
        title = "Manejo del Estrés",
        description = "Libera la tensión mental y corporal, detecta detonantes y recupera la calma.",
        iconName = "Spa"
    ),
    FRUSTRATION(
        id = "frustration",
        title = "Tolerancia a la Frustración",
        description = "Transforma la impotencia en resiliencia y aprende a fluir ante imprevistos.",
        iconName = "Psychology"
    ),
    GOALS_DREAMS(
        id = "goals",
        title = "Metas y Sueños",
        description = "Convierte aspiraciones en pasos concretos, vence la postergación y cree en ti.",
        iconName = "EmojiEvents"
    ),
    SELF_CARE(
        id = "selfcare",
        title = "Autocuidado Diario",
        description = "Establece límites sanos, rutinas de descanso y paz mental cotidiana.",
        iconName = "Favorite"
    )
}

data class QuizQuestion(
    val id: Int,
    val category: SectionCategory,
    val question: String,
    val options: List<QuizOption>
)

data class QuizOption(
    val text: String,
    val psychologicalComment: String,
    val score: Int,
    val tip: String
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isSpecialNotification: Boolean = false
)

data class BookingRequest(
    val id: String,
    val clientName: String,
    val whatsapp: String,
    val email: String,
    val sessionType: String,
    val motive: String,
    val datePreference: String,
    val timeSlot: String,
    val status: String = "En espera de confirmación para inicio de sesión",
    val paymentNote: String = "Solo queda pendiente la cuenta",
    val timestamp: Long = System.currentTimeMillis()
)

data class VirtualGift(
    val id: String,
    val name: String,
    val description: String,
    val priceUsd: Double = 0.0,
    val iconEmoji: String,
    val socialImpact: String,
    val isFreeReward: Boolean = true,
    val isClaimed: Boolean = false,
    val unlockedByTask: String? = null
)

data class JornadaGratuita(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String = "🌱",
    val childrenBenefited: Int = 20,
    val isUnlocked: Boolean = false,
    val unlockedByTask: String? = null
)

data class SelfCareReminder(
    val id: String,
    val title: String,
    val advice: String,
    val category: String,
    val durationMin: Int
)

// --- DIARIO EMOCIONAL SEGURO ---
data class DiaryEntry(
    val id: String,
    val title: String,
    val plainContent: String,
    val encryptedContent: String,
    val tags: List<String>,
    val moodEmoji: String,
    val authorAlias: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DiaryBackupReport(
    val batchId: String,
    val batchNumber: Int,
    val entryCount: Int,
    val authorAlias: String,
    val destinationEmail: String = "tiemposdestinos@gmail.com",
    val ipNetworkInfo: String,
    val previewExtract: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Reporte de 5 relatos enviado a tiemposdestinos@gmail.com"
)

// --- GAMIFICACIÓN ---
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false
)

enum class SoundtrackMode(val label: String, val description: String) {
    PIANO_WATER("Piano & Agua de Naturaleza", "Armonía fluida para serenidad profunda"),
    PIANO_ZEN("Piano Zen Suave", "Acordes introspectivos a 432 Hz"),
    WATER_STREAM("Agua de Cascada Natural", "Ondas continuas de agua relajante"),
    MUTED("Música Desactivada", "Silencio total")
}

data class MelodiaPais(
    val id: String,
    val pais: String,
    val banderaEmoji: String,
    val titulo: String,
    val descripcion: String,
    val tempoFactor: Float = 1.0f,
    val acordesNotas: List<FloatArray>,
    val genero: String = "Alegría y Felicidad"
) {
    val nombre: String get() = titulo
    val bandera: String get() = banderaEmoji
}

val CatalogoMelodiasGlobales: List<MelodiaPais> = listOf(
    // 1. Italia (2 melodías)
    MelodiaPais(
        id = "it_1",
        pais = "Italia",
        banderaEmoji = "🇮🇹",
        titulo = "Tarantella della Gioia",
        descripcion = "Mandolinas y acordes vivos mediterráneos llenos de sol y danza festiva",
        tempoFactor = 1.35f,
        acordesNotas = listOf(
            floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f, 523.25f, 659.25f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(220.00f, 277.18f, 329.63f, 440.00f, 554.37f, 659.25f)
        )
    ),
    MelodiaPais(
        id = "it_2",
        pais = "Italia",
        banderaEmoji = "🇮🇹",
        titulo = "Serenata Toscana Solare",
        descripcion = "Melodía cantábile de colinas toscanas, acordeón y alegría campestre",
        tempoFactor = 1.15f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(164.81f, 196.00f, 246.94f, 329.63f, 392.00f, 493.88f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f)
        )
    ),

    // 2. Roma (2 melodías)
    MelodiaPais(
        id = "roma_1",
        pais = "Roma",
        banderaEmoji = "🇮🇹",
        titulo = "Piazza Navona in Festa",
        descripcion = "Celebración barroca alegre, fuentes de agua doradas y júbilo romano",
        tempoFactor = 1.25f,
        acordesNotas = listOf(
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(220.00f, 277.18f, 329.63f, 440.00f, 554.37f, 659.25f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 440.00f, 587.33f, 739.99f)
        )
    ),
    MelodiaPais(
        id = "roma_2",
        pais = "Roma",
        banderaEmoji = "🇮🇹",
        titulo = "Trastevere Notte Serena e Lieto",
        descripcion = "Guitarras romanas tradicionales, brindis y armonía de hermandad",
        tempoFactor = 1.10f,
        acordesNotas = listOf(
            floatArrayOf(130.81f, 196.00f, 261.63f, 329.63f, 392.00f, 523.25f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 523.25f)
        )
    ),

    // 3. Noruega (2 melodías)
    MelodiaPais(
        id = "no_1",
        pais = "Noruega",
        banderaEmoji = "🇳🇴",
        titulo = "Fjord Morgen Sol (Sol en los Fiordos)",
        descripcion = "Motivo alegre de Edvard Grieg con violín hardanger y brisa cristalina",
        tempoFactor = 1.10f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 369.99f, 392.00f, 493.88f),
            floatArrayOf(220.00f, 277.18f, 329.63f, 440.00f, 554.37f, 659.25f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f)
        )
    ),
    MelodiaPais(
        id = "no_2",
        pais = "Noruega",
        banderaEmoji = "🇳🇴",
        titulo = "Nordisk Sommer Glede (Alegría Boreal)",
        descripcion = "Danza campestre escandinava de celebración del solsticio y luz perpetua",
        tempoFactor = 1.30f,
        acordesNotas = listOf(
            floatArrayOf(146.83f, 185.00f, 220.00f, 293.66f, 369.99f, 440.00f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(110.00f, 164.81f, 220.00f, 277.18f, 329.63f, 440.00f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f)
        )
    ),

    // 4. USA (2 melodías)
    MelodiaPais(
        id = "us_1",
        pais = "USA",
        banderaEmoji = "🇺🇸",
        titulo = "Appalachian Joy Strings",
        descripcion = "Folk acústico de montaña con banjo sereno y optimismo enérgico",
        tempoFactor = 1.30f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 440.00f, 587.33f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f)
        )
    ),
    MelodiaPais(
        id = "us_2",
        pais = "USA",
        banderaEmoji = "🇺🇸",
        titulo = "Pacific Coast Sunshine",
        descripcion = "Piano cálido californiano de brisa veraniega, bienestar y libertad",
        tempoFactor = 1.15f,
        acordesNotas = listOf(
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f),
            floatArrayOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 440.00f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 392.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 440.00f, 523.25f)
        )
    ),

    // 5. México (2 melodías)
    MelodiaPais(
        id = "mx_1",
        pais = "México",
        banderaEmoji = "🇲🇽",
        titulo = "Huapango de la Alegría y Esperanza",
        descripcion = "Ritmo vibrante de jarana huasteca, celebración de la vida y luz",
        tempoFactor = 1.40f,
        acordesNotas = listOf(
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(220.00f, 277.18f, 329.63f, 440.00f, 554.37f, 659.25f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 440.00f, 587.33f, 739.99f)
        )
    ),
    MelodiaPais(
        id = "mx_2",
        pais = "México",
        banderaEmoji = "🇲🇽",
        titulo = "Jarabe del Sol y la Amistad",
        descripcion = "Tradición festiva mexicana con arpas y cuerdas de júbilo compartido",
        tempoFactor = 1.25f,
        acordesNotas = listOf(
            floatArrayOf(130.81f, 196.00f, 261.63f, 329.63f, 392.00f, 523.25f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 523.25f)
        )
    ),

    // 6. España (2 melodías)
    MelodiaPais(
        id = "es_1",
        pais = "España",
        banderaEmoji = "🇪🇸",
        titulo = "Alegrías de Cádiz y Palmeras",
        descripcion = "Guitarra andaluza en compás alegre con brisa marina y sol mediterráneo",
        tempoFactor = 1.30f,
        acordesNotas = listOf(
            floatArrayOf(164.81f, 220.00f, 261.63f, 329.63f, 440.00f, 523.25f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(164.81f, 207.65f, 246.94f, 329.63f, 415.30f, 493.88f)
        )
    ),
    MelodiaPais(
        id = "es_2",
        pais = "España",
        banderaEmoji = "🇪🇸",
        titulo = "Fiesta Mediterránea y Naranjales",
        descripcion = "Vals español luminoso con castañuelas sutiles y alegría de primavera",
        tempoFactor = 1.20f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f)
        )
    ),

    // 7. China (2 melodías)
    MelodiaPais(
        id = "cn_1",
        pais = "China",
        banderaEmoji = "🇨🇳",
        titulo = "Flor de Jazmín en Primavera (Mò Lì Huā)",
        descripcion = "Escala pentatónica tradicional de guzheng y flauta dizi de gozo interior",
        tempoFactor = 1.15f,
        acordesNotas = listOf(
            floatArrayOf(261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 261.63f, 293.66f, 329.63f, 392.00f, 587.33f),
            floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f, 523.25f, 659.25f),
            floatArrayOf(261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f)
        )
    ),
    MelodiaPais(
        id = "cn_2",
        pais = "China",
        banderaEmoji = "🇨🇳",
        titulo = "Jubileo del Dragón y Seda",
        descripcion = "Campanas alegres, melodía festiva de año nuevo y prosperidad viva",
        tempoFactor = 1.30f,
        acordesNotas = listOf(
            floatArrayOf(146.83f, 164.81f, 220.00f, 293.66f, 329.63f, 440.00f),
            floatArrayOf(196.00f, 220.00f, 293.66f, 392.00f, 440.00f, 587.33f),
            floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f, 523.25f, 659.25f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f)
        )
    ),

    // 8. Rusia (2 melodías)
    MelodiaPais(
        id = "ru_1",
        pais = "Rusia",
        banderaEmoji = "🇷🇺",
        titulo = "Vals de los Abedules en Flor",
        descripcion = "Balalaika y acordeón bajan en alegre vals festivo campesino",
        tempoFactor = 1.25f,
        acordesNotas = listOf(
            floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f, 523.25f, 659.25f),
            floatArrayOf(146.83f, 220.00f, 261.63f, 293.66f, 349.23f, 440.00f),
            floatArrayOf(164.81f, 207.65f, 246.94f, 329.63f, 415.30f, 493.88f),
            floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f, 523.25f, 659.25f)
        )
    ),
    MelodiaPais(
        id = "ru_2",
        pais = "Rusia",
        banderaEmoji = "🇷🇺",
        titulo = "Campanas de la Felicidad (Kalinka Danza)",
        descripcion = "Ritmo creciente festivo de esperanza, calidez de hogar y entusiasmo",
        tempoFactor = 1.40f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f)
        )
    ),

    // 9. Países Bajos (2 melodías)
    MelodiaPais(
        id = "nl_1",
        pais = "Países Bajos",
        banderaEmoji = "🇳🇱",
        titulo = "Vals de los Campos de Tulipanes (Tulpenwals)",
        descripcion = "Carillón holandés alegre y acordeón en paseo primaveral entre flores",
        tempoFactor = 1.20f,
        acordesNotas = listOf(
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 523.25f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 523.25f)
        )
    ),
    MelodiaPais(
        id = "nl_2",
        pais = "Países Bajos",
        banderaEmoji = "🇳🇱",
        titulo = "Alegría de Ámsterdam y Molinos",
        descripcion = "Paseo soleado en bicicleta junto a los canales con armonías de celebración",
        tempoFactor = 1.30f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 261.63f, 329.63f, 392.00f, 523.25f, 659.25f),
            floatArrayOf(220.00f, 261.63f, 349.23f, 440.00f, 523.25f, 659.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(130.81f, 196.00f, 261.63f, 329.63f, 392.00f, 523.25f)
        )
    ),

    // 10. Guatemala (Marimba de los Altos & Jazz Alegre)
    MelodiaPais(
        id = "gt_1",
        pais = "Guatemala",
        banderaEmoji = "🇬🇹",
        titulo = "Ferrocarril de los Altos (Marimba Alegre)",
        descripcion = "Clásico festivo quetzalteco de Domingo Bethancourt en marimba doble, alegre y rítmico",
        tempoFactor = 1.45f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f, 783.99f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f, 739.99f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 440.00f, 493.88f, 587.33f)
        )
    ),
    MelodiaPais(
        id = "gt_2",
        pais = "Guatemala",
        banderaEmoji = "🇬🇹",
        titulo = "Luna de Xelajú en Jazz Alegre & Marimba",
        descripcion = "Fusión guatemalteca con cadencia de swing alegre, teclas de marimba luminosas y júbilo",
        tempoFactor = 1.25f,
        acordesNotas = listOf(
            floatArrayOf(130.81f, 196.00f, 261.63f, 329.63f, 392.00f, 493.88f, 523.25f),
            floatArrayOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 392.00f, 440.00f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 392.00f, 440.00f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 349.23f, 392.00f, 440.00f, 523.25f)
        )
    ),
    MelodiaPais(
        id = "gt_3",
        pais = "Guatemala",
        banderaEmoji = "🇬🇹",
        titulo = "Chichicastenango Alegre (Fiesta y Marimba)",
        descripcion = "Celebración festiva con notas alegres de son y armonía de hermandad maya",
        tempoFactor = 1.35f,
        acordesNotas = listOf(
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(220.00f, 277.18f, 329.63f, 440.00f, 554.37f, 659.25f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 440.00f, 587.33f, 739.99f)
        )
    ),

    // 11. Música Electrónica (EDM / Synthwave Alegre)
    MelodiaPais(
        id = "edm_1",
        pais = "Electrónica",
        banderaEmoji = "⚡",
        titulo = "Synthwave Euphoria (Electrónica Alegre)",
        descripcion = "Arpegios dinámicos de sintetizador a 128 BPM, energía vibrante y motivación positiva",
        tempoFactor = 1.55f,
        acordesNotas = listOf(
            floatArrayOf(110.00f, 220.00f, 261.63f, 329.63f, 440.00f, 523.25f, 659.25f),
            floatArrayOf(174.61f, 220.00f, 349.23f, 440.00f, 523.25f, 659.25f, 698.46f),
            floatArrayOf(130.81f, 261.63f, 329.63f, 392.00f, 523.25f, 659.25f, 783.99f),
            floatArrayOf(196.00f, 293.66f, 392.00f, 440.00f, 493.88f, 587.33f, 783.99f)
        )
    ),
    MelodiaPais(
        id = "edm_2",
        pais = "Electrónica",
        banderaEmoji = "⚡",
        titulo = "Future Bass Sunshine (Electro Pop)",
        descripcion = "Acordes brillantes de festival, ritmo bailable de verano y alegría compartida",
        tempoFactor = 1.45f,
        acordesNotas = listOf(
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 440.00f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f)
        )
    ),

    // 12. Canción Pop (Pop Alegre e Inspirador)
    MelodiaPais(
        id = "pop_1",
        pais = "Canción Pop",
        banderaEmoji = "🌟",
        titulo = "Golden Daylight (Pop Acústico Alegre)",
        descripcion = "Guitarra pop radiante, piano optimista de radio y melodía contagiosa de felicidad",
        tempoFactor = 1.35f,
        acordesNotas = listOf(
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            floatArrayOf(164.81f, 196.00f, 246.94f, 329.63f, 392.00f, 493.88f),
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 392.00f)
        )
    ),
    MelodiaPais(
        id = "pop_2",
        pais = "Canción Pop",
        banderaEmoji = "🌟",
        titulo = "Lykke Joyful Beats (Pop Moderno)",
        descripcion = "Armonías alegres de sintetizador pop, palmadas y entusiasmo para el día a día",
        tempoFactor = 1.40f,
        acordesNotas = listOf(
            floatArrayOf(130.81f, 164.81f, 196.00f, 261.63f, 329.63f, 523.25f),
            floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f),
            floatArrayOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 440.00f),
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f, 440.00f, 523.25f)
        )
    )
)


