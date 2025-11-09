package com.omarkarimli.cora.ui.presentation.screen.userSetup

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R // Import R class for string resources
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.useCase.userSetup.UserSetupUseCases
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
class UserSetupViewModel @Inject constructor(
    private val useCases: UserSetupUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _freeSubscriptions = MutableStateFlow<List<SubscriptionModel>>(emptyList())
    val freeSubscriptions: StateFlow<List<SubscriptionModel>> = _freeSubscriptions.asStateFlow()

    init {
        getFreeSubscription()
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun setError(@StringRes toastResId: Int, log: String) {
        _uiState.value = UiState.Error(toastResId = toastResId, log = log)
    }

    fun getFreeSubscription() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val freeSubscriptions = useCases.getFreeSubscriptionsUseCase()

                if (freeSubscriptions.isNotEmpty()) {
                    _freeSubscriptions.value = freeSubscriptions
                    resetUiState()
                } else {
                    setError(
                        toastResId = R.string.error_no_free_subscriptions,
                        log = "No free subscriptions available."
                    )
                }
            } catch (e: Exception) {
                setError(
                    toastResId = R.string.error_get_free_subscription,
                    log = e.message ?: "Failed to get free subscription."
                )
            }
        }
    }

    fun onContinue(userModel: UserModel) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                useCases.saveUserUseCase(userModel)

                _uiState.value = UiState.Success(
                    message = SuccessType.SIGN_UP,
                    route = Screen.Chat.route,
                    canToast = true
                )

                useCases.saveBooleanUseCase(SpConstant.LOGIN_KEY, true)
            } catch (e: Exception) {
                setError(
                    toastResId = R.string.error_save_user_data,
                    log = e.message ?: "Failed to save user data."
                )
            }
        }
    }
}