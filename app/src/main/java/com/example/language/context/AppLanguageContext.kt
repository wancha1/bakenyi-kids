package com.example.language.context

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LanguageMode {
    LUKENYE_PRIMARY,   // Primary language: Lukenye (Audio + Text when verified)
    ENGLISH_PRIMARY,   // Primary language: English
    DUAL_SIDE_BY_SIDE; // Adult / Parent view displaying both English & Lukenye side-by-side

    companion object {
        fun fromString(value: String): LanguageMode {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                LUKENYE_PRIMARY
            }
        }
    }
}

/**
 * Global App Language Context Manager.
 * Governs language mode settings configured via the Adult/Parent Area.
 * Uses SharedPreferences & StateFlow for lightweight, offline-first persistence.
 */
class AppLanguageContext(context: Context) {

    companion object {
        private const val PREFS_NAME = "bakenyi_language_settings"
        private const val KEY_LANGUAGE_MODE = "app_language_mode"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentLanguageMode = MutableStateFlow(loadLanguageMode())
    val currentLanguageMode: StateFlow<LanguageMode> = _currentLanguageMode.asStateFlow()

    private fun loadLanguageMode(): LanguageMode {
        val saved = prefs.getString(KEY_LANGUAGE_MODE, LanguageMode.LUKENYE_PRIMARY.name)
            ?: LanguageMode.LUKENYE_PRIMARY.name
        return LanguageMode.fromString(saved)
    }

    fun setLanguageMode(mode: LanguageMode) {
        prefs.edit().putString(KEY_LANGUAGE_MODE, mode.name).apply()
        _currentLanguageMode.value = mode
    }
}
