package com.example.arogyagrid.core

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    HINDI("hi", "Hindi", "हिन्दी"),
    ENGLISH("en", "English", "English")
}

object LanguageManager {
    private val _currentLanguage = MutableStateFlow(AppLanguage.HINDI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("arogyagrid_lang", Context.MODE_PRIVATE)
        val saved = prefs.getString("selected_lang", "hi")
        _currentLanguage.value = if (saved == "en") AppLanguage.ENGLISH else AppLanguage.HINDI
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        UserSession.getContextOrNull()?.let { ctx ->
            ctx.getSharedPreferences("arogyagrid_lang", Context.MODE_PRIVATE)
                .edit()
                .putString("selected_lang", language.code)
                .apply()
        }
    }

    fun toggleLanguage() {
        val next = if (_currentLanguage.value == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
        setLanguage(next)
    }

    fun isHindi(): Boolean = _currentLanguage.value == AppLanguage.HINDI

    /**
     * Resolves bilingual string based on active app language
     */
    fun s(hi: String, en: String): String {
        return if (_currentLanguage.value == AppLanguage.ENGLISH) en else hi
    }
}
