package com.omarkarimli.cora.ui.presentation.screen.guidelines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import javax.inject.Inject

@HiltViewModel
class GuidelinesViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _guidelineModels = MutableStateFlow<List<GuidelineModel>>(emptyList())
    val guidelineModels: StateFlow<List<GuidelineModel>> = _guidelineModels.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    init {
        getGuidelines()
    }

    private fun getGuidelines() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            var success = false
            try {
                val result = firestoreRepository.getGuidelines()
                _guidelineModels.value = result
                translate(result)

                _uiState.value = UiState.Success(
                    message = SuccessType.GET_GUIDELINES
                )
                success = true
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    log = e.message ?: "Error fetching guidelines",
                    toastResId = R.string.error_something_went_wrong
                )
            } finally {
                if (success) { // Only reset to Idle if the operation was successful
                    resetUiState()
                }
            }
        }
    }

    fun translate(items: List<GuidelineModel>) {
        viewModelScope.launch {
            try {
                val translations = items.map { item ->
                    item.copy(
                        title = translateRepository.translate(item.title),
                        description = translateRepository.translate(item.description)
                    )
                }
                _guidelineModels.value = translations

            } catch (e: Exception) {
                Log.e("GuidelinesViewModel", "Translation failed: ${e.message}")
            }
        }
    }
}
