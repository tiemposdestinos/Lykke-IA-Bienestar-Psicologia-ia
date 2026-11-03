package com.example.data.model

/**
 * Banco de Datos Especializado en Neurofilosofía, Trascendencia, Psicología Humanista y Crecimiento Personal.
 * Incluye autores de frontera: Michael Persinger, Francisco Varela, Oliver Sacks, Søren Kierkegaard,
 * Mircea Eliade, William James, Rudolf Otto, Andrew Newberg, Friedrich Schleiermacher, Bruce Hood,
 * David Eagleman, Maynor Berganza (8 reflexiones clave), Eleanor Roosevelt, Wayne Dyer, Dalai Lama,
 * Nelson Mandela, Martin Luther King Jr., Mahatma Gandhi, Daniel Goleman, Maya Angelou,
 * Erving Goffman, Carl Gustav Jung, Claude Lévi-Strauss, Walter Riso, Marshall Rosenberg,
 * Virginia Satir, Thomas d'Ansembourg y K. Ada Medina.
 */
val bancoDeDatosNeurofilosofiaYHumanismo: List<ElementoFilosofico> = listOf(
    // 1. Michael Persinger
    ElementoFilosofico(
        id = 3001,
        tipo = "FRASE",
        autorOrigen = "Michael Persinger",
        tags = listOf("persinger", "michael persinger", "estados misticos", "cerebro", "revelaciones", "arquitectura cerebral", "neuroteologia", "trascendencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Arquitectura Cerebral y lo Místico",
                texto = "Los estados místicos y las revelaciones no ocurren fuera del cuerpo, sino a través de la arquitectura cerebral que traduce lo invisible en experiencia humana.",
                autorSignatario = "Michael Persinger",
                autor = "Michael Persinger"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Brain Architecture and the Mystical",
                texto = "Mystical states and revelations do not occur outside the body, but through the brain architecture that translates the invisible into human experience.",
                autorSignatario = "Michael Persinger",
                autor = "Michael Persinger"
            )
        )
    ),

    // 2. Francisco Varela
    ElementoFilosofico(
        id = 3002,
        tipo = "FRASE",
        autorOrigen = "Francisco Varela",
        tags = listOf("varela", "francisco varela", "meditacion", "yo", "observador", "universo", "cognicion", "neurobiologia", "mente"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Disolución del Observador",
                texto = "Cuando el cerebro desconecta el sentido del yo en la meditación profunda, la frontera entre el observador y el universo se disuelve.",
                autorSignatario = "Francisco Varela",
                autor = "Francisco Varela"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Dissolution of the Observer",
                texto = "When the brain disconnects the sense of self in deep meditation, the boundary between the observer and the universe dissolves.",
                autorSignatario = "Francisco Varela",
                autor = "Francisco Varela"
            )
        )
    ),

    // 3. Oliver Sacks
    ElementoFilosofico(
        id = 3003,
        tipo = "FRASE",
        autorOrigen = "Oliver Sacks",
        tags = listOf("sacks", "oliver sacks", "neurobiologia", "creencia", "trascendencia", "significado", "cerebro", "evolucion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Neurobiología de la Creencia",
                texto = "La neurobiología de la creencia nos demuestra que el cerebro está evolutivamente programado para buscar trascendencia y significado.",
                autorSignatario = "Oliver Sacks",
                autor = "Oliver Sacks"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Neurobiology of Belief",
                texto = "The neurobiology of belief demonstrates that the human brain is evolutionarily wired to seek transcendence and meaning.",
                autorSignatario = "Oliver Sacks",
                autor = "Oliver Sacks"
            )
        )
    ),

    // 4. Søren Kierkegaard (Ética y Estética)
    ElementoFilosofico(
        id = 3004,
        tipo = "FRASE",
        autorOrigen = "Søren Kierkegaard",
        tags = listOf("kierkegaard", "soren kierkegaard", "etico", "estetico", "deber", "belleza", "existencia", "danes", "filosofia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Lo Ético y lo Estético",
                texto = "Lo ético es lo que hace al hombre elegir el deber, mientras que lo estético es lo que le permite gozar de la belleza de la existencia de forma inmediata.",
                autorSignatario = "Søren Kierkegaard",
                autor = "Søren Kierkegaard"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Ethical and the Aesthetic",
                texto = "The ethical is what leads human beings to choose duty, while the aesthetic is what allows them to immediately enjoy the beauty of existence.",
                autorSignatario = "Søren Kierkegaard",
                autor = "Søren Kierkegaard"
            ),
            "da" to ContenidoMultilingue(
                titulo = "Det Etiske og det Æstetiske",
                texto = "Det etiske gør, at mennesket vælger pligten, mens det æstetiske tillader det at nyde tilværelsens skønhed umiddelbart.",
                autorSignatario = "Søren Kierkegaard",
                autor = "Søren Kierkegaard"
            )
        )
    ),

    // 5. Mircea Eliade
    ElementoFilosofico(
        id = 3005,
        tipo = "FRASE",
        autorOrigen = "Mircea Eliade",
        tags = listOf("eliade", "mircea eliade", "religion", "ritual", "estetico", "moral", "sagrado", "corazon"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Belleza del Ritual Sagrado",
                texto = "La religión se desmorona cuando se reduce a un código moral frío; necesita la belleza del ritual estético para conmover el corazón humano.",
                autorSignatario = "Mircea Eliade",
                autor = "Mircea Eliade"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Sacred and Aesthetic Ritual",
                texto = "Religion crumbles when reduced to a cold moral code; it requires the beauty of aesthetic ritual to touch the human heart.",
                autorSignatario = "Mircea Eliade",
                autor = "Mircea Eliade"
            )
        )
    ),

    // 6. William James
    ElementoFilosofico(
        id = 3006,
        tipo = "FRASE",
        autorOrigen = "William James",
        tags = listOf("william james", "james", "mistica", "conocimiento", "verdad", "inteligencia", "experiencia religiosa", "psicologia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Experiencia Mística como Conocimiento",
                texto = "La experiencia mística tiene una cualidad de conocimiento, de penetración en verdades inaccesibles a la inteligencia discursiva.",
                autorSignatario = "William James",
                autor = "William James"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Mystical States as Knowledge",
                texto = "Mystical states have a quality of insight, of penetration into truth inaccessible to discursive intellect.",
                autorSignatario = "William James",
                autor = "William James"
            )
        )
    ),

    // 7. Rudolf Otto
    ElementoFilosofico(
        id = 3007,
        tipo = "FRASE",
        autorOrigen = "Rudolf Otto",
        tags = listOf("rudolf otto", "otto", "lo sagrado", "numinoso", "etico", "estetico", "religioso", "misterio"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Lo Sagrado y lo Numinoso",
                texto = "Lo sagrado es la categoría de interpretación y valoración peculiar de lo religioso, que se manifiesta con un carácter ético y estético a la vez.",
                autorSignatario = "Rudolf Otto",
                autor = "Rudolf Otto"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Idea of the Holy",
                texto = "The sacred is the category of interpretation unique to religious experience, manifesting with both an ethical and aesthetic character.",
                autorSignatario = "Rudolf Otto",
                autor = "Rudolf Otto"
            )
        )
    ),

    // 8. Andrew Newberg
    ElementoFilosofico(
        id = 3008,
        tipo = "FRASE",
        autorOrigen = "Andrew Newberg",
        tags = listOf("andrew newberg", "newberg", "neuroteologia", "cerebro", "trascendencia", "espiritual", "cognicion", "neurobiologia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Cerebro y la Trascendencia",
                texto = "El cerebro humano parece tener un sistema integrado diseñado para conectar los procesos cognitivos con las sensaciones de trascendencia espiritual.",
                autorSignatario = "Andrew Newberg",
                autor = "Andrew Newberg"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Brain and Spiritual Transcendence",
                texto = "The human brain appears to possess an integrated system designed to link cognitive processes with sensations of spiritual transcendence.",
                autorSignatario = "Andrew Newberg",
                autor = "Andrew Newberg"
            )
        )
    ),

    // 9. Friedrich Schleiermacher
    ElementoFilosofico(
        id = 3009,
        tipo = "FRASE",
        autorOrigen = "Friedrich Schleiermacher",
        tags = listOf("schleiermacher", "friedrich schleiermacher", "religion", "estetica", "universo", "infinito", "intuicion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Contemplación Estética del Infinito",
                texto = "La religión no es solo un conjunto de verdades morales, sino una forma estética de contemplar el universo infinito.",
                autorSignatario = "Friedrich Schleiermacher",
                autor = "Friedrich Schleiermacher"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Aesthetic Contemplation of the Infinite",
                texto = "Religion is not merely a collection of moral truths, but an aesthetic way of contemplating the infinite universe.",
                autorSignatario = "Friedrich Schleiermacher",
                autor = "Friedrich Schleiermacher"
            )
        )
    ),

    // 10. Bruce Hood
    ElementoFilosofico(
        id = 3010,
        tipo = "FRASE",
        autorOrigen = "Bruce Hood",
        tags = listOf("bruce hood", "hood", "paranormal", "creencias", "patrones", "propositos", "cerebro", "cognicion"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Patrones Cognitivos y Creencias",
                texto = "Las creencias paranormales y religiosas surgen de los mismos mecanismos cognitivos normales que buscan patrones y propósitos en nuestro entorno.",
                autorSignatario = "Bruce Hood",
                autor = "Bruce Hood"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Cognitive Patterns and Beliefs",
                texto = "Paranormal and religious beliefs arise from the very same normal cognitive mechanisms that seek patterns and purpose in our environment.",
                autorSignatario = "Bruce Hood",
                autor = "Bruce Hood"
            )
        )
    ),

    // 11. David Eagleman
    ElementoFilosofico(
        id = 3011,
        tipo = "FRASE",
        autorOrigen = "David Eagleman",
        tags = listOf("david eagleman", "eagleman", "neuroteologia", "biologia", "fe", "espiritualidad", "cerebro", "neurociencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Neuroteología y la Mente",
                texto = "La neuroteología busca rastrear las conexiones entre el funcionamiento biológico del cerebro y las complejas experiencias de fe y espiritualidad.",
                autorSignatario = "David Eagleman",
                autor = "David Eagleman"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Neurotheology and Mind",
                texto = "Neurotheology seeks to trace the links between the biological workings of the brain and the complex experiences of faith and spirituality.",
                autorSignatario = "David Eagleman",
                autor = "David Eagleman"
            )
        )
    ),

    // 12. Síntesis Cognitiva y Paranormal
    ElementoFilosofico(
        id = 3012,
        tipo = "CONCEPTOS",
        autorOrigen = "NEUROCIENCIA COGNITIVA",
        tags = listOf("cognitiva", "paranormal", "experiencia cognitiva", "experiencia paranormal", "enlace", "patrones", "mente"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Enlace entre lo Cognitivo y lo Paranormal",
                texto = "Las experiencias cognitivas y las experiencias paranormales se pueden enlazar: ambas nacen de la capacidad innata del cerebro para proyectar significado, intuir conexiones invisibles y anticipar lo desconocido.",
                autorSignatario = "Psicología & Neurociencia Cognitiva",
                autor = "Neurociencia Cognitiva"
            )
        )
    ),

    // 13. Síntesis Ética y Estética en la Religión
    ElementoFilosofico(
        id = 3013,
        tipo = "CONCEPTOS",
        autorOrigen = "FILOSOFÍA DE LA RELIGIÓN",
        tags = listOf("religion", "etico", "estetico", "deber", "belleza", "conlleva", "armonia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Lo Ético y lo Estético en la Religión",
                texto = "En la religión se entrelazan indisolublemente lo ético y lo estético: el compromiso ético sostiene la rectitud y la compasión, mientras la belleza estética eleva el asombro y la contemplación del misterio de la vida.",
                autorSignatario = "Filosofía Trascendental",
                autor = "Filosofía de la Religión"
            )
        )
    ),

    // 14 a 21. Maynor Berganza (8 Reflexiones Maestras)
    ElementoFilosofico(
        id = 3014,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "reinvencion", "miedo", "condicionamiento", "autenticidad", "ser"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Reinvención Personal Auténtica",
                texto = "La verdadera reinvención personal no consiste en cambiar quién eres, sino en recordar quién estabas destinado a ser antes de que el miedo te condicionara.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3015,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "liderazgo", "liderazgo consciente", "tormentas", "mente", "calma"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Liderazgo Consciente",
                texto = "El liderazgo consciente comienza cuando dejas de reaccionar ante las tormentas externas y aprendes a gobernar tu propia mente con serenidad.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3016,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "frustracion", "resiliencia", "camino", "crecimiento", "obstaculo"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Valor de la Frustración",
                texto = "La frustración no es un callejón sin salida; es el combustible necesario para forjar resiliencia y encontrar un nuevo camino con mayor claridad.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3017,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "proposito", "disciplina", "libertad", "autenticidad", "habitos"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Propósito y Disciplina Libre",
                texto = "Cuando alineas tu propósito diario con la autenticidad, la disciplina deja de ser un sacrificio pesado y se convierte en tu mayor acto de libertad.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3018,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "exito", "paz interior", "claridad", "bienestar", "mente"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Éxito Sostenible",
                texto = "El éxito sostenible no se mide por lo que acumulas hacia afuera, sino por la paz y la claridad que mantienes intactas por dentro.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3019,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "emociones", "brujula", "crecer", "gestion emocional", "inteligencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Las Emociones como Brújula",
                texto = "Aprender a gestionar tus emociones no es reprimirlas; es escucharlas como brújulas sabias que te indican con precisión dónde necesitas crecer.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3020,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "levantarse", "bienestar", "futuro", "sembrar", "plenitud"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Sembrando tu Plenitud",
                texto = "Cada día que decides levantarte y trabajar conscientemente en tu bienestar, estás sembrando el futuro de tu versión más plena y compasiva.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),
    ElementoFilosofico(
        id = 3021,
        tipo = "FRASE",
        autorOrigen = "Maynor Berganza",
        tags = listOf("maynor berganza", "berganza", "maynor", "empatia", "compasion", "fuerza", "transformacion", "humano"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Fuerza de la Empatía",
                texto = "La empatía y la compasión no son debilidades humanas; son las mayores fortalezas que poseemos para conectar profundamente y transformar vidas.",
                autorSignatario = "Maynor Berganza",
                autor = "Maynor Berganza"
            )
        )
    ),

    // 22. Eleanor Roosevelt
    ElementoFilosofico(
        id = 3022,
        tipo = "FRASE",
        autorOrigen = "Eleanor Roosevelt",
        tags = listOf("eleanor roosevelt", "roosevelt", "futuro", "suenos", "esperanza", "dignidad", "autoestima"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Belleza de los Sueños",
                texto = "El futuro pertenece a quienes creen en la belleza de sus sueños. Nadie puede hacerte sentir inferior sin tu propio consentimiento.",
                autorSignatario = "Eleanor Roosevelt",
                autor = "Eleanor Roosevelt"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Beauty of Dreams",
                texto = "The future belongs to those who believe in the beauty of their dreams. No one can make you feel inferior without your consent.",
                autorSignatario = "Eleanor Roosevelt",
                autor = "Eleanor Roosevelt"
            )
        )
    ),

    // 23. Wayne Dyer
    ElementoFilosofico(
        id = 3023,
        tipo = "FRASE",
        autorOrigen = "Wayne Dyer",
        tags = listOf("wayne dyer", "dyer", "perspectiva", "cambio", "mirada", "transformacion", "mente"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Cambio de Perspectiva",
                texto = "Si cambias la forma en que miras las cosas, las cosas que miras cambian.",
                autorSignatario = "Dr. Wayne Dyer",
                autor = "Wayne Dyer"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Power of Perspective",
                texto = "If you change the way you look at things, the things you look at change.",
                autorSignatario = "Dr. Wayne Dyer",
                autor = "Wayne Dyer"
            )
        )
    ),

    // 24. Dalai Lama
    ElementoFilosofico(
        id = 3024,
        tipo = "FRASE",
        autorOrigen = "Dalai Lama",
        tags = listOf("dalai lama", "lama", "felicidad", "compasion", "paz", "bondad", "proposito"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Propósito de la Vida",
                texto = "El propósito fundamental de nuestras vidas es ser felices y cultivar una compasión activa por todos los seres.",
                autorSignatario = "Dalai Lama",
                autor = "Dalai Lama"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Purpose of Life",
                texto = "The fundamental purpose of our lives is to be happy and cultivate active compassion for all beings.",
                autorSignatario = "Dalai Lama",
                autor = "Dalai Lama"
            )
        )
    ),

    // 25. Nelson Mandela
    ElementoFilosofico(
        id = 3025,
        tipo = "FRASE",
        autorOrigen = "Nelson Mandela",
        tags = listOf("nelson mandela", "mandela", "levantarse", "caer", "resiliencia", "imposible", "coraje", "perseverancia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Gloria de Levantarse",
                texto = "La mayor gloria no es no caer nunca, sino levantarse cada vez que caemos. Siempre parece imposible hasta que se hace.",
                autorSignatario = "Nelson Mandela",
                autor = "Nelson Mandela"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Glory in Rising",
                texto = "The greatest glory in living lies not in never falling, but in rising every time we fall. It always seems impossible until it's done.",
                autorSignatario = "Nelson Mandela",
                autor = "Nelson Mandela"
            )
        )
    ),

    // 26. Martin Luther King Jr.
    ElementoFilosofico(
        id = 3026,
        tipo = "FRASE",
        autorOrigen = "Martin Luther King Jr.",
        tags = listOf("martin luther king", "mlk", "king", "luz", "oscuridad", "amor", "odio", "esperanza", "justicia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Luz contra la Oscuridad",
                texto = "La oscuridad no puede expulsar a la oscuridad; solo la luz puede hacer eso. El odio no puede expulsar al odio; solo el amor puede hacerlo.",
                autorSignatario = "Martin Luther King Jr.",
                autor = "Martin Luther King Jr."
            ),
            "en" to ContenidoMultilingue(
                titulo = "Light against Darkness",
                texto = "Darkness cannot drive out darkness; only light can do that. Hate cannot drive out hate; only love can do that.",
                autorSignatario = "Martin Luther King Jr.",
                autor = "Martin Luther King Jr."
            )
        )
    ),

    // 27. Mahatma Gandhi
    ElementoFilosofico(
        id = 3027,
        tipo = "FRASE",
        autorOrigen = "Mahatma Gandhi",
        tags = listOf("gandhi", "mahatma gandhi", "cambio", "voluntad", "fuerza", "no violencia", "paz"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Sé el Cambio",
                texto = "Sé el cambio que deseas ver en el mundo. La verdadera fuerza no proviene de la capacidad física, sino de una voluntad indomable.",
                autorSignatario = "Mahatma Gandhi",
                autor = "Mahatma Gandhi"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Be the Change",
                texto = "Be the change that you wish to see in the world. Strength does not come from physical capacity; it comes from an indomitable will.",
                autorSignatario = "Mahatma Gandhi",
                autor = "Mahatma Gandhi"
            )
        )
    ),

    // 28. Daniel Goleman
    ElementoFilosofico(
        id = 3028,
        tipo = "FRASE",
        autorOrigen = "Daniel Goleman",
        tags = listOf("daniel goleman", "goleman", "inteligencia emocional", "autoconciencia", "empatia", "emociones", "psicologia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Inteligencia Emocional y Autoconciencia",
                texto = "La inteligencia emocional determina nuestro potencial para aprender las habilidades prácticas basadas en la autoconciencia, la motivación y la empatía.",
                autorSignatario = "Daniel Goleman",
                autor = "Daniel Goleman"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Emotional Intelligence",
                texto = "Emotional intelligence determines our potential for learning practical skills based on self-awareness, motivation, and empathy.",
                autorSignatario = "Daniel Goleman",
                autor = "Daniel Goleman"
            )
        )
    ),

    // 29. Maya Angelou
    ElementoFilosofico(
        id = 3029,
        tipo = "FRASE",
        autorOrigen = "Maya Angelou",
        tags = listOf("maya angelou", "angelou", "sentir", "huella", "memoria", "corazon", "empatia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Huella del Corazón",
                texto = "He aprendido que la gente olvidará lo que dijiste, la gente olvidará lo que hiciste, pero la gente nunca olvidará cómo la hiciste sentir.",
                autorSignatario = "Maya Angelou",
                autor = "Maya Angelou"
            ),
            "en" to ContenidoMultilingue(
                titulo = "How You Made Them Feel",
                texto = "I've learned that people will forget what you said, people will forget what you did, but people will never forget how you made them feel.",
                autorSignatario = "Maya Angelou",
                autor = "Maya Angelou"
            )
        )
    ),

    // 30. Erving Goffman
    ElementoFilosofico(
        id = 3030,
        tipo = "FRASE",
        autorOrigen = "Erving Goffman",
        tags = listOf("erving goffman", "goffman", "teatro", "dignidad", "interaccion", "sociologia", "humano"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Dignidad en la Interacción",
                texto = "La vida social es un teatro donde cada interacción nos permite cuidar la dignidad del otro y proyectar nuestra mejor versión humana.",
                autorSignatario = "Erving Goffman",
                autor = "Erving Goffman"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Dignity in Social Interaction",
                texto = "Social life is a stage where every interaction allows us to protect each other's dignity and present our best human self.",
                autorSignatario = "Erving Goffman",
                autor = "Erving Goffman"
            )
        )
    ),

    // 31. Carl Gustav Jung (El Alma Humana)
    ElementoFilosofico(
        id = 3031,
        tipo = "FRASE",
        autorOrigen = "Carl Gustav Jung",
        tags = listOf("carl jung", "jung", "alma humana", "teorias", "empatia", "psicologia", "conexion", "sombra"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Contacto con el Alma Humana",
                texto = "Conozca todas las teorías, domine todas las técnicas, pero al tocar un alma humana sea apenas otra alma humana.",
                autorSignatario = "Carl Gustav Jung",
                autor = "Carl Gustav Jung"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Touching a Human Soul",
                texto = "Know all the theories, master all the techniques, but as you touch a human soul be just another human soul.",
                autorSignatario = "Carl Gustav Jung",
                autor = "Carl Gustav Jung"
            )
        )
    ),

    // 32. Claude Lévi-Strauss
    ElementoFilosofico(
        id = 3032,
        tipo = "FRASE",
        autorOrigen = "Claude Lévi-Strauss",
        tags = listOf("levi strauss", "claude levi-strauss", "preguntas", "sabio", "sabiduria", "antropologia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "El Arte de Preguntar",
                texto = "El sabio no es el hombre que proporciona las respuestas verdaderas; es el que formula las preguntas correctas.",
                autorSignatario = "Claude Lévi-Strauss",
                autor = "Claude Lévi-Strauss"
            ),
            "en" to ContenidoMultilingue(
                titulo = "The Art of Questioning",
                texto = "The scientist is not a person who gives the right answers, but one who asks the right questions.",
                autorSignatario = "Claude Lévi-Strauss",
                autor = "Claude Lévi-Strauss"
            )
        )
    ),

    // 33. Walter Riso
    ElementoFilosofico(
        id = 3033,
        tipo = "FRASE",
        autorOrigen = "Walter Riso",
        tags = listOf("walter riso", "riso", "autoestima", "dignidad", "amor propio", "enamorate", "psicologia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Amor Propio y Dignidad",
                texto = "Enamórate de ti, de la vida y luego de quien tú quieras. La dignidad personal nunca es negociable.",
                autorSignatario = "Walter Riso",
                autor = "Walter Riso"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Self-Love and Dignity",
                texto = "Fall in love with yourself, with life, and then with whoever you wish. Personal dignity is never negotiable.",
                autorSignatario = "Walter Riso",
                autor = "Walter Riso"
            )
        )
    ),

    // 34. Marshall Rosenberg (Comunicación No Violenta)
    ElementoFilosofico(
        id = 3034,
        tipo = "FRASE",
        autorOrigen = "Marshall Rosenberg",
        tags = listOf("marshall rosenberg", "rosenberg", "cnv", "comunicacion no violenta", "empatia", "necesidades", "enojo", "juicio"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Detrás del Juicio",
                texto = "La comunicación no violenta nos recuerda que detrás de cada juicio y enojo hay una necesidad humana universal insatisfecha.",
                autorSignatario = "Marshall Rosenberg",
                autor = "Marshall Rosenberg"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Behind Every Judgment",
                texto = "Nonviolent Communication reminds us that behind every judgment and anger lies an unmet universal human need.",
                autorSignatario = "Marshall Rosenberg",
                autor = "Marshall Rosenberg"
            )
        )
    ),

    // 35. Virginia Satir
    ElementoFilosofico(
        id = 3035,
        tipo = "FRASE",
        autorOrigen = "Virginia Satir",
        tags = listOf("virginia satir", "satir", "afrontar", "vida", "actitud", "terapia familiar", "resiliencia"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Manera de Afrontar la Vida",
                texto = "La vida no es lo que se supone que debe ser. Es lo que es. La manera de afrontarla es lo que marca la diferencia.",
                autorSignatario = "Virginia Satir",
                autor = "Virginia Satir"
            ),
            "en" to ContenidoMultilingue(
                titulo = "How We Cope with Life",
                texto = "Life is not what it's supposed to be. It's what it is. The way you cope with it is what makes the difference.",
                autorSignatario = "Virginia Satir",
                autor = "Virginia Satir"
            )
        )
    ),

    // 36. Thomas d'Ansembourg
    ElementoFilosofico(
        id = 3036,
        tipo = "FRASE",
        autorOrigen = "Thomas d'Ansembourg",
        tags = listOf("thomas d'ansembourg", "ansembourg", "autenticidad", "amable", "complacer", "verdad", "asertividad"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Autenticidad sobre Complacer",
                texto = "Dejemos de ser amables por mera complacencia y aprendamos a ser auténticos por amor a la verdad y a las relaciones sanas.",
                autorSignatario = "Thomas d'Ansembourg",
                autor = "Thomas d'Ansembourg"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Authenticity over Pleasing",
                texto = "Stop being nice merely to please; learn to be authentic for the sake of truth and healthy human relationships.",
                autorSignatario = "Thomas d'Ansembourg",
                autor = "Thomas d'Ansembourg"
            )
        )
    ),

    // 37. K. Ada Medina (Asegurada con autor explícito para que siempre aparezca con su autor)
    ElementoFilosofico(
        id = 3037,
        tipo = "FRASE",
        autorOrigen = "K. Ada Medina",
        tags = listOf("k. ada medina", "k ada medina", "ada medina", "k. adam medina", "k adam medina", "adam medina", "medina", "k ada", "ada"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "La Brújula de los Principios",
                texto = "Tus principios son la brújula que nunca te dejará perder el rumbo, aun en medio de la tormenta más densa.",
                autorSignatario = "K. Ada Medina",
                autor = "K. Ada Medina"
            ),
            "en" to ContenidoMultilingue(
                titulo = "Compass of Principles",
                texto = "Your principles are the compass that will never let you lose your way, even amid the densest storm.",
                autorSignatario = "K. Ada Medina",
                autor = "K. Ada Medina"
            )
        )
    ),
    ElementoFilosofico(
        id = 3038,
        tipo = "FRASE",
        autorOrigen = "K. Ada Medina",
        tags = listOf("k. ada medina", "k ada medina", "ada medina", "k. adam medina", "k adam medina", "adam medina", "medina", "k ada", "ada", "mente", "crecimiento"),
        traducciones = mapOf(
            "es" to ContenidoMultilingue(
                titulo = "Transformación Interior",
                texto = "No puedes cambiar el viento, pero siempre puedes ajustar las velas de tu mente para llegar a puerto seguro.",
                autorSignatario = "K. Ada Medina",
                autor = "K. Ada Medina"
            )
        )
    )
)
