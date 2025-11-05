package com.omarkarimli.cora.ui.presentation.screen.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R // Added for R.string access
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.AuthRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.utils.SpConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _userModel = MutableStateFlow<UserModel?>(null)
    val userModel: StateFlow<UserModel?> = _userModel.asStateFlow()

    init {
        getUser()
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun setError(@StringRes toastResId: Int, log: String) {
        _uiState.value = UiState.Error(toastResId = toastResId, log = log)
    }

    private fun getUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            var success = false
            try {
                val result = firestoreRepository.getUser()
                result?.let {
                    _userModel.value = it
                    _uiState.value = UiState.Success(
                        message = SuccessType.GET_USER
                    )
                    success = true
                } ?: run {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.error_something_went_wrong,
                        log = "User result was null in getUser()"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Exception in getUser()"
                )
            } finally {
                if (success) {
                    resetUiState()
                }
            }
        }
    }

    fun updateUser(userModel: UserModel) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                firestoreRepository.saveUser(userModel)
                _uiState.value = UiState.Success(
                    message = SuccessType.UPDATE_PROFILE,
                    canToast = true,
                    route = Screen.Settings.route
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Exception in updateUser() for user: $userModel"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                authRepository.signOut()
                sharedPreferenceRepository.saveBoolean(SpConstant.LOGIN_KEY, false)

                _uiState.value = UiState.Success(
                    message = SuccessType.SIGN_OUT,
                    canToast = true,
                    route = Screen.Auth.route
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_sign_out,
                    log = e.message ?: "Exception in signOut()"
                )
            }
        }
    }
}