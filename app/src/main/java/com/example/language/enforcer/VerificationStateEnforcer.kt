package com.example.language.enforcer

import com.example.language.model.LanguageContent
import com.example.language.model.RenderableLanguageContent
import com.example.language.model.VerificationStatus

/**
 * Enforcement layer for DEC-03 Content Safety Rules.
 * Guarantees that unverified or AI-generated Lukenye content is NEVER
 * silently rendered as authoritative learning content in the child experience.
 */
object VerificationStateEnforcer {

    const val PendingVerificationMarker = "PENDING_VERIFICATION"
    const val AudioPendingMarker = "AUDIO_PENDING"
    const val CulturalContentPendingMarker = "CULTURAL_CONTENT_PENDING"

    /**
     * Determines whether language content is eligible for presentation in the child experience.
     * MUST be active, status MUST be VERIFIED, and non-empty Lukenye text MUST be provided.
     */
    fun isAllowedInChildExperience(content: LanguageContent): Boolean {
        if (!content.active) return false
        if (content.verificationStatus != VerificationStatus.VERIFIED) return false
        if (content.lukenyeText.isNullOrBlank()) return false
        return true
    }

    /**
     * Filters a collection of language items strictly for the child learning experience.
     * Unverified items are excluded.
     */
    fun filterForChildExperience(list: List<LanguageContent>): List<LanguageContent> {
        return list.filter { isAllowedInChildExperience(it) }
    }

    /**
     * Formats content safely for UI rendering based on current verification status and target mode.
     * In development/CMS views, pending states are clearly flagged.
     * In child view, only verified Lukenye is presented; if unverified, falls back gracefully to English
     * with an explicit dev placeholder flag rather than fabricating Lukenye.
     */
    fun resolveRenderableContent(
        content: LanguageContent,
        preferLukenye: Boolean = true,
        isDevMode: Boolean = false
    ): RenderableLanguageContent {
        val isVerified = content.verificationStatus == VerificationStatus.VERIFIED
        val hasLukenye = !content.lukenyeText.isNullOrBlank()

        val primaryText: String
        val secondaryText: String?
        val isPlaceholder: Boolean

        if (preferLukenye && isVerified && hasLukenye) {
            // High-confidence verified Lukenye
            primaryText = content.lukenyeText!!
            secondaryText = content.englishText
            isPlaceholder = false
        } else if (isDevMode) {
            // Development / Content Management Mode
            primaryText = content.englishText
            secondaryText = if (hasLukenye) {
                "[${content.verificationStatus.name}] ${content.lukenyeText}"
            } else {
                "[$PendingVerificationMarker: Lukenye translation awaiting human verification]"
            }
            isPlaceholder = true
        } else {
            // Child experience when content is pending or Lukenye is unavailable
            primaryText = content.englishText
            secondaryText = null // Do NOT display unverified Lukenye text to child
            isPlaceholder = !isVerified
        }

        // Resolve audio asset status
        val audioPath: String?
        val hasAudio: Boolean
        val audioStatusMsg: String?

        if (preferLukenye) {
            if (!content.lukenyeAudioAsset.isNullOrBlank() && content.lukenyeAudioAsset != AudioPendingMarker) {
                audioPath = content.lukenyeAudioAsset
                hasAudio = true
                audioStatusMsg = null
            } else {
                audioPath = null
                hasAudio = false
                audioStatusMsg = AudioPendingMarker
            }
        } else {
            if (!content.englishAudioAsset.isNullOrBlank() && content.englishAudioAsset != AudioPendingMarker) {
                audioPath = content.englishAudioAsset
                hasAudio = true
                audioStatusMsg = null
            } else {
                audioPath = null
                hasAudio = false
                audioStatusMsg = AudioPendingMarker
            }
        }

        // Resolve cultural context note
        val culturalNote = if (!content.culturalContext.isNullOrBlank() && content.culturalContext != CulturalContentPendingMarker) {
            content.culturalContext
        } else {
            if (isDevMode) CulturalContentPendingMarker else null
        }

        return RenderableLanguageContent(
            contentId = content.contentId,
            primaryText = primaryText,
            secondaryText = secondaryText,
            audioAssetPath = audioPath,
            hasAudio = hasAudio,
            audioStatusMessage = audioStatusMsg,
            culturalNote = culturalNote,
            isVerified = isVerified,
            isPlaceholderState = isPlaceholder
        )
    }
}
