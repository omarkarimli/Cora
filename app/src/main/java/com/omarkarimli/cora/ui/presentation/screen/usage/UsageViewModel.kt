package com.omarkarimli.cora.ui.presentation.screen.usage

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsageViewModel @Inject constructor(
    val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _userModel = MutableStateFlow<UserModel?>(null)
    val userModel: StateFlow<UserModel?> = _userModel.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    // Updated setError to take a StringRes Int for the toast message
    fun setError(@StringRes toastResId: Int, log: String) {
        _uiState.value = UiState.Error(toastResId = toastResId, log = log)
    }

    fun getUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            var success = false
            try {
                val result = firestoreRepository.getUser()
                _userModel.value = result

                _uiState.value = UiState.Success(
                    message = SuccessType.GET_USER
                )
                success = true
            } catch (e: Exception) {
                // Use the updated setError with a string resource
                setError(
                    toastResId = R.string.error_something_went_wrong, // Standard error message
                    log = e.message ?: "An unknown error occurred in getUser()"
                )
            } finally {
                // Only reset to Idle if the operation was successful and no error occurred before finally
                if (success) {
                    resetUiState()
                }
            }
        }
    }
}