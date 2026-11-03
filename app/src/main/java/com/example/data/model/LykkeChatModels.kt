package com.example.data.model

enum class LykkeProtocolType(val protocolName: String, val badgeColor: Long) {
    MATRIX("Matrix Global (matrix.org)", 0xFF0284C7),
    IRC("IRC Abierto (Libera.Chat / OFTC)", 0xFF10B981),
    XMPP("Protocolo XMPP (Jabber)", 0xFF8B5CF6),
    CHINA_LOOP("Puente Internacional Baidu Loop", 0xFFD97706),
    RUSSIA_LOOP("Puente Internacional Yandex Loop", 0xFFEF4444),
    CLAN_PRIVADO("Lykke Clan (Matrix P2P E2EE)", 0xFF6366F1)
}

data class LykkeChatChannel(
    val id: String,
    val name: String,
    val protocolType: LykkeProtocolType,
    val protocolTag: String,
    val iconEmoji: String,
    val description: String,
    val availableRooms: List<String>,
    val serverHost: String
)

data class LykkeChatMessage(
    val id: String,
    val channelId: String,
    val roomTopic: String,
    val senderName: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isMine: Boolean = false,
    val avatarColor: Long = 0xFF0284C7
)

object LykkeCommunityRegistry {
    val channels = listOf(
        LykkeChatChannel(
            id = "matrix_global",
            name = "LykkeBBChat",
            protocolType = LykkeProtocolType.MATRIX,
            protocolTag = "Matrix-Org",
            iconEmoji = "🌐",
            description = "Superestructura federada Matrix con salas públicas activas de psicología y ciencia.",
            availableRooms = listOf("#psicologia:matrix.org", "#ciencia:matrix.org", "#debate:matrix.org", "#asesorias:matrix.org", "#salud-mental:matrix.org"),
            serverHost = "matrix.org"
        ),
        LykkeChatChannel(
            id = "irc_global",
            name = "LykkeGrupalChat",
            protocolType = LykkeProtocolType.IRC,
            protocolTag = "IRC-Libera",
            iconEmoji = "💬",
            description = "Canales masivos y abiertos en Libera.Chat y OFTC con debates e intercambio en vivo.",
            availableRooms = listOf("#psicologia", "#ayuda", "#Asesorias", "#Consultorias", "#filosofia"),
            serverHost = "irc.libera.chat"
        ),
        LykkeChatChannel(
            id = "xmpp_global",
            name = "LykkePeople",
            protocolType = LykkeProtocolType.XMPP,
            protocolTag = "XMPP-Jabber",
            iconEmoji = "👥",
            description = "Salas federadas abiertas Jabber Multi-User Chat (MUC) con privacidad de identidad.",
            availableRooms = listOf("sala_psicologia@conference.jabber.org", "sala_asesorias@conference.jabber.org", "sala_bienestar@conference.jabber.org"),
            serverHost = "conference.jabber.org"
        ),
        LykkeChatChannel(
            id = "china_loop",
            name = "LykkeLoop Chine",
            protocolType = LykkeProtocolType.CHINA_LOOP,
            protocolTag = "Baidu-Gateway",
            iconEmoji = "🇨🇳",
            description = "Puente internacional de diálogo para comunidades asiáticas y de habla china.",
            availableRooms = listOf("china_hub_psicologia", "china_hub_debate", "china_hub_mente"),
            serverHost = "bridge.baidu-open.net"
        ),
        LykkeChatChannel(
            id = "russia_loop",
            name = "LykkeLoop Russ",
            protocolType = LykkeProtocolType.RUSSIA_LOOP,
            protocolTag = "Yandex-Open-Federation",
            iconEmoji = "🇷🇺",
            description = "Red de intercambio euroasiática y de habla rusa federada libremente.",
            availableRooms = listOf("russia_hub_psicologia", "russia_hub_consultoria", "russia_hub_ciencia"),
            serverHost = "open.yandex-federation.org"
        ),
        LykkeChatChannel(
            id = "clan_privado",
            name = "LykkeClan Privado",
            protocolType = LykkeProtocolType.CLAN_PRIVADO,
            protocolTag = "Matrix-P2P",
            iconEmoji = "🔒",
            description = "Mensajería directa punto a punto con cifrado de extremo a extremo sin servidores intermediarios.",
            availableRooms = listOf("Directo-1-a-1", "Consultoría-Privada", "Desahogo-Anónimo"),
            serverHost = "p2p.matrix.local"
        )
    )

    // Mensajes semilla realistas y enriquecidos para que las salas tengan vida de inmediato
    fun obtenerMensajesIniciales(channelId: String, roomTopic: String): List<LykkeChatMessage> {
        val now = System.currentTimeMillis()
        return when {
            roomTopic.contains("psicologia", ignoreCase = true) -> listOf(
                LykkeChatMessage(
                    id = "msg_1",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Dra. Valeria M.",
                    senderRole = "Psicóloga Cognitiva",
                    messageText = "Hola a todos. Hoy estuvimos debatiendo sobre la reestructuración cognitiva ante pensamientos catastróficos. La técnica de las 3 preguntas de Beck sigue siendo la más eficaz en consulta.",
                    timestamp = now - 3600000 * 3,
                    avatarColor = 0xFF0284C7
                ),
                LykkeChatMessage(
                    id = "msg_2",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Rodrigo_Neuro",
                    senderRole = "Investigador",
                    messageText = "Totalmente de acuerdo. Además, la neuroplasticidad nos demuestra que 3 semanas de entrenamiento en auto-observación cambian la reactividad de la amígdala cerebral.",
                    timestamp = now - 3600000 * 2,
                    avatarColor = 0xFF10B981
                ),
                LykkeChatMessage(
                    id = "msg_3",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Elena_S",
                    senderRole = "Miembro Activo",
                    messageText = "La respiración diafragmática 4-7-8 que compartieron ayer me ayudó muchísimo a reducir la taquicardia antes de mi examen.",
                    timestamp = now - 1800000,
                    avatarColor = 0xFFD97706
                ),
                LykkeChatMessage(
                    id = "msg_4",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Consultor_Lykke",
                    senderRole = "Moderador Lykke",
                    messageText = "¡Bienvenidos a la sala comunitaria! Recuerden que pueden plantear dudas, casos anónimos y reflexiones en total privacidad.",
                    timestamp = now - 600000,
                    avatarColor = 0xFF8B5CF6
                )
            )
            roomTopic.contains("ciencia", ignoreCase = true) || roomTopic.contains("debate", ignoreCase = true) -> listOf(
                LykkeChatMessage(
                    id = "c_1",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Prof_WalterG",
                    senderRole = "Biólogo Molecular",
                    messageText = "La investigación científica avanza por el conflicto constructivo de ideas, nunca por el dogmatismo. El escepticismo metodológico es indispensable.",
                    timestamp = now - 7200000,
                    avatarColor = 0xFF0284C7
                ),
                LykkeChatMessage(
                    id = "c_2",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Fisico_Quark",
                    senderRole = "Física Teórica",
                    messageText = "Como decía Gross, lo que vemos de la realidad son modelos teóricos. La naturaleza empírica es la jueza final.",
                    timestamp = now - 3600000,
                    avatarColor = 0xFF10B981
                ),
                LykkeChatMessage(
                    id = "c_3",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Dialéctica_R",
                    senderRole = "Filósofo Crítico",
                    messageText = "La conciencia no es una sustancia aislada; es conciencia del mundo material que habitamos e interactuamos.",
                    timestamp = now - 1200000,
                    avatarColor = 0xFFEF4444
                )
            )
            roomTopic.contains("ayuda", ignoreCase = true) || roomTopic.contains("asesorias", ignoreCase = true) -> listOf(
                LykkeChatMessage(
                    id = "a_1",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Guía_Lykke",
                    senderRole = "Orientador Vital",
                    messageText = "Canal de Asesoría Abierto: Si estás pasando por un momento de estancamiento o transición laboral, escribe tu inquietud aquí.",
                    timestamp = now - 4000000,
                    avatarColor = 0xFF0284C7
                ),
                LykkeChatMessage(
                    id = "a_2",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Carlos_B",
                    senderRole = "Consultor",
                    messageText = "El primer paso siempre es clarificar valores fundamentales antes de tomar decisiones impulsivas bajo estrés.",
                    timestamp = now - 2000000,
                    avatarColor = 0xFF10B981
                )
            )
            else -> listOf(
                LykkeChatMessage(
                    id = "gen_1",
                    channelId = channelId,
                    roomTopic = roomTopic,
                    senderName = "Comunidad_Lykke",
                    senderRole = "Protocolo Abierto",
                    messageText = "Conectado de forma segura a $roomTopic mediante el protocolo ${channelId}. Tu identidad y app permanecen anónimas en la red.",
                    timestamp = now - 1800000,
                    avatarColor = 0xFF6366F1
                )
            )
        }
    }
}
