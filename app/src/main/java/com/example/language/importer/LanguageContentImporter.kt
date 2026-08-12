package com.example.language.importer

import com.example.language.model.LanguageContent
import com.example.language.model.VerificationStatus
import com.example.language.repository.ContentImportSummary
import com.example.language.repository.LanguageRepository
import org.json.JSONArray
import org.json.JSONObject

/**
 * Importer for verified Lukenye content supplied by the human language team.
 * Uses standard Android org.json parser to process JSON payloads safely.
 *
 * Enforces safety rules:
 * - Content without explicit human verification metadata is marked PENDING_VERIFICATION.
 * - Missing Lukenye fields are NOT auto-completed or AI-translated.
 */
class LanguageContentImporter(
    private val repository: LanguageRepository
) {

    suspend fun importJsonString(
        jsonString: String,
        defaultVerifierRef: String = "Bakenyi_Kids_Human_Language_Team"
    ): ContentImportSummary {
        val parsedList = mutableListOf<LanguageContent>()
        val errors = mutableListOf<String>()

        try {
            val jsonArray: JSONArray = if (jsonString.trim().startsWith("[")) {
                JSONArray(jsonString)
            } else {
                val rootObj = JSONObject(jsonString)
                if (rootObj.has("items")) {
                    rootObj.getJSONArray("items")
                } else {
                    throw IllegalArgumentException("Expected a JSON array or an object containing an 'items' array.")
                }
            }

            for (i in 0 until jsonArray.length()) {
                try {
                    val obj = jsonArray.getJSONObject(i)

                    val contentId = obj.optString("contentId", "import_${System.currentTimeMillis()}_$i")
                    val englishText = obj.optString("englishText", "")

                    if (englishText.isBlank()) {
                        errors.add("Item $i: Skipped due to missing englishText")
                        continue
                    }

                    val lukenyeText = parseOptionalString(obj, "lukenyeText")
                    val englishMeaning = parseOptionalString(obj, "englishMeaning")
                    val lukenyeAudio = parseOptionalString(obj, "lukenyeAudioAsset")
                    val englishAudio = parseOptionalString(obj, "englishAudioAsset")
                    val pronunciation = parseOptionalString(obj, "pronunciation")
                    val culturalContext = parseOptionalString(obj, "culturalContext")

                    val category = obj.optString("contentCategory", "nature")
                    val ageSuitability = obj.optString("ageSuitability", "4-7")

                    val statusStr = obj.optString("verificationStatus", "PENDING_VERIFICATION")
                    val status = try {
                        VerificationStatus.valueOf(statusStr.uppercase())
                    } catch (e: Exception) {
                        VerificationStatus.PENDING_VERIFICATION
                    }

                    // Strict Safety Rule: If Lukenye text is missing, status CANNOT be VERIFIED
                    val finalStatus = if (lukenyeText == null && status == VerificationStatus.VERIFIED) {
                        errors.add("Item $contentId: Changed status from VERIFIED to PENDING_VERIFICATION due to missing lukenyeText.")
                        VerificationStatus.PENDING_VERIFICATION
                    } else {
                        status
                    }

                    val source = parseOptionalString(obj, "source") ?: defaultVerifierRef
                    val version = obj.optInt("version", 1)
                    val active = obj.optBoolean("active", true)

                    val languageContent = LanguageContent(
                        contentId = contentId,
                        englishText = englishText,
                        lukenyeText = lukenyeText,
                        englishMeaning = englishMeaning,
                        lukenyeAudioAsset = lukenyeAudio,
                        englishAudioAsset = englishAudio,
                        pronunciation = pronunciation,
                        culturalContext = culturalContext,
                        contentCategory = category,
                        ageSuitability = ageSuitability,
                        verificationStatus = finalStatus,
                        source = source,
                        version = version,
                        active = active
                    )

                    parsedList.add(languageContent)

                } catch (e: Exception) {
                    errors.add("Item $i: Failed to parse (${e.message})")
                }
            }

            val importResult = repository.importVerifiedHumanBatch(parsedList, defaultVerifierRef)
            return importResult.copy(errors = importResult.errors + errors)

        } catch (e: Exception) {
            return ContentImportSummary(
                totalImported = 0,
                verifiedCount = 0,
                pendingCount = 0,
                rejectedCount = 0,
                errors = listOf("Failed to parse import payload: ${e.message}")
            )
        }
    }

    private fun parseOptionalString(obj: JSONObject, key: String): String? {
        if (!obj.has(key) || obj.isNull(key)) return null
        val value = obj.optString(key, "").trim()
        if (value.isEmpty() || value.equals("null", ignoreCase = true)) return null
        return value
    }
}
