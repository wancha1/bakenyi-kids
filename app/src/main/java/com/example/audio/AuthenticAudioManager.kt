package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.Random
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * AuthenticAudioManager provides:
 * 1. Low-latency procedural acoustic sound synthesis (AudioTrack):
 *    - Soft physical paper page-flip / flutter rustle.
 *    - Authentic resonant African Kalimba (thumb piano) & wooden marimba notes.
 *    - Gentle water droplet / lake ripple chimes.
 *    - Hollow clay / log village drum (Engoma) heartbeats.
 *    - Delicate bird whistles & celebration pentatonic arpeggios.
 *    - Kato the Otter companion friendly chirp.
 * 2. Android TextToSpeech (TTS) for warm Lukenye / English pronunciation.
 * 3. Fallback MediaPlayer and SoundPool for bundled audio resources.
 */
class AuthenticAudioManager private constructor(private val context: Context) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val audioExecutor = Executors.newSingleThreadExecutor()
    private val random = Random()

    // Pentatonic scale frequencies in Hz for harmonious kalimba notes (C4, D4, E4, G4, A4, C5, D5, E5)
    private val kalimbaPitches = floatArrayOf(
        261.63f, // C4
        293.66f, // D4
        329.63f, // E4
        392.00f, // G4
        440.00f, // A4
        523.25f, // C5
        587.33f, // D5
        659.25f  // E5
    )

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()

            try {
                tts = TextToSpeech(context.applicationContext, this)
            } catch (e: Throwable) {
                Log.w("AUDIO_DEBUG", "TextToSpeech service unavailable: ${e.message}")
            }

            Log.d("AUDIO_DEBUG", "AuthenticAudioManager initialized successfully")
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error initializing AuthenticAudioManager", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("en", "UG")) // Uganda English / phonetic
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            tts?.setSpeechRate(0.85f) // Gentle, calm pace for young learners
            tts?.setPitch(1.05f)      // Warm and friendly pitch
            isTtsReady = true
            Log.d("AUDIO_DEBUG", "TextToSpeech initialized successfully")
        } else {
            Log.w("AUDIO_DEBUG", "TextToSpeech initialization failed with status: $status")
        }
    }

    // ==========================================
    // PROCEDURAL ACOUSTIC SOUND EFFECTS (AudioTrack)
    // ==========================================

    /**
     * Gentle, physical paper page turn sound effect.
     * Mimics the natural paper friction, gentle flutter, and soft settling of a real picture book sheet.
     */
    fun playPageTurnSound() {
        audioExecutor.execute {
            try {
                val sampleRate = 22050
                val durationMs = 380
                val totalSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(totalSamples)

                var filterState = 0f
                val smoothing = 0.12f

                for (i in 0 until totalSamples) {
                    val progress = i.toFloat() / totalSamples

                    // Multi-envelope: fast soft rise, gentle flutter ripple in middle, whisper decay
                    val envelope = when {
                        progress < 0.15f -> (progress / 0.15f)
                        progress < 0.65f -> 1.0f + 0.15f * sin(progress * 40f)
                        else -> ((1f - progress) / 0.35f)
                    }

                    // Soft pink noise simulation (paper texture)
                    val whiteNoise = (random.nextFloat() * 2f - 1f)
                    filterState = filterState + smoothing * (whiteNoise - filterState)

                    // Low-amplitude subtle 80Hz paper body woosh
                    val airWoosh = sin(2.0 * PI * 85.0 * i / sampleRate).toFloat() * 0.25f

                    val sampleValue = (filterState * 0.75f + airWoosh) * envelope * 0.42f
                    buffer[i] = (sampleValue * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing page turn sound", e)
            }
        }
    }

    /**
     * Resonant, warm African Kalimba (Mbira) note.
     * Produces a pure fundamental with a gentle metallic wood chime and exponential decay.
     */
    fun playKalimbaChime(noteIndex: Int = 0) {
        audioExecutor.execute {
            try {
                val sampleRate = 22050
                val durationMs = 600
                val totalSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(totalSamples)

                val pitch = kalimbaPitches[Math.abs(noteIndex) % kalimbaPitches.size]
                val overtonePitch = pitch * 2.756f // Natural acoustic kalimba overtone ratio

                for (i in 0 until totalSamples) {
                    val t = i.toFloat() / sampleRate
                    // Natural exponential decay of metallic tine on hollow wood
                    val decay = exp(-t * 6.5f)
                    val overtoneDecay = exp(-t * 18.0f)

                    val fundamental = sin(2.0 * PI * pitch * t).toFloat()
                    val overtone = sin(2.0 * PI * overtonePitch * t).toFloat() * 0.35f
                    // Warm thumb strike transient in first 15ms
                    val strike = if (t < 0.015f) (random.nextFloat() * 2f - 1f) * (1f - t / 0.015f) * 0.25f else 0f

                    val sample = (fundamental * decay + overtone * overtoneDecay + strike) * 0.65f
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing kalimba chime", e)
            }
        }
    }

    /**
     * Gentle water droplet / lake ripple chime for river and wetland elements.
     */
    fun playWaterDrop() {
        audioExecutor.execute {
            try {
                val sampleRate = 22050
                val durationMs = 280
                val totalSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toFloat() / sampleRate
                    // Frequency swoop downwards: 1200Hz -> 500Hz
                    val currentFreq = 1150f * exp(-t * 8f) + 400f
                    val decay = exp(-t * 12f)
                    val sample = sin(2.0 * PI * currentFreq * t).toFloat() * decay * 0.55f
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing water drop sound", e)
            }
        }
    }

    /**
     * Hollow village drum (Engoma) heartbeat sound.
     * Deep resonance around 110Hz with rich warmth.
     */
    fun playVillageDrum() {
        audioExecutor.execute {
            try {
                val sampleRate = 22050
                val durationMs = 350
                val totalSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toFloat() / sampleRate
                    val freq = 120f * exp(-t * 6f) + 65f
                    val decay = exp(-t * 9.5f)
                    val skinSlap = if (t < 0.012f) (random.nextFloat() * 2f - 1f) * 0.35f else 0f
                    val sample = (sin(2.0 * PI * freq * t).toFloat() * decay * 0.75f) + skinSlap
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing village drum sound", e)
            }
        }
    }

    /**
     * Delicate high-pitched bird chirp for crested crane & kingfisher.
     */
    fun playBirdWhistle() {
        audioExecutor.execute {
            try {
                val sampleRate = 22050
                val durationMs = 250
                val totalSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toFloat() / sampleRate
                    // Trilling high whistle
                    val freq = 2100f + sin(t * 120f) * 350f
                    val decay = if (t < 0.2f) 1f else (1f - (t - 0.2f) / 0.05f)
                    val sample = sin(2.0 * PI * freq * t).toFloat() * decay * 0.4f
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing bird whistle", e)
            }
        }
    }

    /**
     * Adorable friendly chirp for Kato the Otter companion.
     */
    fun playKatoCompanionChirp() {
        audioExecutor.execute {
            try {
                val sampleRate = 22050
                val durationMs = 320
                val totalSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toFloat() / sampleRate
                    // Playful double-harmonic trill
                    val freq = 820f + 650f * sin(t * 35f)
                    val decay = exp(-t * 6f)
                    val sample = (sin(2.0 * PI * freq * t).toFloat() * 0.6f +
                            sin(2.0 * PI * (freq * 1.5f) * t).toFloat() * 0.3f) * decay * 0.5f
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing Kato chirp", e)
            }
        }
    }

    /**
     * Celebratory 5-note pentatonic arpeggio when exploring words or completing a storybook.
     */
    fun playCelebrationChime() {
        audioExecutor.execute {
            try {
                val notes = listOf(0, 2, 4, 5, 7) // Pentatonic ascending
                for ((idx, note) in notes.withIndex()) {
                    playKalimbaChime(note)
                    Thread.sleep(110L)
                }
            } catch (e: Exception) {
                Log.e("AUDIO_DEBUG", "Error playing celebration chime", e)
            }
        }
    }

    /**
     * Plays a tailored sound effect corresponding to the category of the tapped object,
     * followed by speaking the Lukenye pronunciation and English word.
     */
    fun playObjectTapSoundAndSpeech(
        objectEmoji: String,
        lukenyeWord: String,
        englishWord: String,
        pronunciation: String,
        noteSeed: Int = 0,
        onComplete: () -> Unit = {}
    ) {
        // 1. Play immediate acoustic chime based on object category
        when {
            objectEmoji.contains("💧") || objectEmoji.contains("🌊") || objectEmoji.contains("🐟") ||
                    objectEmoji.contains("🐠") || objectEmoji.contains("🐡") || objectEmoji.contains("🛶") ||
                    objectEmoji.contains("🪷") -> {
                playWaterDrop()
            }
            objectEmoji.contains("🪘") || objectEmoji.contains("🏡") || objectEmoji.contains("🍲") ||
                    objectEmoji.contains("🔥") || objectEmoji.contains("🌽") -> {
                playVillageDrum()
            }
            objectEmoji.contains("🦩") || objectEmoji.contains("🐦") || objectEmoji.contains("🦅") ||
                    objectEmoji.contains("🦢") || objectEmoji.contains("🪺") -> {
                playBirdWhistle()
            }
            objectEmoji.contains("🦦") -> {
                playKatoCompanionChirp()
            }
            else -> {
                playKalimbaChime(noteSeed)
            }
        }

        // 2. Speak the pronunciation gently via TTS
        speakWord(lukenyeWord, englishWord, pronunciation, onComplete)
    }

    /**
     * Speaks the word with calm, high-intelligibility pacing.
     */
    fun speakWord(
        lukenyeWord: String,
        englishWord: String,
        pronunciation: String,
        onComplete: () -> Unit = {}
    ) {
        if (!isTtsReady || tts == null) {
            onComplete()
            return
        }

        try {
            // Clean up any bracketed labels e.g. "Emu (1)" -> "Emu"
            val cleanLukenye = lukenyeWord.replace(Regex("\\(.*\\)"), "").trim()
            val textToSpeak = "$cleanLukenye. $englishWord."

            val utteranceId = "word_${System.currentTimeMillis()}"

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onComplete()
                }
                override fun onError(utteranceId: String?) {
                    onComplete()
                }
            })

            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error in speakWord", e)
            onComplete()
        }
    }

    /**
     * Speaks a full story sentence for "Soma Nange" (Read Aloud) mode.
     */
    fun speakStorySentence(
        sentenceLukenye: String,
        sentenceEnglish: String,
        onComplete: () -> Unit = {}
    ) {
        if (!isTtsReady || tts == null) {
            onComplete()
            return
        }

        try {
            val textToSpeak = "$sentenceLukenye. ... $sentenceEnglish."
            val utteranceId = "story_${System.currentTimeMillis()}"

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onComplete()
                }
                override fun onError(utteranceId: String?) {
                    onComplete()
                }
            })

            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error in speakStorySentence", e)
            onComplete()
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error stopping TTS", e)
        }
    }

    // Helper to stream PCM buffer via AudioTrack
    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        var track: AudioTrack? = null
        try {
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(buffer.size * 2)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val format = AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .build()

            track = AudioTrack(
                attributes,
                format,
                bufferSize,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            track.write(buffer, 0, buffer.size)
            track.play()

            // Wait until played then release
            val durationMs = (buffer.size * 1000L / sampleRate) + 50L
            Thread.sleep(durationMs)
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error streaming PCM buffer to AudioTrack", e)
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (ignored: Exception) {}
        }
    }

    // Legacy method compatibility
    fun playPronunciation(audioResName: String, onComplete: () -> Unit = {}) {
        try {
            val resId = context.resources.getIdentifier(audioResName, "raw", context.packageName)
            if (resId != 0) {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                    setOnCompletionListener { onComplete() }
                    setOnErrorListener { _, _, _ ->
                        onComplete()
                        true
                    }
                    start()
                }
            } else {
                Log.w("AUDIO_DEBUG", "Raw resource '$audioResName' not found. Using safe audio fallback.")
                onComplete()
            }
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Exception in playPronunciation", e)
            onComplete()
        }
    }

    fun playKatoVoice(voiceLine: String, onComplete: () -> Unit = {}) {
        playPronunciation("kato_$voiceLine", onComplete)
    }

    fun playSuccessSound() {
        playCelebrationChime()
    }

    fun playFailureSound() {
        // Calm subtle low kalimba note
        playKalimbaChime(1)
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool?.release()
            soundPool = null
            tts?.stop()
            tts?.shutdown()
            tts = null
            audioExecutor.shutdown()
            Log.d("AUDIO_DEBUG", "AuthenticAudioManager released resources")
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error releasing AuthenticAudioManager", e)
        }
    }

    companion object {
        @Volatile
        private var instance: AuthenticAudioManager? = null

        fun getInstance(context: Context): AuthenticAudioManager {
            return instance ?: synchronized(this) {
                instance ?: AuthenticAudioManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
