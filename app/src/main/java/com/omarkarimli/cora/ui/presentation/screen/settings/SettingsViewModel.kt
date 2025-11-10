package com.omarkarimli.cora.ui.presentation.screen.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.main.MainViewModel
import com.omarkarimli.cora.ui.theme.AppTheme
import com.omarkarimli.cora.utils.SpConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val firestoreRepository: FirestoreRepository,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _userModel = MutableStateFlow<UserModel?>(null)
    val userModel: StateFlow<UserModel?> = _userModel.asStateFlow()

    private val _savingPath = MutableStateFlow("")
    val savingPath: StateFlow<String> = _savingPath.asStateFlow()

    private val _isNotificationsEnabled = MutableStateFlow(false)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    private val _isLiveTranslationEnabled = MutableStateFlow(true)
    val isLiveTranslationEnabled: StateFlow<Boolean> = _isLiveTranslationEnabled.asStateFlow()

    init {
        loadSettings()
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
            try {
                val result = firestoreRepository.getUser()
                result?.let {
                    _userModel.value = it
                } ?: run {
                    _uiState.value = UiState.Error(R.string.error_something_went_wrong, "Profile not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Exception in getUser()"
                )
            } finally {
                resetUiState()
            }
        }
    }

    private fun loadSettings() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                _isLiveTranslationEnabled.value = sharedPreferenceRepository.getBoolean(
                    SpConstant.LIVE_TRANSLATION_KEY,
                    true
                )

                _savingPath.value = sharedPreferenceRepository.getString(
                    SpConstant.SAVING_PATH_KEY,
                    "photos"
                )

                _isNotificationsEnabled.value = sharedPreferenceRepository.getBoolean(
                    SpConstant.NOTIFICATION_KEY,
                    true
                )

                resetUiState()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Exception in loadSettings()",
                )
            }
        }
    }

    fun onSavingPathToggle(savingPath: String) {
        viewModelScope.launch {
            _savingPath.value = savingPath
            sharedPreferenceRepository.saveString(SpConstant.SAVING_PATH_KEY, savingPath)
            _uiState.value = UiState.Success(
                message = SuccessType.SETTINGS_UPDATE,
            )
        }
    }

    fun onLiveTranslationToggle(isEnabled: Boolean) {
        viewModelScope.launch {
            _isLiveTranslationEnabled.value = isEnabled
            sharedPreferenceRepository.saveBoolean(SpConstant.LIVE_TRANSLATION_KEY, isEnabled)
            _uiState.value = UiState.Success(
                message = SuccessType.SETTINGS_UPDATE,
            )
        }
    }

    fun onNotificationsToggle(isEnabled: Boolean) {
        viewModelScope.launch {
            var isPermissionGranted = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (isPermissionGranted) {
                _isNotificationsEnabled.value = isEnabled
                sharedPreferenceRepository.saveBoolean(SpConstant.NOTIFICATION_KEY, isEnabled)
                _uiState.value = UiState.Success(
                    message = SuccessType.SETTINGS_UPDATE,
                )
            } else {
                _isNotificationsEnabled.value = false
                sharedPreferenceRepository.saveBoolean(SpConstant.NOTIFICATION_KEY, false)
                _uiState.value = UiState.Error(
                    toastResId = R.string.permission_denied,
                    log = "Notification permission denied"
                )
            }
        }
    }

    fun onResetSettings(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            sharedPreferenceRepository.clearSharedPreferences()
            sharedPreferenceRepository.saveBoolean(SpConstant.LOGIN_KEY, true)

            loadSettings()

            // Reset UI
            mainViewModel.onThemeChange(AppTheme.System)
            mainViewModel.onDynamicColorToggle(false)
        }
    }

    fun onReportIssue(reportIssueModel: ReportIssueModel) {
        _uiState.value = UiState.Loading

        _userModel.value?.let {
            viewModelScope.launch {
                try {
                    firestoreRepository.addReportIssue(reportIssueModel)

                    _uiState.value = UiState.Success(
                        message = SuccessType.REPORT_SEND,
                        canToast = true
                    )
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.error_something_went_wrong,
                        log = e.message ?: "Exception in onReportIssue()"
                    )
                }
            }
        } ?: run {
            _uiState.value = UiState.Error(R.string.error_something_went_wrong, "Profile not found")
        }

        resetUiState()
    }

    fun onLangChange(onChange: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                onChange()
                translateRepository.downloadModel()

                _uiState.value = UiState.Success(
                    message = SuccessType.SETTINGS_UPDATE,
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Exception in onLangChange()"
                )
            } finally {
                resetUiState()
            }
        }
    }
}
