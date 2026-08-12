package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.ToneGenerator
import android.util.Log

class AuthenticAudioManager private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var toneGenerator: ToneGenerator? = null

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

            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

            Log.d("AUDIO_DEBUG", "AuthenticAudioManager initialized successfully")
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error initializing SoundPool or ToneGenerator", e)
            Log.e("BAKENYE_CRASH", "Audio initialization error caught safely", e)
        }
    }

    fun playPronunciation(audioResName: String, onComplete: () -> Unit = {}) {
        Log.d("AUDIO_DEBUG", "playPronunciation requested for: $audioResName")
        try {
            // Check if audio raw resource exists
            val resId = context.resources.getIdentifier(audioResName, "raw", context.packageName)
            if (resId != 0) {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                    setOnCompletionListener {
                        Log.d("AUDIO_DEBUG", "Audio playback completed for: $audioResName")
                        onComplete()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AUDIO_DEBUG", "MediaPlayer error: what=$what, extra=$extra")
                        onComplete()
                        true
                    }
                    start()
                }
            } else {
                Log.w("AUDIO_DEBUG", "Raw resource '$audioResName' not found. Using safe audio fallback.")
                // Graceful completion callback so UI state resets without hanging or crashing
                onComplete()
            }
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Exception during audio playback of $audioResName", e)
            Log.e("BAKENYE_CRASH", "Handled audio crash safely", e)
            onComplete()
        }
    }

    fun playKatoVoice(voiceLine: String, onComplete: () -> Unit = {}) {
        Log.d("AUDIO_DEBUG", "playKatoVoice: $voiceLine")
        playPronunciation("kato_$voiceLine", onComplete)
    }

    fun playSuccessSound() {
        Log.d("AUDIO_DEBUG", "playSuccessSound requested")
        try {
            val resId = context.resources.getIdentifier("snd_success", "raw", context.packageName)
            if (resId != 0) {
                playPronunciation("snd_success")
            } else {
                // Synthesize energetic double-chime success sound
                Thread {
                    try {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
                        Thread.sleep(110)
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
                    } catch (e: Exception) {
                        Log.e("AUDIO_DEBUG", "Tone playback error", e)
                    }
                }.start()
            }
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Exception during success sound playback", e)
        }
    }

    fun playFailureSound() {
        Log.d("AUDIO_DEBUG", "playFailureSound requested")
        try {
            val resId = context.resources.getIdentifier("snd_failure", "raw", context.packageName)
            if (resId != 0) {
                playPronunciation("snd_failure")
            } else {
                // Synthesize low failure buzz sound
                Thread {
                    try {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
                    } catch (e: Exception) {
                        Log.e("AUDIO_DEBUG", "Tone playback error", e)
                    }
                }.start()
            }
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Exception during failure sound playback", e)
        }
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool?.release()
            soundPool = null
            toneGenerator?.release()
            toneGenerator = null
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
