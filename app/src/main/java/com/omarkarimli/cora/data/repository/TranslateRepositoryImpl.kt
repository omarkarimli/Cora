package com.omarkarimli.cora.data.repository

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.omarkarimli.cora.domain.models.TranslateModel
import com.omarkarimli.cora.domain.models.appLanguages
import com.omarkarimli.cora.domain.repository.LangRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton // Added for better scoping if this is a singleton repo

@Singleton // Good practice for a repository managing shared resources like ML models
class TranslateRepositoryImpl @Inject constructor(
    private val langRepository: LangRepository
) : TranslateRepository {

    // Changed to private set to ensure it's only modified internally (good practice for MutableStateFlow)
    private val _translations = MutableStateFlow<List<TranslateModel>>(emptyList())
    override val translations: StateFlow<List<TranslateModel>> = _translations.asStateFlow()

    private var translator: Translator? = null
    private var currentTargetLang: String? = null

    // Conditions moved outside the class or initialized cleanly
    private val conditions = DownloadConditions.Builder()
        //.requireWifi() // Uncomment if required
        .build()

    /**
     * Creates and manages the Translator instance.
     * 1. Ensures thread safety using synchronized block.
     * 2. Closes the old translator before creating a new one if the language changes.
     */
    @Synchronized // Ensures only one thread can modify/access 'translator' at a time
    private fun getTranslator(): Translator {
        val targetLang = langRepository.getLanguageCode()

        if (translator != null && targetLang == currentTargetLang) {
            return translator!!
        }

        // Language changed or translator is null, close the old one if it exists
        translator?.close()
        translator = null

        currentTargetLang = targetLang

        // Use a default language if targetLang is not valid, although ML Kit usually handles this.
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLang)
            .build()

        // Get a new translator instance
        translator = Translation.getClient(options)
        return translator!!
    }

    override suspend fun downloadModel() {
        val targetLangCode = langRepository.getLanguageCode()

        // Skip download if the target is English (source language)
        if (targetLangCode == appLanguages.first().code) {
            Log.d("TranslationRepositoryImpl", "Skipping model download for source language: $targetLangCode")
            return
        }

        try {
            // Re-use the existing translator or get a new one
            Log.d("TranslationRepositoryImpl", "Attempting to download model for: $targetLangCode")
            getTranslator().downloadModelIfNeeded(conditions).await()
            Log.d("TranslationRepositoryImpl", "Model downloaded successfully for: $targetLangCode")
        } catch (e: Exception) {
            Log.e("TranslationRepositoryImpl", "downloadModel failed for $targetLangCode: ${e.message}", e)
            // It's usually better to propagate this error or handle it more gracefully in the UI/VM
        }
    }

    override suspend fun translate(sourceText: String): String {
        // 1. Efficiency check: If target is English, return source immediately.
        if (langRepository.getLanguageCode() == appLanguages.first().code) {
            // Assuming appLanguages.first().code is "en" or equivalent to TranslateLanguage.ENGLISH
            return sourceText
        }

        // 2. Short-circuit for empty text
        if (sourceText.isBlank()) {
            return sourceText
        }

        // 3. Check if translation is already in the StateFlow
        _translations.value.firstOrNull { it.sourceText == sourceText }?.let {
            return it.translatedText
        }

        return try {
            val translatorInstance = getTranslator()

            // Ensure model is downloaded before translating, with retry logic.
            try {
                translatorInstance.downloadModelIfNeeded(conditions).await()
            } catch (downloadException: Exception) {
                Log.w("TranslationRepositoryImpl", "Model download failed, deleting and retrying.", downloadException)
                deleteModel(langRepository.getLanguageCode())
                translatorInstance.downloadModelIfNeeded(conditions).await()
            }

            val translatedText = translatorInstance.translate(sourceText).await()

            if (translatedText.isNullOrBlank() || translatedText == sourceText) {
                // Return original text if translation failed or returned the same text
                sourceText
            } else {
                // Only update the StateFlow if a successful, non-duplicate translation occurred
                _translations.value += TranslateModel(sourceText, translatedText)
                translatedText
            }
        } catch (e: Exception) {
            // If translation fails (e.g., network error, model not found), log and return source.
            Log.e("TranslationRepositoryImpl", "translate failed: ${e.message}", e)
            sourceText
        }
    }

    private suspend fun deleteModel(languageCode: String) {
        val modelManager = RemoteModelManager.getInstance()
        val model = TranslateRemoteModel.Builder(languageCode).build()
        try {
            modelManager.deleteDownloadedModel(model).await()
            Log.i("TranslationRepositoryImpl", "Deleted model for language: $languageCode")
        } catch (e: Exception) {
            Log.e("TranslationRepositoryImpl", "Failed to delete model for language: $languageCode", e)
        }
    }
    
    override fun close() {
        translator?.close()
        translator = null
        currentTargetLang = null
    }
}