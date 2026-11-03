package com.example.data.model

import java.util.Locale

// ============================================================================
// ENTIDADES Y BANCO DE LECTURAS DE FAMILIA, PERDÓN Y ROLES
// ============================================================================

data class LecturaFamilia(
    val categoria: String,
    val titulo: String,
    val contenido: String
)

object LecturasSeed {
    fun obtenerLecturasIniciales(): List<LecturaFamilia> = listOf(
        LecturaFamilia(
            categoria = "FAMILIA_PERDON",
            titulo = "El Refugio Sagrado y la Fuerza del Perdón",
            contenido = "La familia es el único puerto donde se nos ama por lo que somos y no por lo que tenemos o logramos. En ningún otro lugar del mundo encontramos raíces tan profundas. El perdón dentro del hogar no significa justificar el error, sino negarse a que el rencor destruya el lazo sagrado de la sangre. Una familia fuerte no es aquella donde nunca hay desacuerdos, sino aquella donde el orgullo se arrodilla ante el amor. Perdonar a un padre, a una madre o a un hermano libera el alma y sana generaciones enteras."
        ),
        LecturaFamilia(
            categoria = "ROL_PADRE",
            titulo = "La Huella Inolvidable de la Paternidad",
            contenido = "El padre cumple una función irremplazable en el diseño de la vida familiar. Su rol representa el pilar de firmeza, el escudo protector y la voz que enseña al hijo a enfrentarse al mundo con coraje, orden y responsabilidad. Aunque una mujer es capaz de brindar un sacrificio y amor infinitos, la figura paterna aporta una energía particular que complementa la vida del hogar: enseña el valor del esfuerzo silencioso, el deber cumplido y el marco de seguridad donde los hijos descubren su propia fortaleza. Su presencia constante es una guía que nadie más puede suplir de la misma manera."
        ),
        LecturaFamilia(
            categoria = "ROL_MADRE",
            titulo = "El Vientre del Afecto y la Sabiduría Maternal",
            contenido = "La madre posee un don que el hombre jamás podrá igualar: ella no solo gesta la vida en sus entrañas, sino que es el corazón emocional y espiritual del hogar. Su conexión con los hijos nace de una intuición profunda y un amor incondicional que sostiene a la familia en sus horas más oscuras. La madre enseña la compasión y la ternura nutricia que sana las angustias del alma. El hombre puede proveer y proteger, pero solo la madre envuelve el hogar con esa ternura que forja la seguridad emocional de los hijos para siempre."
        ),
        LecturaFamilia(
            categoria = "HERMANDAD",
            titulo = "Hermanos Unidos: La Sangre Vale Más que la Tierra",
            contenido = "Los hermanos son los compañeros de viaje que Dios nos dio para caminar juntos desde el amanecer hasta el anochecer de la vida. El mayor error que un ser humano puede cometer es permitir que las propiedades, las herencias o el dinero abran una brecha de enemistad entre hermanos. La tierra se queda en la tierra; las casas envejecen y las monedas cambian de dueño; pero un hermano es un pedazo de tu propia carne. Jamás permitas que un pedazo de tierra destruya un abrazo de sangre. Apóyense para crecer juntos."
        )
    )
}

// ============================================================================
// BANCOS DE CÁPSULAS Y TERAPIAS EMOCIONALES
// ============================================================================

data class CapsulaEducativa(val categoria: String, val titulo: String, val contenido: String)
data class EjercicioTerapia(val titulo: String, val pasoAPaso: String)

object BancoDeCapsulas {
    val todas = listOf(
        CapsulaEducativa(
            "Química Cotidiana",
            "¿Por qué el jabón corta la grasa?",
            "El agua y el aceite nunca se mezclan solos. El jabón actúa como puente: tiene una cabeza que ama el agua y una cola que se une al aceite. Al lavarte, atrapa la suciedad con su cola y se va arrastrado por el chorro de agua."
        ),
        CapsulaEducativa(
            "Comida Callejera del Mundo",
            "Mofongo Caribeño y Pinchos Españoles",
            "En Puerto Rico y el Caribe se disfruta el Mofongo: plátano verde frito majado con ajo y chicharrón. En España, los pinchos morunos a la brasa y la tortilla jugosa reinan en cada rincón de tapeo."
        ),
        CapsulaEducativa(
            "Gastronomía de Asia",
            "Takoyaki de Japón",
            "En las calles de Osaka se sirven bolitas de masa dorada rellenas de pulpo fresco, bañadas con salsa dulce, alga nori y copos de pescado que se mueven con el calor del plato."
        ),
        CapsulaEducativa(
            "Sabiduría Bíblica",
            "Deuteronomio 31:8 y Tobías 4:7",
            "«El Señor mismo marchará al frente de ti; nunca te dejará ni te abandonará» (Deut 31:8). Y Tobías enseña: «Da limosna de tus bienes y no desvíes los ojos ante el necesitado, para que Dios no desvíe sus ojos de ti»."
        ),
        CapsulaEducativa(
            "Pensamiento Universal",
            "El Corán y la Voluntad",
            "«Dios no cambia la condición de un pueblo mientras las personas no transformen lo que guardan en su propio corazón y acciones» (Sura 13:11)."
        ),
        CapsulaEducativa(
            "Supervivencia y Salud",
            "Filtro Natural de Agua",
            "Para limpiar agua turbia en emergencias, corta una botella: pon capas de algodón, carbón vegetal molido (el gran purificador), arena fina y piedras. Pasa el agua y hiérvela 3 minutos antes de tomarla."
        ),
        CapsulaEducativa(
            "Astronomía",
            "Las Tres Marías y el Cazador",
            "Las tres estrellas del cinturón de Orión (Alnitak, Alnilam y Mintaka) están a más de 1.200 años luz y han servido como brújula natural para viajeros desde tiempos inmemoriales."
        )
    )
}

object BancoDeTerapias {
    val todas = listOf(
        EjercicioTerapia(
            "Desarmar la Culpa en Papel",
            "Escribe en un trozo de papel aquello que te pesa o esa culpa que llevas cargando. Léelo despacio una vez. Rómpelo en cuatro pedazos diciendo con firmeza: «Esto pertenece al pasado, no me define hoy y decido soltarlo». Respira hondo."
        ),
        EjercicioTerapia(
            "Respiración Cuadrada (Desatar el Pecho)",
            "Si sientes un nudo interno: Inhala por la nariz en 4 segundos. Mantén el aire 7 segundos. Exhala con silbido suave por la boca en 8 segundos. Repite 4 veces seguidas y siente tu mente desacelerar."
        ),
        EjercicioTerapia(
            "Reconocimiento Frente al Espejo",
            "Mírate fijamente a los ojos en el espejo por un minuto entero. Sonríe con calma y di en voz alta: «Gracias por tu resistencia. Eres una persona digna y hoy decido tratarte con amor»."
        )
    )
}

object ClaveRecordatorioManager {
    fun obtenerInstruccion(): String = when (Locale.getDefault().language.lowercase()) {
        "en" -> "To set a task, say: 'Note' followed by your activity."
        "fr" -> "Pour enregistrer une tâche, dites: «Note»."
        "ru" -> "Чтобы сохранить задачу, скажите: «Заметка»."
        "zh" -> "要记录任务，请说：“记录”。"
        else -> "Para guardar una tarea, escribe o di: «Anota» seguido de tu actividad."
    }
}

// ============================================================================
// IDIOMAS DESTINO Y TRADUCCIÓN HÍBRIDA
// ============================================================================

enum class IdiomaDestino(val codigo: String, val nombreDisplay: String, val esOffline: Boolean) {
    ESPANOL("es", "Español", true),
    CHINO_MANDARIN("zh", "中文 (Mandarín)", true),
    RUSO("ru", "Русский (Ruso)", true),
    DANES("da", "Dansk (Danés)", true),
    MAYA_KICHE("quc", "K'iche'", false),
    MAYA_QEQCHI("kek", "Q'eqchi'", false),
    MAYA_KAQCHIKEL("cak", "Kaqchikel", false),
    GARIFUNA("cab", "Garífuna", false)
}
