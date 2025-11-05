package com.omarkarimli.cora.ui.presentation.common.widget.carousel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.domain.models.CarouselModel
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCarouselViewModel @Inject constructor(
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _translatedItems = MutableStateFlow<List<CarouselModel>>(emptyList())
    val translatedItems: StateFlow<List<CarouselModel>> = _translatedItems.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun setError(@StringRes toastResId: Int, log: String) {
        _uiState.value = UiState.Error(toastResId = toastResId, log = log)
    }

    fun translate(carouselItems: List<CarouselModel>) {
        viewModelScope.launch {
            try {
                val translations = carouselItems.map { item ->
                    item.copy(
                        title = translateRepository.translate(item.title)
                    )
                }
                _translatedItems.value = translations

            } catch (e: Exception) {
                Log.e("MyCarouselViewModel", "Translation failed: ${e.message}")
            }
        }
    }
}