package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Generador de Sonido de Inicio Cinemático Estilo Consola / PlayStation con Armonía de Electrónica Jazz.
 * Sintetiza en tiempo real un acorde cálido y envolvente de jazz electrónico (Fmaj9 / Dm9)
 * con brillo de campanilla analógica en el ataque, coro estéreo y decadencia resonante suave.
 */
object PlayStationJazzIntroPlayer {

    private var activeAudioTrack: AudioTrack? = null
    private var introJob: Job? = null

    fun playIntroSound(volume: Float = 0.85f) {
        stop()

        introJob = CoroutineScope(Dispatchers.Default).launch {
            val sampleRate = 44100
            val durationSeconds = 42.0f
            val totalFrames = (sampleRate * durationSeconds).toInt()
            val totalSamples = totalFrames * 2 // Estéreo

            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBuf * 2).coerceAtLeast(totalSamples * 2)

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Exception) {
                return@launch
            }

            activeAudioTrack = track

            try {
                track.play()
            } catch (_: Exception) {
                return@launch
            }

            // Progresión armónica Jazz Electrónico en 6 compases cinemáticos (~7s cada uno = 42s aprox)
            // Dm9 -> G13 -> Cmaj9 -> Fmaj9 -> Bbmaj9 -> Dm(add9) con resolución serena
            val chord1 = floatArrayOf(73.42f, 110.00f, 174.61f, 261.63f, 329.63f, 392.00f, 440.00f, 523.25f) // Dm9
            val chord2 = floatArrayOf(98.00f, 123.47f, 174.61f, 220.00f, 293.66f, 329.63f, 392.00f, 440.00f) // G13
            val chord3 = floatArrayOf(65.41f, 98.00f, 164.81f, 246.94f, 293.66f, 392.00f, 493.88f, 587.33f) // Cmaj9
            val chord4 = floatArrayOf(87.31f, 130.81f, 174.61f, 261.63f, 329.63f, 392.00f, 440.00f, 523.25f) // Fmaj9
            val chord5 = floatArrayOf(58.27f, 116.54f, 174.61f, 233.08f, 293.66f, 349.23f, 440.00f, 523.25f) // Bbmaj9
            val chord6 = floatArrayOf(73.42f, 110.00f, 146.83f, 220.00f, 293.66f, 329.63f, 440.00f, 587.33f) // Dm(add9)
            val amplitudes = floatArrayOf(0.40f, 0.35f, 0.30f, 0.35f, 0.32f, 0.28f, 0.25f, 0.22f)

            // Frecuencias para los chimes de campana cristalina (PlayStation shimmer)
            val chimeFreqs = floatArrayOf(1046.50f, 1318.51f, 1567.98f, 2093.00f)

            val chunkSize = 2048
            val chunk = ShortArray(chunkSize)
            var frameIndex = 0

            while (frameIndex < totalFrames && isActive) {
                val framesToGenerate = (chunkSize / 2).coerceAtMost(totalFrames - frameIndex)
                var chunkSampleIdx = 0

                for (f in 0 until framesToGenerate) {
                    val currentFrame = frameIndex + f
                    val t = currentFrame.toFloat() / sampleRate

                    // Envolvente de volumen maestro (ataque suave de 150ms, meseta y fadeout final de 3 segundos)
                    val attack = (t / 0.15f).coerceIn(0f, 1f)
                    val fadeOut = if (t > 38.5f) ((42.0f - t) / 3.5f).coerceIn(0f, 1f) else 1f
                    val masterEnv = attack * fadeOut

                    // Selección del acorde según ciclo de 7 segundos
                    val currentChord = when {
                        t < 7.0f -> chord1
                        t < 14.0f -> chord2
                        t < 21.0f -> chord3
                        t < 28.0f -> chord4
                        t < 35.0f -> chord5
                        else -> chord6
                    }

                    // Envolvente de chimes en compases clave (al inicio t=0 y a mitad t=21s)
                    val chimeEnv1 = (t / 0.02f).coerceIn(0f, 1f) * exp(-t * 2.5f)
                    val chimeTime2 = (t - 21.0f)
                    val chimeEnv2 = if (chimeTime2 > 0f) {
                        (chimeTime2 / 0.02f).coerceIn(0f, 1f) * exp(-chimeTime2 * 2.5f)
                    } else 0f
                    val totalChimeEnv = chimeEnv1 + chimeEnv2

                    var sampleL = 0f
                    var sampleR = 0f

                    // Modulación analógica slow-chorus y trémolo cálido
                    val tremolo = 1.0f + 0.07f * sin(2.0 * PI * 1.5 * t).toFloat()

                    for (i in currentChord.indices) {
                        val freq = currentChord[i]
                        val amp = amplitudes[i]

                        // Detune estereofónico sutil
                        val phaseL = 2.0 * PI * (freq + 0.40) * t
                        val phaseR = 2.0 * PI * (freq - 0.40) * t

                        val toneL = sin(phaseL).toFloat() + 0.20f * sin(phaseL * 2).toFloat()
                        val toneR = sin(phaseR).toFloat() + 0.20f * sin(phaseR * 2).toFloat()

                        sampleL += toneL * amp * tremolo
                        sampleR += toneR * amp * tremolo
                    }

                    // Campanas cristalinas espaciales
                    if (totalChimeEnv > 0.001f) {
                        for (cf in chimeFreqs) {
                            val phase = 2.0 * PI * cf * t
                            val chimeTone = sin(phase).toFloat() * 0.12f * totalChimeEnv
                            sampleL += chimeTone
                            sampleR += chimeTone
                        }
                    }

                    // Normalizar y limitar
                    val finalL = (sampleL * masterEnv * volume * 9500f).coerceIn(-32767f, 32767f).toInt().toShort()
                    val finalR = (sampleR * masterEnv * volume * 9500f).coerceIn(-32767f, 32767f).toInt().toShort()

                    chunk[chunkSampleIdx++] = finalL
                    chunk[chunkSampleIdx++] = finalR
                }

                track.write(chunk, 0, framesToGenerate * 2)
                frameIndex += framesToGenerate
            }

            try {
                track.stop()
                track.release()
            } catch (_: Exception) {}
            activeAudioTrack = null
        }
    }

    fun stop() {
        introJob?.cancel()
        introJob = null
        try {
            activeAudioTrack?.stop()
            activeAudioTrack?.release()
        } catch (_: Exception) {}
        activeAudioTrack = null
    }
}
