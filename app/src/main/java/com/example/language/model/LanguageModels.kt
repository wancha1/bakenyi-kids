package com.example.language.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Verification status for heritage language (Lukenye) and cultural content.
 * As per DEC-03, only VERIFIED content may appear as authoritative learning
 * content in the child experience.
 */
enum class VerificationStatus {
    PENDING_VERIFICATION,
    VERIFIED,
    REJECTED,
    NEEDS_REVISION,
    ARCHIVED;

    fun isVerified(): Boolean = this == VERIFIED
}

/**
 * Data model for language content in Bakenyi Kids.
 * Separates the CONTENT ENGINE from LANGUAGE CONTENT so human-verified Lukenye
 * vocabulary, audio, and cultural context can be imported seamlessly without code changes.
 */
data class LanguageContent(
    val contentId: String,
    val englishText: String,
    val lukenyeText: String? = null,
    val englishMeaning: String? = null,
    val lukenyeAudioAsset: String? = null,
    val englishAudioAsset: String? = null,
    val pronunciation: String? = null,        // Supplied ONLY by human language team
    val culturalContext: String? = null,     // Supplied ONLY by human cultural team
    val contentCategory: String = "nature",  // e.g. nature, greetings, flora_fauna, story
    val ageSuitability: String = "4-7",
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING_VERIFICATION,
    val source: String? = null,               // Verifier reference or human team attribution
    val version: Int = 1,
    val active: Boolean = true
)

/**
 * Room Database Entity for persistent storage of language content.
 */
@Entity(tableName = "language_content")
data class LanguageContentEntity(
    @PrimaryKey val contentId: String,
    val englishText: String,
    val lukenyeText: String?,
    val englishMeaning: String?,
    val lukenyeAudioAsset: String?,
    val englishAudioAsset: String?,
    val pronunciation: String?,
    val culturalContext: String?,
    val contentCategory: String,
    val ageSuitability: String,
    val verificationStatus: String, // String representation of VerificationStatus
    val source: String?,
    val version: Int,
    val active: Boolean,
    val updatedAtTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): LanguageContent {
        val status = try {
            VerificationStatus.valueOf(verificationStatus)
        } catch (e: Exception) {
            VerificationStatus.PENDING_VERIFICATION
        }
        return LanguageContent(
            contentId = contentId,
            englishText = englishText,
            lukenyeText = lukenyeText,
            englishMeaning = englishMeaning,
            lukenyeAudioAsset = lukenyeAudioAsset,
            englishAudioAsset = englishAudioAsset,
            pronunciation = pronunciation,
            culturalContext = culturalContext,
            contentCategory = contentCategory,
            ageSuitability = ageSuitability,
            verificationStatus = status,
            source = source,
            version = version,
            active = active
        )
    }

    companion object {
        fun fromDomainModel(domain: LanguageContent): LanguageContentEntity {
            return LanguageContentEntity(
                contentId = domain.contentId,
                englishText = domain.englishText,
                lukenyeText = domain.lukenyeText,
                englishMeaning = domain.englishMeaning,
                lukenyeAudioAsset = domain.lukenyeAudioAsset,
                englishAudioAsset = domain.englishAudioAsset,
                pronunciation = domain.pronunciation,
                culturalContext = domain.culturalContext,
                contentCategory = domain.contentCategory,
                ageSuitability = domain.ageSuitability,
                verificationStatus = domain.verificationStatus.name,
                source = domain.source,
                version = domain.version,
                active = domain.active,
                updatedAtTimestamp = System.currentTimeMillis()
            )
        }
    }
}

/**
 * Safe rendered view of language content for UI display.
 */
data class RenderableLanguageContent(
    val contentId: String,
    val primaryText: String,
    val secondaryText: String?,
    val audioAssetPath: String?,
    val hasAudio: Boolean,
    val audioStatusMessage: String?,
    val culturalNote: String?,
    val isVerified: Boolean,
    val isPlaceholderState: Boolean
)
