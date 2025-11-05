package com.omarkarimli.cora.ui.presentation.screen.success

import androidx.lifecycle.ViewModel
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SuccessViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun onContinue(route: String) {
        _uiState.value = UiState.Success(
            message = SuccessType.CONTINUE,
            route = route
        )
    }
}
