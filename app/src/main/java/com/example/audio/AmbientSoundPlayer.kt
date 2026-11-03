package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.MelodiaPais
import com.example.data.model.SoundtrackMode
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Reproductor de Música Instrumental Ambiental y Bienestar Relajante.
 * Genera melodías instrumentales fluidas, constantes y en estéreo sin derechos de autor,
 * con arpegios de piano armónico, colchón de cuerdas/pad cálido y suaves olas naturales
 * sin pausas vacías ni notas distantes.
 * Soporta melodías internacionales de alegría y felicidad por países (Italia, Roma, Noruega, etc.).
 */
class AmbientSoundPlayer {

    private var audioTrack: AudioTrack? = null
    private var playerJob: Job? = null
    private var isPlaying = false
    private var currentMode = SoundtrackMode.PIANO_WATER
    private var activeMelodiaPais: MelodiaPais? = null

    private val sampleRate = 32000
    private var volume = 0.65f

    // Progresión armónica serena por defecto: Cmaj7, Am9, Fmaj7, Gsus4
    private val chordNotes = listOf(
        floatArrayOf(130.81f, 196.00f, 261.63f, 329.63f, 392.00f, 493.88f, 523.25f),
        floatArrayOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 392.00f, 493.88f),
        floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 349.23f, 392.00f, 440.00f),
        floatArrayOf(196.00f, 261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f)
    )

    fun startMelodia(melodia: MelodiaPais) {
        activeMelodiaPais = melodia
        iniciarReproduccionInterna(melodia.acordesNotas, melodia.tempoFactor, false)
    }

    fun start(mode: SoundtrackMode) {
        if (mode == SoundtrackMode.MUTED) {
            stop()
            return
        }

        currentMode = mode
        activeMelodiaPais = null
        val isWater = mode == SoundtrackMode.WATER_STREAM || mode == SoundtrackMode.PIANO_WATER
        iniciarReproduccionInterna(chordNotes, 1.0f, isWater)
    }

    private fun iniciarReproduccionInterna(
        chords: List<FloatArray>,
        tempoMultiplier: Float,
        includeWater: Boolean
    ) {
        stop()
        isPlaying = true
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = (minBuf * 2).coerceAtLeast(4096)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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

            audioTrack?.play()
        } catch (_: Exception) {
            isPlaying = false
            return
        }

        playerJob = CoroutineScope(Dispatchers.Default).launch {
            val shortBuffer = ShortArray(bufferSize / 2)
            var sampleCounter = 0L

            var currentChordIndex = 0
            var noteIndexInChord = 0

            val noteStepSamples = (sampleRate * 0.35f / tempoMultiplier.coerceAtLeast(0.5f)).toInt()
            var stepTimer = 0

            class ActiveVoice(val freq: Float, var age: Int, val panLeft: Float, val panRight: Float)
            val activeVoices = mutableListOf<ActiveVoice>()

            var filteredNoiseLeft = 0f
            var filteredNoiseRight = 0f

            while (isPlaying && isActive) {
                var bufIdx = 0
                while (bufIdx < shortBuffer.size - 1) {
                    val timeSec = sampleCounter.toDouble() / sampleRate

                    stepTimer++
                    if (stepTimer >= noteStepSamples) {
                        stepTimer = 0
                        val currentChord = chords[currentChordIndex % chords.size]
                        val nextFreq = currentChord[noteIndexInChord % currentChord.size]

                        val panFactor = 0.3f + 0.4f * (noteIndexInChord % 3)
                        val panL = panFactor
                        val panR = 1f - panFactor

                        activeVoices.add(ActiveVoice(nextFreq, 0, panL, panR))

                        noteIndexInChord++
                        if (noteIndexInChord >= currentChord.size * 2) {
                            noteIndexInChord = 0
                            currentChordIndex = (currentChordIndex + 1) % chords.size
                        }
                    }

                    if (activeVoices.size > 8) {
                        activeVoices.removeAt(0)
                    }

                    var pianoL = 0f
                    var pianoR = 0f

                    val voiceIter = activeVoices.iterator()
                    while (voiceIter.hasNext()) {
                        val voice = voiceIter.next()
                        val voiceT = voice.age.toDouble() / sampleRate
                        val envelope = exp(-1.2 * voiceT).toFloat()

                        if (envelope < 0.005f || voice.age > sampleRate * 3) {
                            voiceIter.remove()
                            continue
                        }

                        val f = voice.freq
                        val fundamental = sin(2.0 * PI * f * voiceT).toFloat()
                        val h1 = 0.35f * sin(4.0 * PI * f * voiceT).toFloat()
                        val h2 = 0.15f * sin(6.0 * PI * f * voiceT).toFloat()
                        val h3 = 0.05f * sin(8.0 * PI * f * voiceT).toFloat()
                        val noteSample = (fundamental + h1 + h2 + h3) * envelope * 0.45f

                        pianoL += noteSample * voice.panLeft
                        pianoR += noteSample * voice.panRight
                        voice.age++
                    }

                    val baseChord = chords[currentChordIndex % chords.size]
                    val padFreq = baseChord[0]
                    val padSample = 0.12f * (sin(2.0 * PI * padFreq * timeSec) + 0.4 * sin(2.0 * PI * (padFreq * 1.5) * timeSec)).toFloat()

                    var waterL = 0f
                    var waterR = 0f
                    if (includeWater) {
                        val noiseL = Random.nextFloat() * 2f - 1f
                        val noiseR = Random.nextFloat() * 2f - 1f
                        filteredNoiseLeft = 0.94f * filteredNoiseLeft + 0.06f * noiseL
                        filteredNoiseRight = 0.94f * filteredNoiseRight + 0.06f * noiseR

                        val swell = 0.8f + 0.2f * sin(2.0 * PI * 0.15 * timeSec).toFloat()
                        waterL = filteredNoiseLeft * swell * 0.25f
                        waterR = filteredNoiseRight * swell * 0.25f
                    }

                    val finalL = ((pianoL + padSample * 0.6f + waterL) * volume).coerceIn(-1f, 1f)
                    val finalR = ((pianoR + padSample * 0.6f + waterR) * volume).coerceIn(-1f, 1f)

                    shortBuffer[bufIdx] = (finalL * 32767f).toInt().toShort()
                    shortBuffer[bufIdx + 1] = (finalR * 32767f).toInt().toShort()

                    bufIdx += 2
                    sampleCounter++
                }

                audioTrack?.write(shortBuffer, 0, shortBuffer.size)
            }
        }
    }

    fun stop() {
        isPlaying = false
        playerJob?.cancel()
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
    }

    fun isCurrentlyPlaying(): Boolean = isPlaying
    fun getMode(): SoundtrackMode = currentMode
    fun getMelodiaActual(): MelodiaPais? = activeMelodiaPais
}
