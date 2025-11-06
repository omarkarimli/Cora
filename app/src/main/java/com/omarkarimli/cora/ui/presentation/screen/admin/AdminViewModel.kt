package com.omarkarimli.cora.ui.presentation.screen.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.repository.AdminRepository
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepo: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    private fun setError(e: Exception) {
        _uiState.value = UiState.Error(
            toastResId = R.string.error_something_went_wrong,
            log = e.message ?: "An unknown error occurred in AdminViewModel."
        )
    }

    fun setGuidelines() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                adminRepo.setGuidelines()
                _uiState.value = UiState.Success(message = SuccessType.SET_GUIDELINES)
            } catch (e: Exception) {
                setError(e)
            } finally {
                resetUiState()
            }
        }
    }

    fun setSubscriptions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                adminRepo.setSubscriptionModels()
                _uiState.value = UiState.Success(message = SuccessType.SET_SUBSCRIPTIONS)
            } catch (e: Exception) {
                setError(e)
            } finally {
                resetUiState()
            }
        }
    }
}