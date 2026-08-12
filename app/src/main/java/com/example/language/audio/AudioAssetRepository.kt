package com.example.language.audio

import android.content.Context
import android.util.Log
import com.example.language.model.LanguageContent
import java.io.File

enum class AudioAssetStatus {
    AVAILABLE,
    AUDIO_PENDING,
    FILE_NOT_FOUND,
    ERROR
}

data class AudioAssetResolution(
    val assetPath: String?,
    val status: AudioAssetStatus,
    val isHumanRecorded: Boolean,
    val debugMessage: String? = null
)

interface AudioAssetRepository {
    fun resolveLukenyeAudio(content: LanguageContent): AudioAssetResolution
    fun resolveEnglishAudio(content: LanguageContent): AudioAssetResolution
}

/**
 * Audio Asset Manager for Bakenyi Kids.
 * Enforces human-recorded audio rules: strictly forbids synthetic speech generation for Lukenye.
 */
class AssetFileAudioRepository(
    private val context: Context
) : AudioAssetRepository {

    companion object {
        private const val TAG = "AudioAssetRepository"
        const val AUDIO_PENDING_MARKER = "AUDIO_PENDING"
    }

    override fun resolveLukenyeAudio(content: LanguageContent): AudioAssetResolution {
        val rawPath = content.lukenyeAudioAsset

        if (rawPath.isNullOrBlank() || rawPath == AUDIO_PENDING_MARKER) {
            return AudioAssetResolution(
                assetPath = null,
                status = AudioAssetStatus.AUDIO_PENDING,
                isHumanRecorded = false,
                debugMessage = "Lukenye audio asset is pending human recording verification."
            )
        }

        return verifyAssetFileExists(rawPath)
    }

    override fun resolveEnglishAudio(content: LanguageContent): AudioAssetResolution {
        val rawPath = content.englishAudioAsset

        if (rawPath.isNullOrBlank() || rawPath == AUDIO_PENDING_MARKER) {
            return AudioAssetResolution(
                assetPath = null,
                status = AudioAssetStatus.AUDIO_PENDING,
                isHumanRecorded = false,
                debugMessage = "English audio asset pending."
            )
        }

        return verifyAssetFileExists(rawPath)
    }

    private fun verifyAssetFileExists(path: String): AudioAssetResolution {
        return try {
            if (path.startsWith("assets/")) {
                val assetRelativePath = path.removePrefix("assets/")
                val inputStream = context.assets.open(assetRelativePath)
                inputStream.close()
                AudioAssetResolution(
                    assetPath = path,
                    status = AudioAssetStatus.AVAILABLE,
                    isHumanRecorded = true
                )
            } else if (path.startsWith("/")) {
                val file = File(path)
                if (file.exists()) {
                    AudioAssetResolution(
                        assetPath = path,
                        status = AudioAssetStatus.AVAILABLE,
                        isHumanRecorded = true
                    )
                } else {
                    AudioAssetResolution(
                        assetPath = null,
                        status = AudioAssetStatus.FILE_NOT_FOUND,
                        isHumanRecorded = false,
                        debugMessage = "File not found at $path"
                    )
                }
            } else {
                // Default asset path check
                val inputStream = context.assets.open(path)
                inputStream.close()
                AudioAssetResolution(
                    assetPath = path,
                    status = AudioAssetStatus.AVAILABLE,
                    isHumanRecorded = true
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Audio asset not resolved: $path (${e.message})")
            AudioAssetResolution(
                assetPath = null,
                status = AudioAssetStatus.AUDIO_PENDING,
                isHumanRecorded = false,
                debugMessage = "Asset unavailable: $path"
            )
        }
    }
}
