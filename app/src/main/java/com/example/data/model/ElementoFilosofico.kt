package com.example.data.model

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

data class ContenidoMultilingue(
    val titulo: String = "",
    val texto: String = "",
    val autorSignatario: String = "",
    val autor: String = if (autorSignatario.isNotBlank()) autorSignatario else ""
)

data class ElementoFilosofico(
    val id: Int,
    val tipo: String, // "FRASE", "PALABRA", "CONCEPTOS"
    val autorOrigen: String,
    val tags: List<String>,
    val traducciones: Map<String, ContenidoMultilingue>
)

// 1. Estructura para la traducción de TODA la plataforma (Botones, Menús, Títulos)
data class UIStrings(
    val nombreApp: String,
    val tituloEspacio: String,
    val botonIdiomas: String,
    val botonClubAsesoria: String, // El botón corregido oficial
    val menuInicio: String,
    val menuPerfil: String,
    val subMenuConfig: String,
    val firmaTarjeta: String,
    // Elementos de plataforma para traducción global completa
    val menuDiario: String = "Diario Seguro",
    val menuChat: String = "Chat de Desahogo",
    val menuTracker: String = "Radar Emocional (Vibe Check)",
    val menuJuegos: String = "Juegos de Preguntas",
    val menuRespira: String = "Respiración 4-7-8",
    val menuCitas: String = "Citas con Psicólogo",
    val menuRegalos: String = "Regalos & Jornadas",
    val menuDiplomas: String = "Insignias & Diplomas",
    val menuAmbiente: String = "Ambiente & Colores",
    val menuCanjearClave: String = "Canjear Clave",
    val menuAportacion: String = "Aportación Libre",
    val subMenuDiario: String = "Diario Seguro con Candado",
    val subMenuChat: String = "Chat de Desahogo Confidencial",
    val subMenuComunidad: String = "Chat Comunidad Lykke",
    val subtituloDiario: String = "Notas con candado y backups",
    val subtituloChat: String = "Consulta emocional & voz",
    val subtituloTracker: String = "Ánimo, Vibe & Psicología HACER/SER",
    val subtituloJuegos: String = "Estrés, metas (+50 XP)",
    val subtituloRespira: String = "Alivio rápido (+40 XP)",
    val subtituloCitas: String = "$5 Audio / $15 Video",
    val subtituloRegalos: String = "100% GRATIS • Recompensas",
    val subtituloDiplomas: String = "Logros (10 sesiones)",
    val subtituloClub: String = "Pase ilimitado de asesoría",
    val subtituloAmbiente: String = "Fondos y tipografía",
    val subtituloCanjear: String = "Club Ilimitado",
    val subtituloAportacion: String = "Soporte voluntario servidores",
    val subtituloTramiteDiploma: String = "Certificado oficial pasarela"
)

// 2. Diccionario global de la App (Internacionales, Mayas, Ruso y el nuevo Danés)
object Localizer {
    val diccionario = mapOf(
        "es" to UIStrings(
            nombreApp = "Asesorías y Consultorías Lykke",
            tituloEspacio = "Tu Espacio de Paz",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Inicio",
            menuPerfil = "Mi Perfil",
            subMenuConfig = "Configuración avanzada",
            firmaTarjeta = "— Estoicismo y Bienestar",
            menuDiario = "Diario Seguro",
            menuChat = "Chat de Desahogo",
            menuTracker = "Radar Emocional (Vibe Check)",
            menuJuegos = "Juegos de Preguntas",
            menuRespira = "Respiración 4-7-8",
            menuCitas = "Citas con Psicólogo",
            menuRegalos = "Regalos & Jornadas",
            menuDiplomas = "Insignias & Diplomas",
            menuAmbiente = "Ambiente & Colores",
            menuCanjearClave = "Canjear Clave",
            menuAportacion = "Aportación Libre",
            subMenuDiario = "Diario Seguro con Candado",
            subMenuChat = "Chat de Desahogo Confidencial",
            subMenuComunidad = "Chat Comunidad Lykke"
        ),
        "en" to UIStrings(
            nombreApp = "Lykke Advisory & Consulting",
            tituloEspacio = "Your Space of Peace",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Unlimited Lykke Advisory Service and Lykke Community",
            menuInicio = "Home",
            menuPerfil = "My Profile",
            subMenuConfig = "Advanced Settings",
            firmaTarjeta = "— Stoicism and Wellness",
            menuDiario = "Secure Diary",
            menuChat = "Relief Chat",
            menuTracker = "Emotional Radar (Vibe Check)",
            menuJuegos = "Question Games",
            menuRespira = "4-7-8 Breathing",
            menuCitas = "Psychologist Sessions",
            menuRegalos = "Free Gifts",
            menuDiplomas = "Badges & Diplomas",
            menuAmbiente = "Ambience & Colors",
            menuCanjearClave = "Redeem Key",
            menuAportacion = "Free Donation",
            subMenuDiario = "Encrypted Lock Diary",
            subMenuChat = "Confidential Relief Chat",
            subMenuComunidad = "Lykke Community Chat",
            subtituloDiario = "Encrypted notes & backups",
            subtituloChat = "Emotional advice & voice",
            subtituloTracker = "Mood, energy & DO/BE",
            subtituloJuegos = "Stress, goals (+50 XP)",
            subtituloRespira = "Quick relief (+40 XP)",
            subtituloCitas = "$5 Audio / $15 Video",
            subtituloRegalos = "100% FREE • Rewards",
            subtituloDiplomas = "Achievements (10 sessions)",
            subtituloClub = "Unlimited Advisory Club pass",
            subtituloAmbiente = "Backgrounds & fonts",
            subtituloCanjear = "Advisory Club",
            subtituloAportacion = "Voluntary server support",
            subtituloTramiteDiploma = "Official certificate gateway"
        ),
        "da" to UIStrings( // <-- NUEVO IDIOMA: DANÉS
            nombreApp = "Lykke Advisory & Consulting",
            tituloEspacio = "Dit Fredfyldte Rum",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Ubegrænset Lykke Rådgivningstjeneste og Fællesskab",
            menuInicio = "Hjem",
            menuPerfil = "Min Profil",
            subMenuConfig = "Avancerede Indstillinger",
            firmaTarjeta = "— Stoicism og Velvære",
            menuDiario = "Sikker Dagbog",
            menuChat = "Aflastningschat",
            menuTracker = "Tracker Tilstand",
            menuJuegos = "Spørgsmålsspil",
            menuRespira = "4-7-8 Vejrtrækning",
            menuCitas = "Psykologaftaler",
            menuRegalos = "Gratis Gaver",
            menuDiplomas = "Emblemer & Diplomer",
            menuAmbiente = "Miljø & Farver",
            menuCanjearClave = "Indløs Nøgle",
            menuAportacion = "Frivilligt Bidrag",
            subMenuDiario = "Krypteret Sikker Dagbog",
            subMenuChat = "Fortrolig Aflastningschat",
            subMenuComunidad = "Lykke Fællesskabs Chat",
            subtituloDiario = "Krypterede noter og backups",
            subtituloChat = "Følelsesmæssig rådgivning og stemme",
            subtituloTracker = "Humør, energi og GØRE/VÆRE",
            subtituloJuegos = "Stress, mål (+50 XP)",
            subtituloRespira = "Hurtig ro (+40 XP)",
            subtituloCitas = "$5 Lyd / $15 Video",
            subtituloRegalos = "100% GRATIS • Belønninger",
            subtituloDiplomas = "Præstationer (10 sessioner)",
            subtituloClub = "Ubegrænset rådgivningspas",
            subtituloAmbiente = "Baggrunde og skrifttyper",
            subtituloCanjear = "Rådgivningsklub",
            subtituloAportacion = "Frivillig serverstøtte",
            subtituloTramiteDiploma = "Officielt certifikat portal"
        ),
        "fr" to UIStrings(
            nombreApp = "Lykke Advisory & Consulting",
            tituloEspacio = "Votre Espace de Paix",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Service Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Accueil",
            menuPerfil = "Mon Profil",
            subMenuConfig = "Paramètres Avancés",
            firmaTarjeta = "— Stoïcisme et Bien-être",
            menuDiario = "Journal Sécurisé",
            menuChat = "Discussion d'Aide",
            menuTracker = "Mode Tracker",
            menuJuegos = "Jeux de Questions",
            menuRespira = "Respiration 4-7-8",
            menuCitas = "Rendez-vous Psy",
            menuRegalos = "Cadeaux Gratuits",
            menuDiplomas = "Badges & Diplômes",
            menuAmbiente = "Ambiance & Couleurs",
            menuCanjearClave = "Échanger Clé",
            menuAportacion = "Don Libre",
            subMenuDiario = "Journal Chiffré Sécurisé",
            subMenuChat = "Discussion d'Aide Confidentielle",
            subMenuComunidad = "Chat Communauté Lykke"
        ),
        "it" to UIStrings(
            nombreApp = "Lykke Advisory & Consulting",
            tituloEspacio = "Il Tuo Spazio di Pace",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Home",
            menuPerfil = "Il Mio Profilo",
            subMenuConfig = "Impostazioni Avanzate",
            firmaTarjeta = "— Stoicismo e Benessere",
            menuDiario = "Diario Sicuro",
            menuChat = "Chat di Sfogamento",
            menuTracker = "Modalità Tracker",
            menuJuegos = "Giochi di Domande",
            menuRespira = "Respirazione 4-7-8",
            menuCitas = "Appuntamenti con Psicologo",
            menuRegalos = "Regali Gratuiti",
            menuDiplomas = "Distintivi & Diplomi",
            menuAmbiente = "Ambiente & Colori",
            menuCanjearClave = "Riscatta Chiave",
            menuAportacion = "Donazione Libera",
            subMenuDiario = "Diario Criptato Sicuro",
            subMenuChat = "Chat di Sfogamento Confidenziale",
            subMenuComunidad = "Chat Comunità Lykke"
        ),
        "zh" to UIStrings(
            nombreApp = "Lykke 咨询与顾问",
            tituloEspacio = "您的宁静空间",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "首页",
            menuPerfil = "个人资料",
            subMenuConfig = "高级设置",
            firmaTarjeta = "— 斯多葛主义与身心健康",
            menuDiario = "安全日记",
            menuChat = "倾诉沟通",
            menuTracker = "追踪模式",
            menuJuegos = "心理问答",
            menuRespira = "4-7-8 呼吸",
            menuCitas = "预约心理咨询",
            menuRegalos = "免费礼物",
            menuDiplomas = "徽章与文凭",
            menuAmbiente = "氛围与色彩",
            menuCanjearClave = "兑换秘钥",
            menuAportacion = "自愿赞助",
            subMenuDiario = "私密加密日记",
            subMenuChat = "保密倾诉对话",
            subMenuComunidad = "Lykke 社区群聊"
        ),
        "ru" to UIStrings(
            nombreApp = "Lykke Консалтинг и Консультации",
            tituloEspacio = "Ваше пространство мира",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Главная",
            menuPerfil = "Мой Профиль",
            subMenuConfig = "Расширенные Настройки",
            firmaTarjeta = "— Стоицизм и Благополучие",
            menuDiario = "Безопасный Дневник",
            menuChat = "Чат Поддержки",
            menuTracker = "Режим Трекера",
            menuJuegos = "Игры Вопросов",
            menuRespira = "Дыхание 4-7-8",
            menuCitas = "Сессии с Психологом",
            menuRegalos = "Бесплатные Подарки",
            menuDiplomas = "Значки и Дипломы",
            menuAmbiente = "Атмосфера и Цвета",
            menuCanjearClave = "Активировать Ключ",
            menuAportacion = "Поддержка Проекта",
            subMenuDiario = "Зашифрованный Дневник",
            subMenuChat = "Конфиденциальный Чат Поддержки",
            subMenuComunidad = "Чат Сообщества Lykke"
        ),
        "cak" to UIStrings(
            nombreApp = "Asesorías y Consultorías Lykke",
            tituloEspacio = "Apxib'al K'uxlal",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Tikilib'al",
            menuPerfil = "Nu-wäch",
            subMenuConfig = "Ruchojmil k'exoj",
            firmaTarjeta = "— Na'oj",
            menuDiario = "Wuj richin K'uxlal",
            menuChat = "Ch'owen richin Kolob'ik",
            menuTracker = "K'aslemal Tracker",
            menuJuegos = "Etz'anem K'utunïk",
            menuRespira = "Jik'ik K'uxlal 4-7-8",
            menuCitas = "Tzijonem ruk' Aq'omanel",
            menuRegalos = "Sipam SIPANÏK",
            menuDiplomas = "Rupanoqil Na'ob'al",
            menuAmbiente = "B'anob'äl & Bonil",
            menuCanjearClave = "K'exoj Ruq'a'",
            menuAportacion = "To'ik richin Utzil",
            subMenuDiario = "Ewan Wuj richin K'uxlal",
            subMenuChat = "Ewan Ch'owen richin Kolob'ik",
            subMenuComunidad = "Chat Tinamït Lykke"
        ),
        "quc" to UIStrings(
            nombreApp = "Asesorías y Consultorías Lykke",
            tituloEspacio = "Uk'u'x Jamaril",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Majib'al",
            menuPerfil = "Nu-wäch",
            subMenuConfig = "Ucholajil k'exoj",
            firmaTarjeta = "— No'j",
            menuDiario = "Wuj rech K'uxlib'al",
            menuChat = "Ch'awem rech Kolob'al",
            menuTracker = "Tracker rech Jamaril",
            menuJuegos = "Etz'anem K'otow Chi'",
            menuRespira = "Ujiq'ik Uk'u'x 4-7-8",
            menuCitas = "K'ulmatajem ruk' Ajkunanel",
            menuRegalos = "Sipanik rech Utzil",
            menuDiplomas = "Etalil rech K'aslemal",
            menuAmbiente = "Kojob'al & Kayib'al",
            menuCanjearClave = "K'exoj Jaqib'al",
            menuAportacion = "Sipanik Tob'anik",
            subMenuDiario = "Awam Wuj rech K'uxlib'al",
            subMenuChat = "Awam Ch'awem rech Kolob'al",
            subMenuComunidad = "Chat Tinamït Lykke"
        ),
        "kek" to UIStrings( // Q'EQCHI'
            nombreApp = "Asesorías y Consultorías Lykke",
            tituloEspacio = "Tuqtuukilal K'al",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Tiklayil",
            menuPerfil = "In-wankil",
            subMenuConfig = "Xtusulal jaljookil",
            firmaTarjeta = "— Na'ojil",
            menuDiario = "Hu rech K'a'uxl",
            menuChat = "Aatinak rech Hilank",
            menuTracker = "Tracker K'aslemal",
            menuJuegos = "B'atz'unk Patz'ok",
            menuRespira = "Musike'k 4-7-8",
            menuCitas = "Aatinak rik'in Banonel",
            menuRegalos = "Maatan SIPOK",
            menuDiplomas = "Matq'ab'il K'aslemal",
            menuAmbiente = "Na'ajej & B'onol",
            menuCanjearClave = "Xk'eeb'al Lawil",
            menuAportacion = "Sipank Tenq'",
            subMenuDiario = "Tz'apb'il Hu rech K'a'uxl",
            subMenuChat = "Muqmuukil Aatinak rech Hilank",
            subMenuComunidad = "Chat Tenamit Lykke"
        ),
        "gar" to UIStrings( // GARÍFUNA
            nombreApp = "Lykke Advisory & Consulting",
            tituloEspacio = "Isieni Lidan Darangilaü",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Lagumeseha",
            menuPerfil = "Nuguya",
            subMenuConfig = "Aba abanichuni",
            firmaTarjeta = "— Anichuni luma Bwiti",
            menuDiario = "Garüdia Darangilaü",
            menuChat = "Ariñahani Darangilaü",
            menuTracker = "Tracker Ibagari",
            menuJuegos = "Uraga Ábürüha",
            menuRespira = "Samina 4-7-8",
            menuCitas = "Ábürüha luma Surusia",
            menuRegalos = "Idewei Bwiti",
            menuDiplomas = "Dürügüha Ibagari",
            menuAmbiente = "Ubaü & Bonita",
            menuCanjearClave = "Aransei Yebe",
            menuAportacion = "Idewei Arufudahani",
            subMenuDiario = "Afüyein Garüdia Darangilaü",
            subMenuChat = "Afüyein Ariñahani",
            subMenuComunidad = "Chat Garinagu Lykke"
        ),
        "xin" to UIStrings( // XINKA
            nombreApp = "Asesorías y Consultorías Lykke",
            tituloEspacio = "Hapašü ¯üük'aü",
            botonIdiomas = "Languages",
            botonClubAsesoria = "Servicio Asesoría Lykke Ilimitado y comunidad Lykke",
            menuInicio = "Hurra",
            menuPerfil = "Nin",
            subMenuConfig = "Maku ta k'exoj",
            firmaTarjeta = "— Na'u",
            menuDiario = "Wuj Hapašü",
            menuChat = "Tzi'k Hapašü",
            menuTracker = "Tracker K'aslekey",
            menuJuegos = "K'utuk'u Hapašü",
            menuRespira = "Jik' Hapa 4-7-8",
            menuCitas = "Kunanel Hapašü",
            menuRegalos = "Sipa Hapašü",
            menuDiplomas = "Etal Na'u",
            menuAmbiente = "B'ano & Kayi",
            menuCanjearClave = "K'ex Jaq",
            menuAportacion = "To' Utzil",
            subMenuDiario = "Ewan Wuj Hapašü",
            subMenuChat = "Ewan Tzi'k Hapašü",
            subMenuComunidad = "Chat Comunidad Lykke"
        )
    )

    fun get(code: String): UIStrings = diccionario[code] ?: diccionario["es"]!!
}

data class IdiomaNacional(
    val code: String,
    val name: String,
    val isoCode: String
)

object IdiomasGuatemalaCatalog {
    private val languages = listOf(
        IdiomaNacional("cak", "Kaqchiquel", "cak"),
        IdiomaNacional("quc", "K'iche'", "quc"),
        IdiomaNacional("kek", "Q'eqchi'", "kek"),     // <-- Q'eqchi'
        IdiomaNacional("gar", "Garífuna", "fai"),     // <-- Garífuna
        IdiomaNacional("xin", "Xinka", "xin")          // <-- Xinka
    )

    fun findByCode(code: String): IdiomaNacional? {
        return languages.find { it.code == code }
    }

    fun getAll(): List<IdiomaNacional> = languages
}

data class IdiomaCheck(
    val code: String,
    val label: String
)

val listaIdiomas = listOf(
    IdiomaCheck("es", "Español 🇪🇸"),
    IdiomaCheck("en", "English 🇺🇸"),
    IdiomaCheck("da", "Danés (Dansk) 🇩🇰"),
    IdiomaCheck("fr", "Français 🇫🇷"),
    IdiomaCheck("it", "Italiano 🇮🇹"),
    IdiomaCheck("zh", "Mandarín (Chino) 🇨🇳"),
    IdiomaCheck("ru", "Ruso (Русский) 🇷🇺"),
    IdiomaCheck("cak", "Kaqchiquel (Maya) 🇬🇹"),
    IdiomaCheck("quc", "K'iche' (Maya) 🇬🇹"),
    IdiomaCheck("kek", "Q'eqchi' (Maya) 🇬🇹"),
    IdiomaCheck("gar", "Garífuna 🇬🇹"),
    IdiomaCheck("xin", "Xinka 🇬🇹")
)

data class AppLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val isMaya: Boolean = false,
    val flag: String = "🌐"
)

object AppLanguageCatalog {
    val languages = listOf(
        AppLanguage("es", "Español", "Español", flag = "🇪🇸"),
        AppLanguage("en", "Inglés", "English", flag = "🇬🇧"),
        AppLanguage("da", "Danés", "Dansk (Lykke)", flag = "🇩🇰"),
        AppLanguage("fr", "Francés", "Français", flag = "🇫🇷"),
        AppLanguage("it", "Italiano", "Italiano", flag = "🇮🇹"),
        AppLanguage("zh", "Mandarín", "中文", flag = "🇨🇳"),
        AppLanguage("ru", "Ruso", "Русский", flag = "🇷🇺"),
        AppLanguage("cak", "Kaqchiquel", "Kaqchikel (Maya)", isMaya = true, flag = "🏛️"),
        AppLanguage("quc", "Quiché", "K'iche' (Maya)", isMaya = true, flag = "🌽"),
        AppLanguage("kek", "Q'eqchi'", "Q'eqchi' (Maya)", isMaya = true, flag = "🌿"),
        AppLanguage("gar", "Garífuna", "Garinagu", isMaya = false, flag = "🇬🇹"),
        AppLanguage("xin", "Xinka", "Xinka", isMaya = false, flag = "🇬🇹")
    )

    fun findByCode(code: String): AppLanguage? = languages.find { it.code == code }
}

object DiccionarioEmocionalLocal {
    // Mapa que asocia palabras o sinónimos en cualquier idioma con el TAG interno de la app
    val palabrasClave = mapOf(
        // ==========================================
        // 🇪🇸 ESPAÑOL
        // ==========================================
        "estres" to "estres", "estresado" to "estres", "estresada" to "estres", "ansiedad" to "estres", "emociones" to "estres", "angustia" to "estres",
        "enfermedad" to "enfermedad", "enfermo" to "enfermedad", "enferma" to "enfermedad", "dolor" to "enfermedad", "tormenta" to "enfermedad", "sintomas" to "enfermedad", "cuerpo" to "enfermedad",
        "continuar" to "continuar", "reinventar" to "continuar", "reinventarte" to "continuar", "adelante" to "continuar", "vida" to "continuar", "futuro" to "continuar", "forjar" to "continuar",
        "solo" to "soledad", "soledad" to "soledad", "sola" to "soledad", "triste" to "soledad", "conmigo" to "soledad", "acompañado" to "soledad",
        "viriditas" to "viriditas", "hildegarda" to "viriditas", "fuerza verde" to "viriditas", "vitalidad" to "viriditas",
        "merton" to "soledad", "rumi" to "rumi", "silencio" to "rumi", "luz" to "rumi",
        "kaslemal" to "k'aslemal", "nawal" to "k'aslemal", "nawales" to "k'aslemal", "equilibrio" to "k'aslemal",
        "kierkegaard" to "kierkegaard", "spinoza" to "spinoza", "lao tse" to "calma", "wu wei" to "calma", "emerson" to "confianza", "thoreau" to "simplicidad",
        "tuchan" to "leonor tuchan", "sequen" to "sequen", "independencia" to "independencia",
        "adam" to "k. adam medina", "medina" to "k. adam medina", "ada" to "k. adam medina", "k ada" to "k. adam medina", "k. ada" to "k. adam medina", "k ada medina" to "k. adam medina", "k. ada medina" to "k. adam medina", "sadhguru" to "sadhguru",
        "gilbert" to "walter gilbert", "walter" to "walter gilbert", "adn" to "walter gilbert",
        "ruzzarin" to "diego ruzzarin", "diego" to "diego ruzzarin",
        "gross" to "david j. gross", "david gross" to "david j. gross",
        "moody" to "dr. raymond moody", "raymond" to "dr. raymond moody",
        "blanco" to "carlos blanco", "carlos" to "carlos blanco", "cerebro" to "carlos blanco",
        "einstein" to "albert einstein",
        "sans" to "dr. manuel sans segarra", "segarra" to "dr. manuel sans segarra",
        "freud" to "sigmund freud", "jung" to "carl jung", "lacan" to "jacques lacan",
        "frankl" to "viktor frankl", "logoterapia" to "viktor frankl",
        "seligman" to "martin seligman", "hayes" to "steven hayes",
        "schwartz" to "jeffrey schwartz", "neuroplasticidad" to "jeffrey schwartz",
        "linehan" to "marsha linehan", "dbt" to "marsha linehan",
        "beck" to "aaron beck", "tcc" to "aaron beck",
        "benedetti" to "mario benedetti", "lealtad" to "mario benedetti",
        "riso" to "walter riso", "permico" to "permico", "mortandad" to "permico",
        "messi" to "messi", "jordan" to "jordan", "federer" to "federer", "schwarzenegger" to "arnold schwarzenegger",
        "yao" to "yao ming", "hendrix" to "jimi hendrix", "clapton" to "eric clapton", "ferreiro" to "lara ferreiro",
        "graham" to "billy graham", "ronaldinho" to "ronaldinho", "dubon" to "ross dubon", "corado" to "rene corado",
        "jesus" to "jesus de nazaret", "confucio" to "confucio", "chua" to "luis morales chua",
        "borges" to "borges", "cortazar" to "julio cortazar", "mcluhan" to "marshall mcluhan", "sagan" to "carl sagan",
        "jobs" to "steve jobs", "musk" to "elon musk", "cook" to "tim cook", "suger" to "eduardo suger cofino",
        "daniel" to "profeta daniel", "elias" to "profeta elias", "ezequiel" to "profeta ezequiel",

        // ==========================================
        // 🇺🇸 INGLÉS (English)
        // ==========================================
        "stress" to "estres", "stressed" to "estres", "anxiety" to "estres", "anxious" to "estres",
        "illness" to "enfermedad", "sick" to "enfermedad", "pain" to "enfermedad", "storm" to "enfermedad", "body" to "enfermedad",
        "continue" to "continuar", "reinvent" to "continuar", "forward" to "continuar", "life" to "continuar", "future" to "continuar",
        "alone" to "soledad", "lonely" to "soledad", "solitude" to "soledad", "sad" to "soledad", "with" to "soledad",

        // ==========================================
        // 🇩🇰 DANÉS (Dansk) - ¡NUEVO!
        // ==========================================
        "stress" to "estres", "stresset" to "estres", "angst" to "estres",
        "sygdom" to "enfermedad", "syg" to "enfermedad", "smerte" to "enfermedad", "storm" to "enfermedad",
        "fortsætte" to "continuar", "genopfinde" to "continuar", "liv" to "continuar", "fremtid" to "continuar",
        "alene" to "soledad", "ensom" to "soledad", "trist" to "soledad", "med" to "soledad",

        // ==========================================
        // 🇫🇷 FRANCÉS (Français)
        // ==========================================
        "stress" to "estres", "anxiété" to "estres", "angoisse" to "estres",
        "maladie" to "enfermedad", "malade" to "enfermedad", "douleur" to "enfermedad", "tempête" to "enfermedad",
        "continuer" to "continuar", "réinventer" to "continuar", "vie" to "continuar", "futur" to "continuar",
        "seul" to "soledad", "seule" to "soledad", "solitude" to "soledad", "triste" to "soledad",

        // ==========================================
        // 🇮🇹 ITALIANO (Italiano)
        // ==========================================
        "stress" to "estres", "ansia" to "estres", "angoscia" to "estres",
        "malattia" to "enfermedad", "malato" to "enfermedad", "dolore" to "enfermedad", "tempesta" to "enfermedad",
        "continuare" to "continuar", "reinventare" to "continuar", "vita" to "continuar", "futuro" to "continuar",
        "solo" to "soledad", "sola" to "soledad", "solitudine" to "soledad", "triste" to "soledad",

        // ==========================================
        // 🇨🇳 CHINO MANDARÍN (中文)
        // ==========================================
        "压力" to "estres", "焦虑" to "estres", 
        "疾病" to "enfermedad", "生病" to "enfermedad", "痛苦" to "enfermedad", "暴风雨" to "enfermedad",
        "继续" to "continuar", "重塑" to "continuar", "生活" to "continuar", "未来" to "continuar",
        "孤独" to "soledad", "独自" to "soledad", "伤心" to "soledad",

        // ==========================================
        // 🇷🇺 RUSO (Русский)
        // ==========================================
        "стресс" to "estres", "тревога" to "estres", 
        "болезнь" to "enfermedad", "болен" to "enfermedad", "боль" to "enfermedad", "буря" to "enfermedad",
        "продолжать" to "continuar", "переосмыслить" to "continuar", "жизнь" to "continuar", "будущее" to "continuar",
        "один" to "soledad", "одиноко" to "soledad", "грустно" to "soledad",

        // ==========================================
        // 🇬🇹 KAQCHIQUEL (Maya)
        // ==========================================
        "k'axomal" to "estres", "jolom" to "estres",
        "yab'il" to "enfermedad", "tewal" to "enfermedad", "jab" to "enfermedad", "ch'acul" to "enfermedad",
        "k'aslemal" to "continuar", "banobil" to "continuar", "yanuk" to "continuar",

        // ==========================================
        // 🇬🇹 K'ICHE' (Maya)
        // ==========================================
        "jamaril" to "estres", "k'ax" to "estres",
        "yab'il" to "enfermedad", "tew" to "enfermedad", "jab'" to "enfermedad",
        "tzukunik" to "continuar", "k'aslemal" to "continuar", "b'anok" to "continuar",

        // ==========================================
        // 🇬🇹 Q'EQCHI' (Maya) - ¡NUEVO!
        // ==========================================
        "tuqtuukilal" to "estres", "ch'ool" to "estres",
        "yab'il" to "enfermedad", "tenq'ank" to "enfermedad", "qiix" to "enfermedad",
        "tiklayil" to "continuar", "yuam" to "continuar", "k'aslik" to "continuar",

        // ==========================================
        // 🇬🇹 GARÍFUNA - ¡NUEVO!
        // ==========================================
        "darangilaü" to "estres", "anfidi" to "estres",
        "sandi" to "enfermedad", "diga" to "enfermedad", "ütünu" to "enfermedad",
        "lagumeseha" to "continuar", "abani" to "continuar", "kawuyu" to "continuar",
        "nuguya" to "soledad", "ágama" to "soledad",

        // ==========================================
        // 🇬🇹 XINKA - ¡NUEVO!
        // ==========================================
        "hapašü" to "estres", "üük'aü" to "estres",
        "pul'y" to "enfermedad", "ayudadó" to "enfermedad", "maku" to "enfermedad",
        "hurra" to "continuar", "k'exoj" to "continuar", "ta'wu" to "continuar"
    )
}

object MotorWellnessLocal {
    /**
     * Analiza el texto dictado al micrófono o escrito. Limpia los signos de puntuación,
     * fragmenta en palabras individuales y detecta el TAG temático correspondiente.
     */
    fun clasificarEntrada(textoEntrada: String): String? {
        val textoLimpio = textoEntrada.lowercase()
            .replace(Regex("[.,/#!$%^&*;:{}=\\-_`~()?¿¡]"), "")
        val palabras = textoLimpio.split(" ")
        for (palabra in palabras) {
            if (DiccionarioEmocionalLocal.palabrasClave.containsKey(palabra)) {
                return DiccionarioEmocionalLocal.palabrasClave[palabra]
            }
        }
        return null
    }

    /**
     * Extrae el contenido multilingüe final desde la base de datos de la app
     * según el idioma activo seleccionado en el menú desplegable (Checklist).
     */
    fun obtenerContenidoUI(tagDetectado: String, idiomaActivo: String): ContenidoMultilingue? {
        val elemento = bancoDeDatosCompleto.find { it.tags.contains(tagDetectado) }
        return elemento?.traducciones?.get(idiomaActivo) ?: elemento?.traducciones?.get("es")
    }

    fun procesarTextoLocal(textoUsuario: String): String? = clasificarEntrada(textoUsuario)
    fun obtenerFraseLocal(tagDetectado: String, idiomaActualInterfaz: String): ContenidoMultilingue? = obtenerContenidoUI(tagDetectado, idiomaActualInterfaz)
}

// Alias para compatibilidad de nombramiento
val MuloTraduccionLocal = MotorWellnessLocal

object MayaLanguageCatalog {
    fun findByCode(code: String): IdiomaNacional? = IdiomasGuatemalaCatalog.findByCode(code)
    fun getAll(): List<IdiomaNacional> = IdiomasGuatemalaCatalog.getAll()
}

fun generarPromptParaGemini(codigoIdioma: String, textoOriginal: String): String {
    val idiomaSeleccionado = IdiomasGuatemalaCatalog.findByCode(codigoIdioma)
    return """
        Traduce el siguiente texto en Español al idioma maya ${idiomaSeleccionado?.name}. 
        Utiliza la variante lingüística correspondiente al código ISO: ${idiomaSeleccionado?.isoCode}. 
        Conserva el sentido de fortaleza interior y bienestar.
        
        Texto a traducir: "$textoOriginal"
    """.trimIndent()
}

data class FraseEstoica(
    val texto: String,
    val autor: String,
    val enfoque: String // "HACER" o "SER"
)

val listaDeFrases: List<FraseEstoica> = listOf(
    FraseEstoica(
        texto = "Comienza de inmediato a vivir, y cuenta cada día separado como una vida nueva.",
        autor = "Séneca",
        enfoque = "HACER"
    ),
    FraseEstoica(
        texto = "Tienes poder sobre tu mente, no sobre los acontecimientos externos. Date cuenta de esto y verás tu fuerza.",
        autor = "Marco Aurelio",
        enfoque = "SER"
    ),
    FraseEstoica(
        texto = "Tenemos dos oídos y una boca para que podamos escuchar el doble de lo que hablamos.",
        autor = "Zenón de Citio",
        enfoque = "SER"
    ),
    FraseEstoica(
        texto = "Al amanecer, dite: me encontraré con hombres indiscretos, ingratos, insolentes. Yo no puedo ser dañado por ellos.",
        autor = "Marco Aurelio",
        enfoque = "SER"
    ),
    FraseEstoica(
        texto = "La felicidad comienza con una distinción clara: algunas cosas están bajo nuestro control y otras no.",
        autor = "Epicteto",
        enfoque = "HACER"
    ),
    FraseEstoica(
        texto = "Sufres más a menudo en la imaginación que en la realidad.",
        autor = "Séneca",
        enfoque = "SER"
    )
)

/**
 * Banco de datos completo de sabiduría filosófica, estoicismo, reflexión y psicología del bienestar (Lykke).
 * Incluye traducciones internacionales (es, en, fr, it, zh) e idiomas locales mayas (Kaqchiquel 'cak', K'iche' 'quc').
 */
val bancoDeDatosBase: List<ElementoFilosofico> = listOf(
    // FRASES PROPIAS (REINVENTARSE / CONTINUAR)
    ElementoFilosofico(
        id = 101, tipo = "FRASE", autorOrigen = "PROPIO",
        tags = listOf("continuar", "vida", "reinventar", "adelante", "seguir", "fuerza"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(texto = "Debes continuar en la vida, debes de reinventarte.", autorSignatario = "Reflexión"),
            "en" to ContenidoMultilingue(texto = "You must continue in life, you must reinvent yourself.", autorSignatario = "Reflection"),
            "fr" to ContenidoMultilingue(texto = "Tu dois continuer dans la vie, tu dois te réinventer.", autorSignatario = "Réflexion"),
            "it" to ContenidoMultilingue(texto = "Devi continuare nella vita, devi reinventarti.", autorSignatario = "Riflessione"),
            "zh" to ContenidoMultilingue(texto = "你必须继续生活，你必须重塑自己。", autorSignatario = "冥想"),
            "ru" to ContenidoMultilingue(texto = "Ты должен продолжать жить, ты должен переосмыслить и создать себя заново.", autorSignatario = "Размышление"),
            "da" to ContenidoMultilingue(texto = "Du skal fortsætte i livet, du skal genopfinde dig selv.", autorSignatario = "Refleksion"),
            // Lenguas de Guatemala
            "cak" to ContenidoMultilingue(texto = "K'o chi yaban el vave' chi ri k'aslemal, k'o chi yanuk' chic a b'anobil.", autorSignatario = "Na'oj"),
            "quc" to ContenidoMultilingue(texto = "Rajawaxik kakat tzukunik pa le k'aslemal, rajawaxik kak'ex ri a b'anok.", autorSignatario = "No'j"),
            "kek" to ContenidoMultilingue(texto = "K'ub' e re xk'ulub'ankil li yu'am, k'ub' e re xsik'b'al aak'aslemal ut xk'eeb'al aak'a'uxl.", autorSignatario = "Na'leb'"),
            "gar" to ContenidoMultilingue(texto = "Músiti bugumesehani tidan ibagari, músiti abanichuni bahüra.", autorSignatario = "Anichuni"),
            "xin" to ContenidoMultilingue(texto = "Hurra ta k'aslekey k'exoj, hapa ayudadó maku na'u.", autorSignatario = "Na'u")
        )
    ),
    
    // FRASE SOBRE LA ENFERMEDAD
    ElementoFilosofico(
        id = 102, tipo = "FRASE", autorOrigen = "PROPIO",
        tags = listOf("enfermedad", "salud", "tormenta", "invierno", "cuerpo", "forjar", "fin", "sanar", "superar"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(texto = "Puedes superar esta enfermedad como una tormenta de invierno; no define el año, no es el fin. Solo es el antecedente para prepararte y forjarte para que tu cuerpo crezca más.", autorSignatario = "Reflexión"),
            "en" to ContenidoMultilingue(texto = "You can overcome this illness like a winter storm; it does not define the year, it is not the end.", autorSignatario = "Reflection"),
            "fr" to ContenidoMultilingue(texto = "Tu peux surmonter cette maladie comme une tempête d'hiver; elle ne définit pas l'année.", autorSignatario = "Réflexion"),
            "it" to ContenidoMultilingue(texto = "Puoi superare questa malattia como una tempesta d'inverno; non definisce l'anno.", autorSignatario = "Riflessione"),
            "zh" to ContenidoMultilingue(texto = "你能像度过冬日暴风雨一样战胜这场疾病；它不代表这一年。", autorSignatario = "冥想"),
            "ru" to ContenidoMultilingue(texto = "Ты можешь преодолеть эту болезнь, как зимнюю бурю; она не определяет весь год, это не конец.", autorSignatario = "Размышление"),
            "da" to ContenidoMultilingue(texto = "Du kan overvinde denne sygdom som en vinterstorm; den definerer ikke året, den er ikke slutningen.", autorSignatario = "Refleksion"),
            "cak" to ContenidoMultilingue(texto = "Yatkowin xat-el chupam re yab'il re' achi'el jun tewal jab'; re' ma nuk'is ta ri juna'.", autorSignatario = "Na'oj"),
            "quc" to ContenidoMultilingue(texto = "Kaktowin kaxel chupam we yab'il ri' pacha' jun tewal jab'; wa' man kutzup ta ri junab'.", autorSignatario = "No'j"),
            "kek" to ContenidoMultilingue(texto = "Tarwi' aawankil xkolb'al aawib' chiru li yajel pacha' jun hab' k'uula'; ink'a' naraj xyeeb'al li chihab'.", autorSignatario = "Na'leb'"),
            "gar" to ContenidoMultilingue(texto = "Gayanbuga ágamba sandi le kei aban garabali tidan dimaasu; mama ágani luma lila.", autorSignatario = "Anichuni"),
            "xin" to ContenidoMultilingue(texto = "Pul'y yab'il hapašü ma k'o ta tzij, hurra k'aslekey ta'wu.", autorSignatario = "Na'u")
        )
    ),

    // FRASE DEL ESTRÉS (NO EXISTE / COMPORTAMIENTO)
    ElementoFilosofico(
        id = 103, tipo = "FRASE", autorOrigen = "PROPIO",
        tags = listOf("estres", "ansiedad", "emociones", "cuerpo", "aprender", "calma", "sentir"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(texto = "Si crees tener estrés no pasa nada, el estrés no existe. Solo es el comportamiento de tu cuerpo que no ha aprendido a lidiar con sus emociones.", autorSignatario = "Mente Consciente"),
            "en" to ContenidoMultilingue(texto = "If you think you have stress, it's okay, stress doesn't exist. It is only the behavior of your body that has not learned to deal with its emotions.", autorSignatario = "Mindful Mind"),
            "fr" to ContenidoMultilingue(texto = "Si tu penses avoir du stress, ce n'est rien, le stress n'existe pas. C'est le comportement de ton corps qui apprend.", autorSignatario = "Esprit Conscient"),
            "it" to ContenidoMultilingue(texto = "Se credi di avere stress non succede nulla, lo stress non esiste. È solo il corpo che impara con le emociones.", autorSignatario = "Mente Cosciente"),
            "zh" to ContenidoMultilingue(texto = "如果你感到压力，别担心，压力并不存在。那只是身体在适应情绪。", autorSignatario = "正念"),
            "ru" to ContenidoMultilingue(texto = "Если ты чувствуешь стресс — ничего страшного, стресса не существует. Это поведение тела, обучающегося эмоциям.", autorSignatario = "Осознанность"),
            "da" to ContenidoMultilingue(texto = "Hvis du tror, du har stress, er det helt i orden; stress findes ikke. Det er blot kroppens adfærd, som lærer at håndtere følelser.", autorSignatario = "Mindful Bevidsthed"),
            "cak" to ContenidoMultilingue(texto = "We nach'ob' chi k'o jun k'axomal pa a jolom, man k'o ta tzij. Re k'axomal re' ma k'o ta, xa xu' ri a ch'acul ma retamaj ta.", autorSignatario = "Na'oj"),
            "quc" to ContenidoMultilingue(texto = "We nach'ob' chi k'o jun k'axomal pa a jolom, man k'o ta tzij. Re k'axomal re' ma k'o ta, xa xu' ri a ch'acul ma retamaj ta.", autorSignatario = "No'j"),
            "kek" to ContenidoMultilingue(texto = "Wi nach'oxla naq k'o aach'a'ajkilal, maach'a'ajko'; li ch'a'ajkilal maawa' jun na'leb'. Ka'ajwi' xna'leb' aatz'ejwal.", autorSignatario = "Na'leb'"),
            "gar" to ContenidoMultilingue(texto = "Anhein gürigia darangilaü, úati kaban; darangilaü mama katon. Ka'ajwi' xna'leb' aatz'ejwal.", autorSignatario = "Isieni"),
            "xin" to ContenidoMultilingue(texto = "Hapašü üük'aü ma k'o ta tzij, ayudadó maku ta k'exoj.", autorSignatario = "Na'u")
        )
    ),
    ElementoFilosofico(
        id = 4,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("oidos", "boca", "escuchar", "hablar", "prudencia", "silencio"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(titulo = "El Arte de Escuchar", texto = "Tenemos dos oídos y una boca para que podamos escuchar el doble de lo que hablamos.", autor = "Zenón de Citio"),
            "en" to ContenidoMultilingue(titulo = "The Art of Listening", texto = "We have two ears and one mouth, so we may hear more than we speak.", autor = "Zeno of Citium"),
            "fr" to ContenidoMultilingue(titulo = "L'art d'écouter", texto = "Nous avons deux oreilles et une bouche pour écouter deux fois plus que nous ne parlons.", autor = "Zénon de Kition"),
            "it" to ContenidoMultilingue(titulo = "L'arte di ascoltare", texto = "Abbiamo due orecchie e una sola bocca per ascoltare il doppio di quanto parliamo.", autor = "Zenone di Cizio")
        )
    ),
    ElementoFilosofico(
        id = 5,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("mañana", "despertar", "vida", "propósito", "personas", "paz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(titulo = "Meditación Matutina", texto = "Al amanecer, dite: me encontraré con hombres indiscretos, ingratos, insolentes. Yo no puedo ser dañado por ellos.", autor = "Marco Aurelio"),
            "en" to ContenidoMultilingue(titulo = "Morning Reflection", texto = "When you wake up in the morning, tell yourself: the people I deal with today will be meddling, ungrateful, arrogant. I cannot be harmed by them.", autor = "Marcus Aurelius")
        )
    ),
    ElementoFilosofico(
        id = 6,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("control", "juzgar", "libertad", "felicidad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(titulo = "Control y Libertad", texto = "La felicidad comienza con una distinción clara: algunas cosas están bajo nuestro control y otras no.", autor = "Epicteto"),
            "en" to ContenidoMultilingue(titulo = "Control and Freedom", texto = "Happiness and freedom begin with a clear understanding of one principle: some things are within our control, and some things are not.", autor = "Epictetus")
        )
    ),
    ElementoFilosofico(
        id = 7,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("dolor", "sufrimiento", "mente", "imaginacion", "temor"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(titulo = "Imaginación y Sufrimiento", texto = "Sufres más a menudo en la imaginación que en la realidad.", autor = "Séneca"),
            "en" to ContenidoMultilingue(titulo = "Imaginary Suffering", texto = "We suffer more often in imagination than in reality.", autor = "Seneca")
        )
    ),
    ElementoFilosofico(
        id = 601,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("control", "dicotomia", "preocupacion", "ansiedad", "juicio", "libertad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Dicotomía del Control",
                texto = "La felicidad y la calma comienzan con una distinción clara: algunas cosas están bajo nuestro absoluto control (nuestros juicios, intenciones y respuestas) y otras no (las acciones ajenas, el pasado, el resultado). Enfoca tu energía solo en lo que depende de ti.",
                autor = "Epicteto"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Dichotomy of Control",
                texto = "The practice of separating what is up to you from what is not up to you.",
                autor = "Epictetus"
            )
        )
    ),
    ElementoFilosofico(
        id = 602,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("amor fati", "destino", "amar", "aceptacion", "dificultad", "cambio"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Amor Fati",
                texto = "Amor al destino. Aceptar e incluso abrazar todo lo que sucede en la vida, bueno o malo, como algo necesario y útil para moldear tu carácter.",
                autor = "Marco Aurelio & Nietzsche"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Amor Fati",
                texto = "Love of fate. Embracing everything that happens in life as necessary and instructive.",
                autor = "Marcus Aurelius"
            )
        )
    ),
    ElementoFilosofico(
        id = 603,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("memento mori", "muerte", "recordar", "tiempo", "presente", "vida"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Memento Mori",
                texto = "Recuerda que vas a morir. Una práctica serena para valorar el tiempo presente, agradecer estar vivos hoy y no desperdiciar la vida en banalidades.",
                autor = "Séneca"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Memento Mori",
                texto = "Remember that you must die. A timeless reminder to cherish the present moment and live purposefully.",
                autor = "Seneca"
            )
        )
    ),
    ElementoFilosofico(
        id = 604,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("ataraxia", "calma", "tranquilidad", "paz", "serenidad", "desasosiego"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Ataraxia",
                texto = "Tranquilidad espiritual y paz mental imperturbable frente a los deseos desmedidos o temores del entorno.",
                autor = "Epicuro & Pirrón"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Ataraxia",
                texto = "A lucid state of robust inner tranquility free from unnecessary fear and reactive anxiety.",
                autor = "Epicurus"
            )
        )
    ),
    ElementoFilosofico(
        id = 605,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("prosoch", "prosocha", "atencion", "mindfulness", "presencia", "vigilancia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Prosochê",
                texto = "Atención plena y vigilancia constante sobre tus propios pensamientos, impulsos y juicios en el momento presente.",
                autor = "Epicteto"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Prosochê",
                texto = "Continuous mindful awareness and vigilance over one's own faculties of judgment and immediate actions.",
                autor = "Epictetus"
            )
        )
    ),
    ElementoFilosofico(
        id = 606,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("dolor", "sufrimiento", "mente", "imaginacion", "rumiacion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Imaginación y el Dolor",
                texto = "Sufres más a menudo en la imaginación que en la realidad. La mayoría de los temores que anticipas nunca llegan a suceder.",
                autor = "Séneca"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Imaginary Suffering",
                texto = "We suffer more often in imagination than in reality.",
                autor = "Seneca"
            )
        )
    ),
    ElementoFilosofico(
        id = 607,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("obstaculo", "camino", "adversidad", "fuerza", "crecimiento"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Obstáculo es el Camino",
                texto = "El impedimento para la acción hace avanzar la acción. Lo que se interpone en el camino se convierte en el camino.",
                autor = "Marco Aurelio"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Obstacle is the Way",
                texto = "The impediment to action advances action. What stands in the way becomes the way.",
                autor = "Marcus Aurelius"
            )
        )
    ),
    ElementoFilosofico(
        id = 608,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA DANESA",
        tags = listOf("lykke", "felicidad", "bienestar", "sentido", "comunidad", "danes"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Lykke (Felicidad Danesa)",
                texto = "Lykke es la búsqueda equilibrada del bienestar, la confianza interpersonal, la libertad interior y la satisfacción con las cosas cotidianas.",
                autor = "Meik Wiking (Instituto de Investigación de Felicidad de Copenhague)"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Lykke",
                texto = "The Danish word for happiness; rooted in everyday purpose, warm human connection, and trust.",
                autor = "Meik Wiking"
            )
        )
    ),
    ElementoFilosofico(
        id = 609,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA DANESA",
        tags = listOf("hygge", "calidez", "hogar", "intimidad", "confort", "acogedor"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Hygge",
                texto = "La atmósfera acogedora de disfrutar de los placeres simples de la vida en compañía de quienes amamos o en íntima quietud.",
                autor = "Tradición Escandinava"
            )
        )
    ),
    ElementoFilosofico(
        id = 610,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("eudaimonia", "plenitud", "prosperidad", "virtud", "proposito"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Eudaimonía",
                texto = "El florecimiento humano pleno. Vivir de acuerdo con nuestra mejor naturaleza, guiados por la virtud y el bien común.",
                autor = "Aristóteles & Estoicos"
            )
        )
    ),
    ElementoFilosofico(
        id = 611,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("sympatheia", "conexion", "humanidad", "empatia", "solidaridad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Sympatheia",
                texto = "La convicción de que todos los seres humanos estamos interconectados en una red invisible de fraternidad universal. Nadie es una isla solitaria.",
                autor = "Marco Aurelio"
            )
        )
    ),
    ElementoFilosofico(
        id = 612,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("aponia", "dolor", "cuerpo", "salud", "alivio"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Aponía",
                texto = "La ausencia de sufrimiento y dolor físico, permitiendo que la mente habite en serenidad y ligereza.",
                autor = "Epicuro"
            )
        )
    ),
    ElementoFilosofico(
        id = 613,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("apateia", "apatheia", "ecuanimidad", "pasiones", "emocion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Apatheia",
                texto = "Libertad de las pasiones destructivas y los apegos desmedidos; no significa frialdad, sino inmunidad emocional frente a la histeria.",
                autor = "Zenón de Citio"
            )
        )
    ),
    ElementoFilosofico(
        id = 614,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("arete", "excelencia", "virtud", "bondad", "coraje"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Areté",
                texto = "La excelencia moral del carácter. La disposición a actuar con justicia, templanza, coraje y sabiduría en cada situación.",
                autor = "Sócrates"
            )
        )
    ),
    ElementoFilosofico(
        id = 615,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("phronesis", "prudencia", "sabiduria", "discernimiento", "decision"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Phronêsis",
                texto = "Sabiduría práctica o prudencia. El arte de tomar las decisiones correctas en el momento oportuno para el bienestar de todos.",
                autor = "Aristóteles"
            )
        )
    ),
    ElementoFilosofico(
        id = 616,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("oikeiosis", "afinidad", "autocuidado", "familia", "comunidad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Oikeiôsis",
                texto = "El proceso natural de apropiación afectiva: comenzar amándote a ti mismo y extender ese amor en círculos concéntricos a tu familia, amigos y a la humanidad entera.",
                autor = "Hierocles"
            )
        )
    ),
    ElementoFilosofico(
        id = 617,
        tipo = "PALABRA",
        autorOrigen = "ESTOICISMO",
        tags = listOf("epoche", "suspension", "juicio", "pausa", "calma"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Epoché",
                texto = "La suspensión consciente del juicio precipitado. Detener la reacción automática antes de clasificar un evento como trágico.",
                autor = "Escuela Escéptica & Pirrón"
            )
        )
    ),
    ElementoFilosofico(
        id = 618,
        tipo = "CONCEPTOS",
        autorOrigen = "PSICOLOGÍA HUMANISTA",
        tags = listOf("sentido", "frankl", "logoterapia", "esperanza", "proposito"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Voluntad de Sentido",
                texto = "A quien tiene un porqué para vivir, casi cualquier cómo le resulta soportable. La última de las libertades humanas es elegir tu propia actitud ante cualquier circunstancia.",
                autor = "Viktor Frankl"
            )
        )
    ),
    ElementoFilosofico(
        id = 619,
        tipo = "CONCEPTOS",
        autorOrigen = "PSICOLOGÍA ANALÍTICA",
        tags = listOf("sombra", "jung", "autenticidad", "aceptacion", "integracion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Integración de la Sombra",
                texto = "Uno no se ilumina imaginando figuras de luz, sino haciendo consciente su propia oscuridad. Acepta todas tus facetas para ser un ser completo.",
                autor = "Carl Gustav Jung"
            )
        )
    ),
    ElementoFilosofico(
        id = 620,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA ORIENTAL",
        tags = listOf("kintsugi", "heridas", "cicatrices", "oro", "resiliencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Kintsugi",
                texto = "El arte japonés de reparar cerámica rota con oro. Tus cicatrices y vivencias difíciles no son defectos que ocultar, sino insignias de tu resiliencia.",
                autor = "Filosofía Tradicional Japonesa"
            )
        )
    ),
    ElementoFilosofico(
        id = 621,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA ORIENTAL",
        tags = listOf("ikigai", "vocacion", "vida", "mision", "pasion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Ikigai",
                texto = "Tu razón de ser y levantarte cada mañana: la intersección entre lo que amas, lo que se te da bien, lo que el mundo necesita y por lo que puedes vivir.",
                autor = "Tradición de Okinawa"
            )
        )
    ),
    ElementoFilosofico(
        id = 622,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA NÓRDICA",
        tags = listOf("sisu", "coraje", "perseverancia", "fuerza", "invierno"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Sisu",
                texto = "Determinación extraordinaria frente a la adversidad. Cuando los recursos parecen agotados, encuentras una fuerza interior que no sabías que tenías.",
                autor = "Filosofía Finlandesa"
            )
        )
    ),
    ElementoFilosofico(
        id = 623,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA SUECA",
        tags = listOf("lagom", "equilibrio", "medida", "suficiente", "moderacion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Lagom",
                texto = "Ni muy poco, ni demasiado: la cantidad justa. El arte de la moderación y la satisfacción plena con lo suficiente.",
                autor = "Filosofía Sueca"
            )
        )
    ),
    ElementoFilosofico(
        id = 624,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("logos", "razon", "orden", "universo", "armonia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Logos Universal",
                texto = "El principio racional que organiza el cosmos. Al alinear tu pensamiento con la realidad objetiva, disuelves el conflicto interno.",
                autor = "Heráclito & Cleantes"
            )
        )
    ),
    ElementoFilosofico(
        id = 625,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("ira", "enojo", "furia", "reaccion", "freno"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Sobre la Ira",
                texto = "La ira es una locura breve. El mayor remedio para la ira es la postergación: respira antes de hablar o actuar.",
                autor = "Séneca"
            )
        )
    ),
    ElementoFilosofico(
        id = 626,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("manana", "despertar", "gratitud", "privilegio", "luz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Privilegio de Despertar",
                texto = "Cuando te levantes por la mañana, piensa en qué privilegio precioso es estar vivo: respirar, pensar, disfrutar, amar.",
                autor = "Marco Aurelio"
            )
        )
    ),
    ElementoFilosofico(
        id = 627,
        tipo = "CONCEPTOS",
        autorOrigen = "PSICOLOGÍA COGNITIVA",
        tags = listOf("reestructuracion", "distorsiones", "pensamiento", "creencias"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Reestructuración Cognitiva",
                texto = "No son los hechos los que perturban a las personas, sino la interpretación que hacen de esos hechos. Cuestiona tus pensamientos automáticos.",
                autor = "Aaron Beck & Albert Ellis"
            )
        )
    ),
    ElementoFilosofico(
        id = 628,
        tipo = "PALABRA",
        autorOrigen = "BUDISMO",
        tags = listOf("upadana", "apego", "desapego", "aferramiento", "paz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Desapego Consciente",
                texto = "Dejar ir la necesidad de que las cosas sean diferentes de lo que son. El desapego no es no poseer nada, sino que nada te posea a ti.",
                autor = "Gautama Buda"
            )
        )
    ),
    ElementoFilosofico(
        id = 629,
        tipo = "CONCEPTOS",
        autorOrigen = "PSICOLOGÍA POSITIVA",
        tags = listOf("gratitud", "aprecio", "abundancia", "reconocimiento"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Gratitud Activa",
                texto = "Agradecer no cambia lo que tienes, pero transforma lo que tienes en suficiente. Es el antídoto cerebral más eficaz contra la amargura.",
                autor = "Robert Emmons"
            )
        )
    ),
    ElementoFilosofico(
        id = 630,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("kathekon", "deber", "responsabilidad", "coherencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Kathêkon",
                texto = "La acción apropiada y el deber moral cotidiano: hacer lo correcto simplemente porque es lo correcto, sin buscar aplausos.",
                autor = "Zenón de Citio"
            )
        )
    ),
    ElementoFilosofico(
        id = 631,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("libertad", "cadenas", "mente", "prision"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Verdadera Libertad",
                texto = "Nadie es libre si no es dueño de sí mismo. La libertad exterior sin dominio interior no es más que una ilusión.",
                autor = "Epicteto"
            )
        )
    ),
    ElementoFilosofico(
        id = 632,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA ORIENTAL",
        tags = listOf("wabi-sabi", "imperfeccion", "sencillez", "autenticidad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Wabi-Sabi",
                texto = "Apreciar la belleza de las cosas imperfectas, mudables e incompletas. La perfección es fría y artificial; lo humano es cálido e imperfecto.",
                autor = "Estética Zen Japonesa"
            )
        )
    ),
    ElementoFilosofico(
        id = 633,
        tipo = "CONCEPTOS",
        autorOrigen = "MINDFULNESS",
        tags = listOf("respiracion", "ancla", "ansiedad", "oxigeno", "calma"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Ancla de la Respiración",
                texto = "Tu respiración siempre ocurre en el presente. Cada vez que tu mente vuele al futuro o al pasado, regresa suavemente a la sensación de inhalar y exhalar.",
                autor = "Thich Nhat Hanh"
            )
        )
    ),
    ElementoFilosofico(
        id = 634,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("soledad", "retiro", "mente", "silencio", "paz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Ciudadela Interior",
                texto = "Los hombres buscan retiros en el campo, en la costa o en los montes; pero en ninguna parte se retira uno con mayor sosiego y tranquilidad que en su propia alma.",
                autor = "Marco Aurelio"
            )
        )
    ),
    ElementoFilosofico(
        id = 635,
        tipo = "CONCEPTOS",
        autorOrigen = "PSICOLOGÍA COGNITIVA",
        tags = listOf("autocompasion", "neff", "amabilidad", "critica"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Autocompasión Serena",
                texto = "Trátate con la misma amabilidad y comprensión con la que tratarías a un amigo querido que está atravesando un momento difícil.",
                autor = "Kristin Neff"
            )
        )
    ),
    ElementoFilosofico(
        id = 636,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("tiempo", "desperdicio", "vida", "aprovechar"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "De la Brevedad de la Vida",
                texto = "No tenemos poco tiempo, sino que perdemos mucho. La vida es suficientemente larga si sabemos emplearla con sabiduría.",
                autor = "Séneca"
            )
        )
    ),
    ElementoFilosofico(
        id = 637,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA CLÁSICA",
        tags = listOf("autognosis", "conocerse", "oraculo", "verdad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Conócete a Ti Mismo (Gnothi Seauton)",
                texto = "El examen constante de nuestros motivos profundos y deseos es la clave de toda sabiduría y tranquilidad perdurable.",
                autor = "Oráculo de Delfos & Sócrates"
            )
        )
    ),
    ElementoFilosofico(
        id = 638,
        tipo = "PALABRA",
        autorOrigen = "FILOSOFÍA NÓRDICA",
        tags = listOf("friluftsliv", "naturaleza", "aire libre", "bosque", "salud"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Friluftsliv",
                texto = "La conexión profunda con el aire libre y la naturaleza para restaurar la calma psíquica y la claridad del espíritu.",
                autor = "Henrik Ibsen & Tradición Nórdica"
            )
        )
    ),
    ElementoFilosofico(
        id = 639,
        tipo = "CONCEPTOS",
        autorOrigen = "ESTOICISMO",
        tags = listOf("premeditatio", "malorum", "anticipacion", "preparacion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Premeditatio Malorum",
                texto = "Visualizar anticipadamente los posibles contratiempos no para asustarse, sino para desarmar la sorpresa y saber responder con serenidad.",
                autor = "Séneca"
            )
        )
    ),
    ElementoFilosofico(
        id = 640,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("opinion", "injuria", "ofensa", "dano"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Ofensa y la Interpretación",
                texto = "Si alguien te agravia o te ofende, recuerda que solo tu juicio puede causarte daño. Si eliminas el 'se me ha hecho daño', el daño desaparece.",
                autor = "Marco Aurelio"
            )
        )
    )
)

val bancoDeDatosPaqueteExtendido: List<ElementoFilosofico> = listOf(
    // 801 - Rumi
    ElementoFilosofico(
        id = 801,
        tipo = "CONCEPTOS",
        autorOrigen = "MISTICISMO SUFÍ",
        tags = listOf("rumi", "luz", "amor", "corazon", "sufi", "misticismo", "herida", "sanar"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Herida y la Luz",
                texto = "La herida es el lugar por donde la luz entra en ti. No te lamentes por el quiebre: es la apertura hacia tu verdadero ser.",
                autor = "Djalâl ad-Dîn Rûmî",
                autorSignatario = "Rumi"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Wound and the Light",
                texto = "The wound is the place where the Light enters you.",
                autor = "Rumi",
                autorSignatario = "Rumi"
            ),
            "da" to ContenidoMultilingue(
                titulo = "Såret og Lyset",
                texto = "Såret er stedet, hvor lyset trænger ind i dig.",
                autor = "Rumi",
                autorSignatario = "Rumi"
            )
        )
    ),
    // 802 - Lao Tse
    ElementoFilosofico(
        id = 802,
        tipo = "CONCEPTOS",
        autorOrigen = "TAOÍSMO",
        tags = listOf("wu wei", "lao tse", "tao", "fluir", "agua", "calma", "sencillez", "serenidad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Wu Wei (El Fluir del Agua)",
                texto = "El agua es suave y flexible, pero vence a lo más duro. Practica el no forzar: fluye con la naturaleza y nada quedará sin hacerse.",
                autor = "Lao Tse",
                autorSignatario = "Tao Te Ching"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Wu Wei",
                texto = "Water is fluid, soft, and yielding. But water will wear away rock, which is rigid and cannot yield.",
                autor = "Lao Tzu",
                autorSignatario = "Tao Te Ching"
            )
        )
    ),
    // 803 - Baruch Spinoza
    ElementoFilosofico(
        id = 803,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA MODERNA",
        tags = listOf("spinoza", "dios", "naturaleza", "alegria", "etica", "afectos", "panteismo"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Amor Intelectual de la Naturaleza",
                texto = "La alegría es el paso del ser humano de una menor a una mayor perfección. Comprender que todo forma parte de un orden divino natural disipa el rencor.",
                autor = "Baruch Spinoza",
                autorSignatario = "Ética"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Joy and Understanding",
                texto = "Joy is the transition of a person from a lesser to a greater perfection.",
                autor = "Baruch Spinoza",
                autorSignatario = "Ethics"
            )
        )
    ),
    // 804 - Søren Kierkegaard
    ElementoFilosofico(
        id = 804,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA DANESA",
        tags = listOf("kierkegaard", "danes", "fe", "angustia", "salto", "autenticidad", "existencia", "lykke"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Salto hacia la Autenticidad",
                texto = "La angustia es el vértigo de la libertad. Quien aprende a angustiarse con sabiduría ha aprendido la más alta verdad del alma.",
                autor = "Søren Kierkegaard",
                autorSignatario = "Kierkegaard (Dinamarca)"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Leap of Faith",
                texto = "Anxiety is the dizziness of freedom.",
                autor = "Søren Kierkegaard",
                autorSignatario = "Kierkegaard"
            ),
            "da" to ContenidoMultilingue(
                titulo = "Troens Spring",
                texto = "Angst er frihedens svimmelhed. At turde er at miste fodfæste et øjeblik; ikke at turde er at miste sig selv.",
                autor = "Søren Kierkegaard",
                autorSignatario = "Kierkegaard"
            )
        )
    ),
    // 805 - San Francisco de Asís
    ElementoFilosofico(
        id = 805,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD CRISTIANA",
        tags = listOf("san francisco", "paz", "creacion", "naturaleza", "hermano", "serenidad", "humildad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Instrumento de Paz",
                texto = "Donde haya odio, siembre yo amor; donde haya ofensa, perdón; donde haya discordia, unión; donde haya duda, fe.",
                autor = "San Francisco de Asís",
                autorSignatario = "Cántico de las Criaturas"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Instrument of Peace",
                texto = "Lord, make me an instrument of your peace: where there is hatred, let me sow love.",
                autor = "Francis of Assisi",
                autorSignatario = "Francis of Assisi"
            )
        )
    ),
    // 806 - San Agustín de Hipona
    ElementoFilosofico(
        id = 806,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD CLÁSICA",
        tags = listOf("san agustin", "inquieto", "corazon", "interior", "verdad", "alma", "quietud"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Quietud del Corazón",
                texto = "Nos hiciste para ti, y nuestro corazón está inquieto hasta que descanse en la paz interior y la verdad eterna.",
                autor = "San Agustín de Hipona",
                autorSignatario = "Confesiones"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Restless Heart",
                texto = "Our heart is restless until it rests in genuine inner peace and eternal truth.",
                autor = "Saint Augustine",
                autorSignatario = "Confessions"
            )
        )
    ),
    // 807 - Santo Tomás de Aquino
    ElementoFilosofico(
        id = 807,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA ESCOLÁSTICA",
        tags = listOf("tomas de aquino", "sabiduria", "razon", "orden", "alma", "paz", "verdad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Paz como Tranquilidad del Orden",
                texto = "La paz interior no es la mera ausencia de conflicto exterior, sino la tranquilidad que nace del orden armonioso de nuestros deseos y afectos.",
                autor = "Santo Tomás de Aquino",
                autorSignatario = "Summa Theologiae"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Tranquility of Order",
                texto = "Peace is the tranquility of order in the soul.",
                autor = "Thomas Aquinas",
                autorSignatario = "Summa Theologiae"
            )
        )
    ),
    // 808 - Santa Teresa de Ávila & San Juan de la Cruz
    ElementoFilosofico(
        id = 808,
        tipo = "CONCEPTOS",
        autorOrigen = "MÍSTICA RENACENTISTA",
        tags = listOf("teresa de avila", "juan de la cruz", "morada", "castillo", "silencio", "alma", "noche oscura"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Castillo Interior",
                texto = "Nada te turbe, nada te espante, todo se pasa, la paciencia todo lo alcanza. Dentro de ti existe un castillo de luz inextinguible.",
                autor = "Santa Teresa de Jesús & San Juan de la Cruz",
                autorSignatario = "Las Moradas"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Interior Castle",
                texto = "Let nothing disturb you, let nothing frighten you. All things pass; patience obtains all things.",
                autor = "Teresa of Ávila",
                autorSignatario = "The Interior Castle"
            )
        )
    ),
    // 809 - Confucio
    ElementoFilosofico(
        id = 809,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA ORIENTAL",
        tags = listOf("confucio", "ren", "benevolencia", "respeto", "armonia", "familia", "nobleza"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Ren (La Benevolencia Cordial)",
                texto = "No hagas a otros lo que no deseas para ti mismo. El hombre noble busca en sí mismo la templanza; el hombre vulgar la exige en los demás.",
                autor = "Confucio",
                autorSignatario = "Analectas"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Ren and Harmony",
                texto = "What you do not wish for yourself, do not do to others.",
                autor = "Confucius",
                autorSignatario = "Analects"
            ),
            "zh" to ContenidoMultilingue(
                titulo = "仁与礼",
                texto = "己所不欲，勿施于人。君子求诸己，小人求诸人。",
                autor = "孔子",
                autorSignatario = "论语"
            )
        )
    ),
    // 810 - Bhagavad Gita
    ElementoFilosofico(
        id = 810,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD VÉDICA",
        tags = listOf("gita", "karma", "krishna", "accion", "deber", "ecuanimidad", "desapego"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Nishkama Karma (Acción Sagrada)",
                texto = "Tienes derecho al trabajo y al esfuerzo noble, pero nunca a los frutos de la acción. Actúa con total entrega y permanece ecuánime ante el éxito o el fracaso.",
                autor = "Bhagavad Gita",
                autorSignatario = "Sri Krishna"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Action without Attachment",
                texto = "You have a right to perform your prescribed duties, but you are not entitled to the fruits of your actions.",
                autor = "Bhagavad Gita",
                autorSignatario = "Bhagavad Gita"
            )
        )
    ),
    // 811 - Swami Vivekananda
    ElementoFilosofico(
        id = 811,
        tipo = "CONCEPTOS",
        autorOrigen = "VEDANTA ADVAITA",
        tags = listOf("vivekananda", "fuerza", "levantate", "divinidad", "espiritu", "potencial", "despertar"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Divinidad Interior",
                texto = "¡Levántate, despierta y no te detengas hasta alcanzar la meta! Todo el poder del universo ya está dentro de ti.",
                autor = "Swami Vivekananda",
                autorSignatario = "Vedanta"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Arise and Awake",
                texto = "Arise, awake, and stop not until the goal is reached.",
                autor = "Swami Vivekananda",
                autorSignatario = "Swami Vivekananda"
            )
        )
    ),
    // 812 - Ralph Waldo Emerson
    ElementoFilosofico(
        id = 812,
        tipo = "CONCEPTOS",
        autorOrigen = "TRASCENDENTALISMO",
        tags = listOf("emerson", "confianza", "autonomia", "alma", "genio", "naturaleza", "self-reliance"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Confianza en Uno Mismo",
                texto = "Confía en ti mismo: todo corazón vibra ante esa cuerda de hierro. Quien es fiel a su luz interior nunca se sentirá desamparado.",
                autor = "Ralph Waldo Emerson",
                autorSignatario = "Self-Reliance"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Self-Reliance",
                texto = "Trust thyself: every heart vibrates to that iron string.",
                autor = "Ralph Waldo Emerson",
                autorSignatario = "Emerson"
            )
        )
    ),
    // 813 - Henry David Thoreau
    ElementoFilosofico(
        id = 813,
        tipo = "CONCEPTOS",
        autorOrigen = "TRASCENDENTALISMO",
        tags = listOf("thoreau", "simplicidad", "walden", "deliberada", "esencial", "naturaleza", "bosque"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Vida Deliberada en Walden",
                texto = "Fui a los bosques porque deseaba vivir deliberadamente, enfrentar solo los hechos esenciales de la vida y ver si podía aprender lo que ella tenía que enseñar.",
                autor = "Henry David Thoreau",
                autorSignatario = "Walden"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Deliberate Life",
                texto = "I went to the woods because I wished to live deliberately, to front only the essential facts of life.",
                autor = "Henry David Thoreau",
                autorSignatario = "Walden"
            )
        )
    ),
    // 814 - Kahlil Gibran
    ElementoFilosofico(
        id = 814,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD POÉTICA",
        tags = listOf("kahlil gibran", "profeta", "dolor", "comprension", "alegria", "tristeza", "corazon"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Ruptura del Caparazón",
                texto = "Tu dolor es la ruptura del caparazón que encierra tu comprensión. Cuanto más hondo cave la tristeza en tu ser, más alegría podrás contener.",
                autor = "Kahlil Gibran",
                autorSignatario = "El Profeta"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Shell of Understanding",
                texto = "Your pain is the breaking of the shell that encloses your understanding.",
                autor = "Kahlil Gibran",
                autorSignatario = "The Prophet"
            )
        )
    ),
    // 815 - Popol Vuh
    ElementoFilosofico(
        id = 815,
        tipo = "CONCEPTOS",
        autorOrigen = "COSMOGONÍA MAYA",
        tags = listOf("popol vuh", "maya", "ajaw", "nawal", "tierra", "creador", "raiz", "maiz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Tz'akol B'itol (Armonía con la Tierra)",
                texto = "Que todos se levanten, que nadie se quede atrás. El corazón del cielo y el corazón de la tierra nos tejen en una sola familia viva.",
                autor = "Popol Vuh",
                autorSignatario = "Sabiduría Ancestral K'iche'"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Heart of Sky, Heart of Earth",
                texto = "May all rise up, may no one be left behind. Heart of Sky and Heart of Earth sustain us as one living family.",
                autor = "Popol Vuh",
                autorSignatario = "Maya K'iche'"
            ),
            "cak" to ContenidoMultilingue(
                titulo = "Uk'u'x Kaj, Uk'u'x Ulew",
                texto = "Konojel kiyakatäj, majun k'o chi kakanäj. Ri ruk'u'x kaj chuqa' ri ruk'u'x ulew nuya' qak'aslemal.",
                autor = "Popol Vuh",
                autorSignatario = "Maya Na'oj"
            ),
            "quc" to ContenidoMultilingue(
                titulo = "Tz'akol B'itol",
                texto = "Konojel kiyakatajok, man k'o ta jun kakanoq. Ri uk'u'x kaj xuquje' uk'u'x ulew kuya ri junam k'aslemal.",
                autor = "Popol Vuh",
                autorSignatario = "No'jib'al"
            )
        )
    ),
    // 816 - Hermes Trismegisto
    ElementoFilosofico(
        id = 816,
        tipo = "CONCEPTOS",
        autorOrigen = "TRADICIÓN HERMÉTICA",
        tags = listOf("hermes", "correspondencia", "adentro", "afuera", "mente", "polaridad", "ritmo"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Principio de Correspondencia",
                texto = "Como es adentro, es afuera; como es arriba, es abajo. Pacifica tu universo interior y el exterior responderá con serenidad.",
                autor = "Hermes Trismegisto",
                autorSignatario = "El Kybalión"
            ),
            "en" to ContenidoMultilingue(
                titulo = "As Within, So Without",
                texto = "As above, so below; as within, so without. Bring peace to your inner world and harmony will reflect outwardly.",
                autor = "Hermes Trismegistus",
                autorSignatario = "The Kybalion"
            )
        )
    ),
    // 817 - Al-Ghazali
    ElementoFilosofico(
        id = 817,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD ISLÁMICA",
        tags = listOf("al-ghazali", "alquimia", "felicidad", "corazon", "purificacion", "alma", "espejo"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Alquimia de la Felicidad",
                texto = "El corazón es como un espejo: cuando se limpia del polvo del rencor y los deseos desmedidos, refleja con claridad la verdad y la paz divina.",
                autor = "Al-Ghazali",
                autorSignatario = "Kimiya-yi Sa'adat"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Alchemy of Happiness",
                texto = "The heart is like a mirror; when cleansed of excessive vanity and anger, it reflects genuine serenity.",
                autor = "Al-Ghazali",
                autorSignatario = "The Alchemy of Happiness"
            )
        )
    ),
    // 818 - Meister Eckhart
    ElementoFilosofico(
        id = 818,
        tipo = "CONCEPTOS",
        autorOrigen = "MÍSTICA RENANA",
        tags = listOf("eckhart", "gelassenheit", "desprendimiento", "serenidad", "fondo", "alma", "gracia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Gelassenheit (El Sagrado Dejar Ir)",
                texto = "Si la única oración que dijeras en toda tu vida fuera 'gracias', con eso bastaría. Suelta el afán de controlar y hallarás el fondo intocado de tu alma.",
                autor = "Meister Eckhart",
                autorSignatario = "Tratados Espirituales"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Gelassenheit",
                texto = "If the only prayer you ever say in your entire life is thank you, it will be enough.",
                autor = "Meister Eckhart",
                autorSignatario = "Meister Eckhart"
            )
        )
    ),
    // 819 - Simone Weil
    ElementoFilosofico(
        id = 819,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA CONTEMPORÁNEA",
        tags = listOf("simone weil", "atencion", "gracia", "pureza", "pensamiento", "espera", "escucha"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Atención Pura",
                texto = "La atención absoluta y desinteresada es la forma más pura de generosidad y oración. Mirar al prójimo y preguntarle con verdad: '¿Cuál es tu pena?'",
                autor = "Simone Weil",
                autorSignatario = "La Gravedad y la Gracia"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Pure Attention",
                texto = "Attention, taken to its highest degree, is the same thing as prayer. It presupposes faith and love.",
                autor = "Simone Weil",
                autorSignatario = "Gravity and Grace"
            )
        )
    ),
    // 820 - Rabindranath Tagore
    ElementoFilosofico(
        id = 820,
        tipo = "CONCEPTOS",
        autorOrigen = "POESÍA ESPIRITUAL",
        tags = listOf("tagore", "luz", "pajaro", "amanecer", "fe", "esperanza", "musica"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Fe del Pájaro",
                texto = "La fe es el pájaro que siente la luz y canta cuando el amanecer todavía está oscuro.",
                autor = "Rabindranath Tagore",
                autorSignatario = "Gitanjali"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Faith of the Bird",
                texto = "Faith is the bird that feels the light and sings when the dawn is still dark.",
                autor = "Rabindranath Tagore",
                autorSignatario = "Gitanjali"
            )
        )
    ),
    // 821 - Dōgen Zenji
    ElementoFilosofico(
        id = 821,
        tipo = "CONCEPTOS",
        autorOrigen = "ZEN JAPONÉS",
        tags = listOf("dogen", "zen", "shikantaza", "sentarse", "presencia", "respirar", "iluminacion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Shikantaza (El Solo Sentarse)",
                texto = "Estudiar el camino es estudiarse a uno mismo. Estudiarse a uno mismo es olvidarse de uno mismo. Olvidarse de uno mismo es ser iluminado por todas las cosas.",
                autor = "Dōgen Zenji",
                autorSignatario = "Shōbōgenzō"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Shikantaza",
                texto = "To study the self is to forget the self. To forget the self is to be actualized by myriad things.",
                autor = "Dōgen Zenji",
                autorSignatario = "Shōbōgenzō"
            )
        )
    ),
    // 822 - Immanuel Kant
    ElementoFilosofico(
        id = 822,
        tipo = "CONCEPTOS",
        autorOrigen = "ILUSTRACIÓN ÉTICA",
        tags = listOf("kant", "moral", "cielo", "conciencia", "deber", "admiracion", "estrella"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Asombro Interior y Exterior",
                texto = "Dos cosas llenan el ánimo de admiración y respeto siempre nuevos: el cielo estrellado sobre mí y la ley moral dentro de mí.",
                autor = "Immanuel Kant",
                autorSignatario = "Crítica de la Razón Práctica"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Starry Heavens and Moral Law",
                texto = "Two things fill the mind with ever new and increasing admiration: the starry heavens above me and the moral law within me.",
                autor = "Immanuel Kant",
                autorSignatario = "Critique of Practical Reason"
            )
        )
    ),
    // 823 - Baruch Spinoza
    ElementoFilosofico(
        id = 823,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA ÉTICA",
        tags = listOf("conatus", "perseverar", "vida", "impulso", "energia", "vitalidad", "fuerza"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Conatus (El Impulso Vital)",
                texto = "Cada cosa, en cuanto está en ella, se esfuerza por perseverar en su ser. Tu impulso natural es vivir, florecer y multiplicar tu alegría.",
                autor = "Baruch Spinoza",
                autorSignatario = "Ética Demostrada"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Conatus",
                texto = "Each thing, in so far as it is in itself, endeavours to persist in its own being.",
                autor = "Baruch Spinoza",
                autorSignatario = "Ethics"
            )
        )
    ),
    // 824 - Blaise Pascal
    ElementoFilosofico(
        id = 824,
        tipo = "CONCEPTOS",
        autorOrigen = "PENSAMIENTO EXISTENCIAL",
        tags = listOf("pascal", "corazon", "razon", "asombro", "quietud", "infinito", "silencio"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Las Razones del Corazón",
                texto = "El corazón tiene sus razones que la razón no comprende del todo. Toda la infelicidad del ser humano procede de no saber permanecer tranquilamente a solas consigo mismo.",
                autor = "Blaise Pascal",
                autorSignatario = "Pensamientos"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Reasons of the Heart",
                texto = "The heart has its reasons of which reason knows nothing.",
                autor = "Blaise Pascal",
                autorSignatario = "Pensées"
            )
        )
    ),
    // 825 - Friedrich Nietzsche
    ElementoFilosofico(
        id = 825,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA VITALISTA",
        tags = listOf("nietzsche", "fuerza", "voluntad", "superacion", "porque", "como", "destino"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Sentido de la Fortaleza",
                texto = "Aquel que tiene un porqué para vivir puede soportar casi cualquier cómo. Lo que no me destruye, me fortalece.",
                autor = "Friedrich Nietzsche",
                autorSignatario = "Crepúsculo de los Ídolos"
            ),
            "en" to ContenidoMultilingue(
                titulo = "A Why to Live",
                texto = "He who has a why to live can bear almost any how.",
                autor = "Friedrich Nietzsche",
                autorSignatario = "Nietzsche"
            )
        )
    ),
    // 826 - Martin Buber
    ElementoFilosofico(
        id = 826,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA DEL DIÁLOGO",
        tags = listOf("buber", "yo-tu", "encuentro", "relacion", "dialogo", "humanidad", "presencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Principio Dialógico Yo-Tú",
                texto = "Toda vida verdadera es encuentro. Cuando te relacionas con el otro no como objeto utilizable, sino como un 'Tú' sagrado, el universo entero adquiere sentido.",
                autor = "Martin Buber",
                autorSignatario = "Yo y Tú"
            ),
            "en" to ContenidoMultilingue(
                titulo = "I and Thou",
                texto = "All real living is meeting.",
                autor = "Martin Buber",
                autorSignatario = "I and Thou"
            )
        )
    ),
    // 827 - Pierre Teilhard de Chardin
    ElementoFilosofico(
        id = 827,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD CÓSMICA",
        tags = listOf("teilhard", "evolucion", "espiritu", "experiencia", "cosmos", "amor", "convergencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Seres Espirituales",
                texto = "No somos seres humanos viviendo una experiencia espiritual; somos seres espirituales viviendo una experiencia humana.",
                autor = "Pierre Teilhard de Chardin",
                autorSignatario = "El Fenómeno Humano"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Spiritual Beings",
                texto = "We are not human beings having a spiritual experience; we are spiritual beings having a human experience.",
                autor = "Pierre Teilhard de Chardin",
                autorSignatario = "Teilhard de Chardin"
            )
        )
    ),
    // 828 - San Juan Crisóstomo
    ElementoFilosofico(
        id = 828,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD CLÁSICA",
        tags = listOf("crisostomo", "olas", "tempestad", "ancla", "fe", "calma", "mar"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Calma en la Tempestad",
                texto = "Las olas pueden rugir y el viento arreciar, pero el barco no zozobra cuando el ancla de la fe y la calma están firmes en la profundidad del alma.",
                autor = "San Juan Crisóstomo",
                autorSignatario = "Homilías"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Calm amid the Waves",
                texto = "The waters have risen and severe storms are upon us, but we do not fear drowning, for we stand upon a firm rock.",
                autor = "John Chrysostom",
                autorSignatario = "Homilies"
            )
        )
    ),
    // 829 - Nagarjuna
    ElementoFilosofico(
        id = 829,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA BUDISTA",
        tags = listOf("nagarjuna", "camino medio", "interdependencia", "vacuidad", "paz", "desapego"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Camino Medio y la Interdependencia",
                texto = "Ninguna cosa existe de forma aislada e independiente; todo surge en mutua interconexión. Al comprender esto, el aferramiento al ego se disuelve y florece la paz.",
                autor = "Nagarjuna",
                autorSignatario = "Mulamadhyamakakarika"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Middle Way",
                texto = "Whatever is dependently co-arisen, that is explained to be emptiness and the middle way.",
                autor = "Nagarjuna",
                autorSignatario = "Nagarjuna"
            )
        )
    ),
    // 830 - Plotino
    ElementoFilosofico(
        id = 830,
        tipo = "CONCEPTOS",
        autorOrigen = "NEOPLATONISMO",
        tags = listOf("plotino", "lo uno", "belleza", "alma", "ascenso", "interior", "escultor"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Esculpe tu Propia Estatua",
                texto = "Retírate dentro de ti mismo y mira. Si aún no te ves bello, haz como el escultor: quita lo superfluo, endereza lo torcido y no ceses hasta que brille la pura luz interior.",
                autor = "Plotino",
                autorSignatario = "Enéadas"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Sculpt Your Own Statue",
                texto = "Withdraw into yourself and look. If you do not yet see yourself as beautiful, do as the sculptor of a statue does: cut away what is excessive.",
                autor = "Plotinus",
                autorSignatario = "Enneads"
            )
        )
    ),
    // 831 - Séneca & Lucilio
    ElementoFilosofico(
        id = 831,
        tipo = "FRASE",
        autorOrigen = "ESTOICISMO",
        tags = listOf("lucilio", "seneca", "nobleza", "amigo", "consejo", "sabiduria", "tiempo"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Cartas de Amistad y Serenidad",
                texto = "Reclámate para ti mismo: recupera y conserva el tiempo que hasta ahora te quitaban, te sustraían o se te escapaba. Quien tiene un amigo sabio nunca camina solo.",
                autor = "Séneca a Lucilio",
                autorSignatario = "Epístolas Morales a Lucilio"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Letters to Lucilius",
                texto = "Hold every hour in your grasp. Lay hold of to-day's task, and you will not need to depend so much upon to-morrow's.",
                autor = "Seneca to Lucilius",
                autorSignatario = "Letters to Lucilius"
            )
        )
    ),
    // 832 - Chuang Tzu (Zhuangzi)
    ElementoFilosofico(
        id = 832,
        tipo = "CONCEPTOS",
        autorOrigen = "TAOÍSMO CLÁSICO",
        tags = listOf("chuang tzu", "zhuangzi", "mariposa", "libertad", "vuelo", "perspectiva", "sueno"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Sueño de la Mariposa y la Gran Perspectiva",
                texto = "Una vez soñé que era una mariposa que revoloteaba feliz, libre de preocupaciones. Mira tus tribulaciones desde las alturas del cielo y verás cuán pequeñas son.",
                autor = "Chuang Tzu (Zhuangzi)",
                autorSignatario = "Zhuangzi"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Butterfly Dream",
                texto = "Once upon a time, I dreamt I was a butterfly, fluttering hither and thither, joyful and doing as I pleased.",
                autor = "Zhuangzi",
                autorSignatario = "Zhuangzi"
            )
        )
    ),
    // 833 - Santa Hildegarda de Bingen (CONCEPTO REQUERIDO)
    ElementoFilosofico(
        id = 833,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD Y MÍSTICA MEDIEVAL",
        tags = listOf("viriditas", "fuerza verde", "hildegarda", "vitalidad", "creacion", "sanacion", "espiritualidad", "salud"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Viriditas (La Fuerza Verde Vivificante)",
                texto = "Viriditas es el hálito verde y fecundante de Dios que late en cada hierba, en cada árbol y en el fondo viviente de tu cuerpo y alma. Confía en tu poder natural de regeneración y sanación.",
                autor = "Santa Hildegarda de Bingen",
                autorSignatario = "Scivias & Physica"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Viriditas (The Greening Power)",
                texto = "Viriditas is the divine greening life-force animating every leaf and renewing the human soul and body with healing vitality.",
                autor = "Saint Hildegard of Bingen",
                autorSignatario = "Hildegard of Bingen"
            ),
            "da" to ContenidoMultilingue(
                titulo = "Viriditas (Den Grønne Livskraft)",
                texto = "Viriditas er den guddommelige grønne kraft, som fornyer sjælen og skaber helbredende harmoni i kroppen.",
                autor = "Hildegard af Bingen",
                autorSignatario = "Hildegard af Bingen"
            ),
            "fr" to ContenidoMultilingue(
                titulo = "Viriditas (La Force Verte)",
                texto = "Viriditas est le souffle vert et vivifiant qui anime la création et régénère l'esprit et le corps.",
                autor = "Sainte Hildegarde de Bingen",
                autorSignatario = "Hildegarde de Bingen"
            ),
            "it" to ContenidoMultilingue(
                titulo = "Viriditas (La Forza Verdeggiante)",
                texto = "Viriditas è la forza vitale e rinvigorente che abita nella natura e ristabilisce la pace del corpo e dell'anima.",
                autor = "Santa Ildegarda di Bingen",
                autorSignatario = "Ildegarda di Bingen"
            ),
            "cak" to ContenidoMultilingue(
                titulo = "Rax K'aslemal (Viriditas)",
                texto = "Ri ruxla' k'aslemal k'o pa konojel che' chuqa' pa aq'a' qaxukulil richin ri kunanïk.",
                autor = "Hildegarda de Bingen",
                autorSignatario = "Na'oj"
            ),
            "quc" to ContenidoMultilingue(
                titulo = "Rax Uk'u'x K'aslemal",
                texto = "Ri rax k'aslemal kuya uchuq'ab' che le qab'anuk' richin ri utzilaj k'aslemal.",
                autor = "Hildegarda de Bingen",
                autorSignatario = "No'j"
            )
        )
    ),
    // 834 - Thomas Merton (CONCEPTO REQUERIDO)
    ElementoFilosofico(
        id = 834,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD Y CONTEMPLACIÓN",
        tags = listOf("thomas merton", "merton", "soledad", "compasion", "monasterio", "conexion", "despertar", "luz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Despertar de la Compasión Universal",
                texto = "En el centro de nuestro ser hay un punto de nada que no es tocado por el pecado ni por la ilusión: es un punto de pura verdad y gracia donde todos brillamos como el sol. No somos extraños entre nosotros.",
                autor = "Thomas Merton",
                autorSignatario = "Conjeturas de un Espectador Culpable"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Point of Pure Truth",
                texto = "At the center of our being is a point of nothingness which is untouched by illusion, a spark of pure truth where all of us shine like the sun.",
                autor = "Thomas Merton",
                autorSignatario = "Thomas Merton"
            ),
            "da" to ContenidoMultilingue(
                titulo = "Universel Medfølelse",
                texto = "I centrum af vort væsen er et punkt af ren sandhed, hvor vi alle skinner sammen som solen.",
                autor = "Thomas Merton",
                autorSignatario = "Thomas Merton"
            )
        )
    ),
    // 835 - Djalâl ad-Dîn Rûmî (CONCEPTO REQUERIDO)
    ElementoFilosofico(
        id = 835,
        tipo = "FRASE",
        autorOrigen = "MISTICISMO SUFÍ",
        tags = listOf("rumi", "silencio", "universo", "musica", "alma", "eco", "trascendencia", "amor"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Silencio donde Habla el Universo",
                texto = "El silencio es el lenguaje de Dios; todo lo demás es una pobre traducción. Haz una pausa en tu mente, aquieta los ruidos y escucha la música eterna que sostiene tu corazón.",
                autor = "Djalâl ad-Dîn Rûmî",
                autorSignatario = "Masnavi"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Language of Silence",
                texto = "Silence is the language of God, all else is poor translation. Still your thoughts and listen to the eternal melody of your heart.",
                autor = "Rumi",
                autorSignatario = "Masnavi"
            ),
            "da" to ContenidoMultilingue(
                titulo = "Stilhedens Sprog",
                texto = "Stilheden er Guds sprog; alt andet er en fattig oversættelse. Lyt til dit hjertes evige melodi.",
                autor = "Rumi",
                autorSignatario = "Rumi"
            )
        )
    ),
    // 836 - Maya K'iche' Ancestral (CONCEPTO REQUERIDO)
    ElementoFilosofico(
        id = 836,
        tipo = "CONCEPTOS",
        autorOrigen = "ESPIRITUALIDAD MAYA K'ICHE'",
        tags = listOf("kaslemal", "k'aslemal", "nawal", "nawales", "equilibrio", "kiche", "maya", "ajaw", "sagrado", "vida", "armonia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Sagrado K'aslemal y los 20 Nawales",
                texto = "El K'aslemal es la plenitud de la vida en armonía con los 20 Nawales del Cholq'ij. Cada día tiene su energía sagrada, su propósito y su protector espiritual para restaurar el equilibrio en tu cuerpo, mente y comunidad.",
                autor = "Sabiduría Ancestral Maya K'iche'",
                autorSignatario = "Ajq'ijab' (Guías Espirituales Mayas)"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Sacred K'aslemal and the 20 Nawales",
                texto = "K'aslemal is the fullness of living in sacred balance with the 20 Nawales. Every single day carries a protective energy to renew body, mind, and community.",
                autor = "Maya K'iche' Ancestral Wisdom",
                autorSignatario = "Cholq'ij Tradition"
            ),
            "cak" to ContenidoMultilingue(
                titulo = "Ri Saqil K'aslemal",
                texto = "Ri K'aslemal ja ri junam k'aslemal ruk' ri Nawales richin ri Cholq'ij richin nqatzukuj ri utzilaj b'anob'äl.",
                autor = "Ajq'ijab'",
                autorSignatario = "Kaqchikel Na'oj"
            ),
            "quc" to ContenidoMultilingue(
                titulo = "Ri K'aslemal rech le 20 Nawales",
                texto = "Ri K'aslemal are' ri tz'aqat k'aslem ruk' le 20 Nawales rech le Cholq'ij richin ri jamaril pa le qak'u'x xuquje' le qatinamit.",
                autor = "Ajq'ijab'",
                autorSignatario = "No'jib'al Maya K'iche'"
            ),
            "kek" to ContenidoMultilingue(
                titulo = "Li Santil Yu'am",
                texto = "Li Yu'am a'an li xk'eeb'al qach'ool chiru li Awab'ejiil re xk'uulankil li tuqtuukilal sa' qayanq.",
                autor = "Na'leb' Q'eqchi'",
                autorSignatario = "Q'eqchi'"
            ),
            "gar" to ContenidoMultilingue(
                titulo = "Darangilaü Ibagari",
                texto = "Lidan sun ubau luma gürigia, gayanbuga ábürüha darangilaü tidan wanichuni luma ibagari.",
                autor = "Garinagu Tradition",
                autorSignatario = "Garinagu"
            ),
            "xin" to ContenidoMultilingue(
                titulo = "Hapašü K'aslekey",
                texto = "Ta k'aslekey hapa ayudadó maku na'u richin ri utzilaj k'aslemal.",
                autor = "Xinka Ancestral",
                autorSignatario = "Xinka"
            )
        )
    )
)

val bancoDeDatosCompleto: List<ElementoFilosofico> = bancoDeDatosBase + bancoDeDatosPaqueteExtendido + bancoDeDatosAutoresHistoricos1 + bancoDeDatosAutoresHistoricos2 + bancoDeDatosAutoresHistoricos3 + bancoDeDatosAutoresHistoricos4 + bancoDeDatosNeurofilosofiaYHumanismo
val bancoDeDatosWellness: List<ElementoFilosofico> = bancoDeDatosCompleto

object BuscadorFilosofico {

    // Función auxiliar para quitar acentos, signos de puntuación y pasar a minúsculas
    fun normalizarTexto(texto: String): String {
        val textoMinusculas = texto.lowercase().trim()
        val nfdNormalizedString = Normalizer.normalize(textoMinusculas, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        val textoSinAcentos = pattern.matcher(nfdNormalizedString).replaceAll("")
        return textoSinAcentos.replace(Regex("[.,/#!$%^&*;:{}=\\-_`~()?¿¡!]"), " ").replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Limpia los prefijos de comando cuando el usuario le ordena algo a la IA
     * Ej: "IA busca a Séneca", "busca frases de motivación", "encuentra qué dice sobre la paz"
     */
    /**
     * Limpia los prefijos de comando cuando el usuario le ordena algo a la IA vocalmente o por texto
     * Ej: "IA busca a Séneca", "busca frases de motivación", "busca la frase de Walter Gilbert", "encuentra qué dice sobre la paz"
     */
    fun extraerConsultaDeComando(entrada: String): String {
        val normalizado = normalizarTexto(entrada)
        val patrones = listOf(
            Regex("^(ia\\s+conciencia\\s+|ia\\s+|conciencia\\s+|oye\\s+ia\\s+|hey\\s+ia\\s+)?(por\\s+favor\\s+)?(busca|buscar|buscame|encuentra|encontrar|rastrea|rastrear|localiza|localizar|dime|cuentame|hablame|mostrar|muestra|trae|ponme|citame|citar)\\s+(algo\\s+sobre\\s+|una\\s+frase\\s+de\\s+|la\\s+frase\\s+de\\s+|las\\s+frases\\s+de\\s+|frases?\\s+de\\s+|citas?\\s+de\\s+|pensamientos?\\s+de\\s+|reflexiones?\\s+de\\s+|sobre\\s+|acerca\\s+de\\s+|a\\s+|de\\s+)?"),
            Regex("^(ia\\s+|oye\\s+ia\\s+|conciencia\\s+)?(que\\s+dice|que\\s+dijo|que\\s+hay|quien\\s+es|quien\\s+fue)\\s+(sobre\\s+|de\\s+)?"),
            Regex("^(quiero\\s+saber|quisiera\\s+ver|necesito|deseo\\s+ver)\\s+(frases?\\s+de\\s+|citas?\\s+de\\s+|sobre\\s+|de\\s+)?"),
            Regex("^(ordeno\\s+que\\s+busques?|te\\s+ordeno\\s+buscar|orden\\s+buscar)\\s+(a\\s+|de\\s+|sobre\\s+)?")
        )
        var resultado = normalizado
        for (p in patrones) {
            val despues = resultado.replaceFirst(p, "").trim()
            if (despues.isNotBlank()) {
                resultado = despues
                break
            }
        }
        return if (resultado.isNotBlank()) resultado else normalizado
    }

    private val palabrasVaciasEspanol = setOf("de", "la", "el", "los", "las", "un", "una", "unos", "unas", "en", "por", "y", "a", "al", "del", "que", "con", "sobre", "para")

    /**
     * Busca todas las coincidencias con CUALQUIER palabra o letras (subcadenas, prefijos, autores, temas, etc.)
     * Permite buscar "es", "fe", "sen", "paz", "bor", "medina", "viriditas", etc.
     */
    fun buscarCoincidenciasMultiples(
        entradaUsuario: String,
        idiomaSeleccionado: String = "es",
        todasLasFrases: List<ElementoFilosofico> = bancoDeDatosCompleto,
        frasesAprendidas: List<ElementoFilosofico> = emptyList(),
        limite: Int = 40
    ): List<Pair<ElementoFilosofico, ContenidoMultilingue>> {
        val consultaLimpia = extraerConsultaDeComando(entradaUsuario)
        val consultaDirecta = normalizarTexto(entradaUsuario)
        val terminos = listOf(consultaLimpia, consultaDirecta).filter { it.isNotBlank() }.distinct()

        val poolTotal = frasesAprendidas + todasLasFrases

        if (terminos.isEmpty()) {
            return poolTotal.take(limite).map { el ->
                val trad = el.traducciones[idiomaSeleccionado] ?: el.traducciones["es"] ?: ContenidoMultilingue()
                Pair(el, trad)
            }
        }

        // Palabras individuales de la consulta, filtrando palabras vacías para mayor precisión
        val todasLasPalabras = consultaLimpia.split(" ").filter { it.isNotBlank() }
        val palabrasFiltradas = todasLasPalabras.filter { !palabrasVaciasEspanol.contains(it) }
        val palabrasConsulta = if (palabrasFiltradas.isNotEmpty()) palabrasFiltradas else todasLasPalabras

        // Búsqueda ponderada
        val resultadosConPuntaje = poolTotal.mapNotNull { elemento ->
            val tradActual = elemento.traducciones[idiomaSeleccionado] ?: elemento.traducciones["es"] ?: ContenidoMultilingue()
            val autorNorm = normalizarTexto(elemento.autorOrigen)
            val signatarioNorm = normalizarTexto(tradActual.autorSignatario)
            val tituloNorm = normalizarTexto(tradActual.titulo)
            val textoNorm = normalizarTexto(tradActual.texto)
            val tagsNorm = elemento.tags.map { normalizarTexto(it) }

            // Revisar también los textos en español como base
            val tradEs = elemento.traducciones["es"]
            val textoEsNorm = normalizarTexto(tradEs?.texto ?: "")

            var puntaje = 0

            for (termino in terminos) {
                // Coincidencia exacta o contenida con el término completo
                if (autorNorm.contains(termino) || signatarioNorm.contains(termino)) puntaje += 80
                if (tagsNorm.any { it.contains(termino) || termino.contains(it) }) puntaje += 60
                if (tituloNorm.contains(termino)) puntaje += 50
                if (textoNorm.contains(termino) || textoEsNorm.contains(termino)) puntaje += 40

                // Diccionario emocional mapeado
                val tagMapeado = DiccionarioEmocionalLocal.palabrasClave[termino]
                if (tagMapeado != null && tagsNorm.contains(tagMapeado)) {
                    puntaje += 70
                }
            }

            // Coincidencia por palabras individuales
            for (palabra in palabrasConsulta) {
                if (autorNorm.contains(palabra) || signatarioNorm.contains(palabra)) puntaje += 35
                if (tagsNorm.any { it.contains(palabra) }) puntaje += 25
                if (tituloNorm.contains(palabra)) puntaje += 20
                if (textoNorm.contains(palabra) || textoEsNorm.contains(palabra)) puntaje += 15

                val tagMapeado = DiccionarioEmocionalLocal.palabrasClave[palabra]
                if (tagMapeado != null && tagsNorm.contains(tagMapeado)) {
                    puntaje += 30
                }
            }

            if (puntaje > 0) {
                Pair(Pair(elemento, tradActual), puntaje)
            } else {
                null
            }
        }

        return resultadosConPuntaje
            .sortedByDescending { it.second }
            .take(limite)
            .map { it.first }
    }

    /**
     * Busca de forma inteligente y devuelve el mejor resultado individual.
     */
    fun filtrarFraseInteligente(
        entradaUsuario: String,
        idiomaSeleccionado: String = "es",
        todasLasFrases: List<ElementoFilosofico> = bancoDeDatosCompleto,
        frasesAprendidas: List<ElementoFilosofico> = emptyList()
    ): ContenidoMultilingue {
        val coincidencias = buscarCoincidenciasMultiples(
            entradaUsuario = entradaUsuario,
            idiomaSeleccionado = idiomaSeleccionado,
            todasLasFrases = todasLasFrases,
            frasesAprendidas = frasesAprendidas,
            limite = 1
        )

        if (coincidencias.isNotEmpty()) {
            return coincidencias.first().second
        }

        return respuestaPorDefecto(idiomaSeleccionado)
    }

    fun respuestaPorDefecto(idiomaSeleccionado: String): ContenidoMultilingue {
        return when (idiomaSeleccionado) {
            "en" -> ContenidoMultilingue(
                titulo = "Lykke Mind",
                texto = "Keep moving forward. What would you like to explore or learn today? Speak to me or type how you feel.",
                autorSignatario = "Lykke Conscience",
                autor = "Lykke Conscience"
            )
            "da" -> ContenidoMultilingue(
                titulo = "Lykke Bevidsthed",
                texto = "Fortsæt fremad i livet. Hvad vil du udforske i dag? Tal til mig eller skriv hvordan du har det.",
                autorSignatario = "Lykke Rådgivning",
                autor = "Lykke Rådgivning"
            )
            "fr" -> ContenidoMultilingue(
                titulo = "Conscience Lykke",
                texto = "Continue d'avancer. Que souhaites-tu explorer aujourd'hui ? Parle-moi ou écris ce que tu ressens.",
                autorSignatario = "Conseil Lykke",
                autor = "Conseil Lykke"
            )
            "it" -> ContenidoMultilingue(
                titulo = "Coscienza Lykke",
                texto = "Continua ad andare avanti nella vita. Cosa vorresti esplorare oggi? Parlami o scrivi come ti senti.",
                autorSignatario = "Consulenza Lykke",
                autor = "Consulenza Lykke"
            )
            "zh" -> ContenidoMultilingue(
                titulo = "Lykke 心灵",
                texto = "坚定前行。今天你想探索什么智慧？随时告诉我你的感受。",
                autorSignatario = "Lykke 导师",
                autor = "Lykke 导师"
            )
            "ru" -> ContenidoMultilingue(
                titulo = "Сознание Lykke",
                texto = "Продолжай идти вперед. Что бы ты хотел узнать сегодня? Расскажи мне голосом или текстом.",
                autorSignatario = "Lykke Мудрость",
                autor = "Lykke Мудрость"
            )
            "cak" -> ContenidoMultilingue(
                titulo = "Lykke Na'oj",
                texto = "K'o chi yaban el vave' chi ri k'aslemal. ¿Achike nawajo' nqatzukuj vacami? Tab'ij pe chwe.",
                autorSignatario = "Lykke Na'oj",
                autor = "Lykke Na'oj"
            )
            "quc" -> ContenidoMultilingue(
                titulo = "Lykke No'j",
                texto = "Rajawaxik kakat tzukunik pa le k'aslemal. ¿Jas kawaj katzukuj kamik? Chatzijoj chwe.",
                autorSignatario = "Lykke No'j",
                autor = "Lykke No'j"
            )
            "kek" -> ContenidoMultilingue(
                titulo = "Lykke Na'leb'",
                texto = "K'ub' e re xk'ulub'ankil li yu'am. ¿K'aru taawaj xk'eeb'al aach'ool chalen anaqwan?",
                autorSignatario = "Lykke Na'leb'",
                autor = "Lykke Na'leb'"
            )
            "gar" -> ContenidoMultilingue(
                titulo = "Lykke Isieni",
                texto = "Músiti bugumesehani tidan ibagari. ¿Ka busenba bágamba ugundani ubau?",
                autorSignatario = "Lykke Garinagu",
                autor = "Lykke Garinagu"
            )
            "xin" -> ContenidoMultilingue(
                titulo = "Lykke Na'u",
                texto = "Hurra ta k'aslekey k'exoj. Hapa ayudadó maku na'u richin ri utzilaj k'aslemal.",
                autorSignatario = "Lykke Xinka",
                autor = "Lykke Xinka"
            )
            else -> ContenidoMultilingue(
                titulo = "Asesoría Lykke Conciencia",
                texto = "No te desanimes, es momento de continuar y reinventarte. ¿Qué quieres que busquemos hoy, o estás listo para tu rutina? Cuéntame cómo te sientes o enséñame algo nuevo.",
                autorSignatario = "Lykke Mente y Psicología",
                autor = "Lykke Mente y Psicología"
            )
        }
    }
}

/**
 * Busca una respuesta filosófica en el banco de datos y en lo aprendido por la IA basándose en las palabras del usuario.
 */
fun buscarRespuestaFilosofica(
    inputUsuario: String,
    idiomaSeleccionado: String = "es",
    frasesAprendidas: List<ElementoFilosofico> = emptyList()
): ContenidoMultilingue {
    return BuscadorFilosofico.filtrarFraseInteligente(
        entradaUsuario = inputUsuario,
        idiomaSeleccionado = idiomaSeleccionado,
        todasLasFrases = bancoDeDatosCompleto,
        frasesAprendidas = frasesAprendidas
    )
}

/**
 * Busca múltiples coincidencias para mostrar en el buscador interactivo (autores, letras, palabras, etiquetas).
 */
fun buscarCoincidenciasFilosoficas(
    inputUsuario: String,
    idiomaSeleccionado: String = "es",
    frasesAprendidas: List<ElementoFilosofico> = emptyList(),
    limite: Int = 40
): List<Pair<ElementoFilosofico, ContenidoMultilingue>> {
    return BuscadorFilosofico.buscarCoincidenciasMultiples(
        entradaUsuario = inputUsuario,
        idiomaSeleccionado = idiomaSeleccionado,
        todasLasFrases = bancoDeDatosCompleto,
        frasesAprendidas = frasesAprendidas,
        limite = limite
    )
}

/**
 * Inicia el reconocimiento de voz en la actividad para interacción emocional por voz.
 */
fun iniciarReconocimientoVoz(activity: Activity, requestCode: Int = 7001) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Te escucho. Háblame de cómo te sientes hoy...")
    }
    try {
        activity.startActivityForResult(intent, requestCode)
    } catch (_: Exception) {
        // Manejo silencioso o toast si el dispositivo no soporta reconocimiento
    }
}
