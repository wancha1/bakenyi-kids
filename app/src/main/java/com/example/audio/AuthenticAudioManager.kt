package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

class AuthenticAudioManager private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null

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

            Log.d("AUDIO_DEBUG", "AuthenticAudioManager initialized successfully")
        } catch (e: Exception) {
            Log.e("AUDIO_DEBUG", "Error initializing SoundPool", e)
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

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool?.release()
            soundPool = null
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
