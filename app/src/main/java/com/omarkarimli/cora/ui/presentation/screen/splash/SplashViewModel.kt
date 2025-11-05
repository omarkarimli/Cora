package com.omarkarimli.cora.ui.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.theme.Durations.SPLASH
import com.omarkarimli.cora.utils.SpConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val sharedPreferenceRepository: SharedPreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        getLoginKey()
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun getLoginKey() {
        viewModelScope.launch {
            delay(SPLASH)

            val loginKey = sharedPreferenceRepository
                .getBoolean(SpConstant.LOGIN_KEY, false)

            _uiState.value = UiState.Success(
                message = SuccessType.LOG_IN,
                route = if (loginKey) Screen.Chat.route else Screen.Auth.route
            )
        }
    }
}