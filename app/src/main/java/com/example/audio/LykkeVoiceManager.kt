package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.io.File
import java.util.Locale

enum class TipoVozMascota(val id: String, val nombreEs: String, val nombreEn: String) {
    FEMENINA("femenina", "Voz Femenina Natural 👩", "Natural Female Voice 👩"),
    MASCULINA("masculina", "Voz Masculina Cálida 👨", "Warm Male Voice 👨"),
    CONCIENCIA_CLONADA("conciencia", "Mi Propia Conciencia (Voz Reflexiva) 🧠", "My Own Conscience (Reflective Voice) 🧠"),
    MASCOTA_ANIMAL("mascota_voz", "Compañero Amigable (Tono Cercano) 🐾", "Friendly Companion (Warm Tone) 🐾")
}

/**
 * Gestor de Síntesis y Voz para la I.A. Conciencia de Lykke.
 * Configura voces humanas de alta fidelidad, con entonación natural, pausas fluidas
 * y garantizando que el idioma predeterminado sea Español ("es") o el seleccionado en la lista,
 * evitando que suene robótico o en inglés.
 */
class LykkeVoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var mediaRecorder: MediaRecorder? = null
    private var archivoGrabacion: File? = null
    private var ttsListo = false
    private var idiomaActual = "es"

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsListo = true
            aplicarIdioma(idiomaActual)
        }
    }

    fun setIdioma(codigo: String) {
        idiomaActual = codigo
        if (ttsListo) {
            aplicarIdioma(codigo)
        }
    }

    private fun aplicarIdioma(codigo: String) {
        val localeDeseado = when (codigo) {
            "en" -> Locale.US
            "fr" -> Locale.FRANCE
            "it" -> Locale.ITALY
            "da" -> Locale("da", "DK")
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "ru" -> Locale("ru", "RU")
            "cak", "quc", "kek", "gar", "xin" -> Locale("es", "GT") // Pronunciación suave y clara para Guatemala
            else -> Locale("es", "ES") // Español predeterminado
        }

        try {
            val result = tts?.setLanguage(localeDeseado)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Respaldo en español genérico
                tts?.setLanguage(Locale("es"))
            }

            // Seleccionar la voz de mayor calidad disponible en el dispositivo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val voces = tts?.voices
                if (!voces.isNullOrEmpty()) {
                    val lang = localeDeseado.language
                    val mejorVoz = voces.filter { v ->
                        v.locale.language.equals(lang, ignoreCase = true)
                    }.maxByOrNull { v ->
                        var puntaje = 0
                        if (v.quality == Voice.QUALITY_VERY_HIGH) puntaje += 40
                        if (v.quality == Voice.QUALITY_HIGH) puntaje += 25
                        if (!v.isNetworkConnectionRequired) puntaje += 10
                        if (v.name.contains("natural", ignoreCase = true)) puntaje += 20
                        puntaje
                    }
                    if (mejorVoz != null) {
                        tts?.voice = mejorVoz
                    }
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Reproduce el texto con entonación humana, fluida y con volumen amplificado al máximo para bocinas.
     */
    fun hablar(texto: String, tipoVoz: TipoVozMascota = TipoVozMascota.FEMENINA, codigoIdioma: String = idiomaActual) {
        if (!ttsListo) return

        aplicarIdioma(codigoIdioma)

        // Subir volumen del flujo multimedia al máximo para que la voz de la I.A. se escuche fuerte y clara
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            audioManager?.let { am ->
                val maxVol = am.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
                val curVol = am.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
                if (curVol < (maxVol * 0.95f).toInt()) {
                    am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, maxVol, 0)
                }
            }
        } catch (_: Exception) {}

        // Limpiar el texto y asegurar pausas humanas naturales
        val textoHumano = formatearTextoParaHablaHumana(texto)

        val bundleParams = android.os.Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }

        when (tipoVoz) {
            TipoVozMascota.FEMENINA -> {
                // Tono cálido, empático y natural con volumen alto
                tts?.setPitch(1.02f)
                tts?.setSpeechRate(0.96f) // Velocidad pausada y comprensiva
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts?.speak(textoHumano, TextToSpeech.QUEUE_FLUSH, bundleParams, "ID_FEM")
                } else {
                    val map = HashMap<String, String>().apply { put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0") }
                    tts?.speak(textoHumano, TextToSpeech.QUEUE_FLUSH, map)
                }
            }
            TipoVozMascota.MASCULINA -> {
                // Tono masculino sereno, seguro y reflexivo
                tts?.setPitch(0.95f)
                tts?.setSpeechRate(0.94f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts?.speak(textoHumano, TextToSpeech.QUEUE_FLUSH, bundleParams, "ID_MASC")
                } else {
                    val map = HashMap<String, String>().apply { put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0") }
                    tts?.speak(textoHumano, TextToSpeech.QUEUE_FLUSH, map)
                }
            }
            TipoVozMascota.MASCOTA_ANIMAL -> {
                // Compañero amigable: tono afectuoso y dinámico
                val saludo = if (codigoIdioma == "en") "Listen to this: " else "Escucha con atención: "
                val textoAnimado = "$saludo$textoHumano"
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(0.98f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts?.speak(textoAnimado, TextToSpeech.QUEUE_FLUSH, bundleParams, "ID_PET")
                } else {
                    val map = HashMap<String, String>().apply { put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0") }
                    tts?.speak(textoAnimado, TextToSpeech.QUEUE_FLUSH, map)
                }
            }
            TipoVozMascota.CONCIENCIA_CLONADA -> {
                reproducirVozConciencia(textoHumano)
            }
        }
    }

    /**
     * Ajusta la puntuación y ritmo para que el motor TTS pause como un ser humano real.
     */
    private fun formatearTextoParaHablaHumana(texto: String): String {
        return texto
            .replace("...", ". ")
            .replace("—", ", ")
            .replace("-", ", ")
            .trim()
    }

    fun detenerHabla() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    /**
     * Sistema de grabación para la muestra de voz
     */
    @Suppress("DEPRECATION")
    fun iniciarGrabacionDeConciencia() {
        try {
            archivoGrabacion = File(context.cacheDir, "mi_conciencia_muestra.mp3")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(archivoGrabacion?.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun detenerGrabacionDeConciencia(): File? {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return archivoGrabacion
    }

    private fun reproducirVozConciencia(textoAMaterializar: String) {
        tts?.setPitch(0.98f)
        tts?.setSpeechRate(0.95f)
        val prefijo = if (idiomaActual == "en") "Your inner conscience speaks: " else "Tu conciencia interior resuena: "
        tts?.speak("$prefijo$textoAMaterializar", TextToSpeech.QUEUE_FLUSH, null, "ID_CONCIENCIA")
    }

    fun liberarRecursos() {
        tts?.stop()
        tts?.shutdown()
        try {
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (_: Exception) {}
    }
}
