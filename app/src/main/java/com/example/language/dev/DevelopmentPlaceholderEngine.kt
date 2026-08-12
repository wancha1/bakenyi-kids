package com.example.language.dev

import com.example.language.model.LanguageContent
import com.example.language.model.VerificationStatus

/**
 * Development Engine for Bakenyi Kids.
 * Generates clearly marked placeholder data structures for system engineering, UI wireframing,
 * and integration tests.
 *
 * SAFETY MANDATE:
 * Placeholder Lukenye fields are explicitly marked with [PENDING_VERIFICATION] and never contain
 * fabricated, guessed, or AI-generated pseudo-Lukenye words.
 */
object DevelopmentPlaceholderEngine {

    const val DEV_VERIFIER_REF = "Bakenyi_Kids_Dev_Engine"

    /**
     * Creates a placeholder LanguageContent item for development/testing.
     * Lukenye fields are explicitly null or marked with pending placeholder badges.
     */
    fun createDevelopmentPlaceholder(
        contentId: String,
        englishText: String,
        category: String = "nature",
        englishMeaning: String? = null
    ): LanguageContent {
        return LanguageContent(
            contentId = contentId,
            englishText = englishText,
            lukenyeText = null, // Strictly null until human team verifies
            englishMeaning = englishMeaning ?: englishText,
            lukenyeAudioAsset = null, // AUDIO_PENDING state
            englishAudioAsset = null,
            pronunciation = null, // Strictly null (no AI pronunciation guides)
            culturalContext = null, // Strictly null (no AI cultural stories)
            contentCategory = category,
            ageSuitability = "4-7",
            verificationStatus = VerificationStatus.PENDING_VERIFICATION,
            source = DEV_VERIFIER_REF,
            version = 1,
            active = true
        )
    }

    /**
     * Generates a starter set of placeholder content items for initial application layout testing.
     * Zero Lukenye words are fabricated.
     */
    fun getStarterDevelopmentPlaceholders(): List<LanguageContent> {
        return listOf(
            createDevelopmentPlaceholder("nature_tree_01", "Tree", "nature", "A tall living plant in the sanctuary"),
            createDevelopmentPlaceholder("nature_river_02", "River", "nature", "Flowing water through the grove"),
            createDevelopmentPlaceholder("nature_bird_03", "Bird", "fauna", "Feathered creature of the canopy"),
            createDevelopmentPlaceholder("nature_sun_04", "Sun", "nature", "Warm morning light"),
            createDevelopmentPlaceholder("greeting_morning_05", "Good Morning", "greetings", "Gentle morning greeting"),
            createDevelopmentPlaceholder("greeting_welcome_06", "Welcome", "greetings", "Warm welcome to the sanctuary")
        )
    }
}
