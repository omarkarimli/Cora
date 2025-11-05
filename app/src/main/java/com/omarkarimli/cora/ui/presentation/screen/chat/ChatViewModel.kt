package com.omarkarimli.cora.ui.presentation.screen.chat

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.models.CreditConditions
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import com.omarkarimli.cora.domain.repository.ChatRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.PermissionRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.utils.SpConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val sharedPreferenceRepository: SharedPreferenceRepository,
    val permissionRepository: PermissionRepository,
    val firestoreRepository: FirestoreRepository,
    val chatRepository: ChatRepository,
    val historyRepo: ChatHistoryRepo,
    val translateRepository: TranslateRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isLiveTranslationEnabled = MutableStateFlow(true)

    private val _chatHistoryModel = MutableStateFlow<ChatHistoryItemModel?>(null)

    private val _userModel = MutableStateFlow<UserModel?>(null)
    val userModel: StateFlow<UserModel?> = _userModel.asStateFlow()

    val creditConditions: StateFlow<CreditConditions> = firestoreRepository.creditConditions

    val hasCameraPermission: StateFlow<Boolean> = permissionRepository.cameraPermissionState
    val hasStoragePermission: StateFlow<Boolean> = permissionRepository.storagePermissionState

    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages: StateFlow<List<MessageModel>> = _messages.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun setError(@StringRes toastResId: Int, log: String) {
        _uiState.value = UiState.Error(toastResId = toastResId, log = log)
    }

    fun getUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = firestoreRepository.getUser()
                result?.let {
                    _userModel.value = it
                } ?: run {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.error_something_went_wrong,
                        log = "User not found."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Something went wrong."
                )
            } finally {
                resetUiState()
            }
        }
    }

    fun onSend(messageModel: MessageModel) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                _messages.update { currentMessages ->
                    currentMessages + messageModel
                }

                var responseMessage = chatRepository.sendMessage(messageModel)
                responseMessage = responseMessage.copy(
                    text = translate(responseMessage.text)
                )
                _messages.update { currentMessages ->
                    currentMessages + responseMessage
                }

                val newMessages = listOf(messageModel, responseMessage)

                val updatedHistory = _chatHistoryModel.value?.copy(
                    messages = _chatHistoryModel.value!!.messages + newMessages
                ) ?: ChatHistoryItemModel(
                    title = messageModel.text,
                    messages = newMessages
                )

                historyRepo.insertInstance(updatedHistory)
                _chatHistoryModel.value = updatedHistory

                _uiState.value = UiState.Success(
                    message = SuccessType.MESSAGE_SEND
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = "Error onSend: ${e.message}"
                )
            }
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
                        log = "Error onReportIssue: ${e.message}"
                    )
                }
            }
        } ?: run {
            _uiState.value = UiState.Error(
                toastResId = R.string.error_something_went_wrong,
                log = "User not found."
            )
        }
        resetUiState()
    }

    private suspend fun translate(text: String): String {
        return if (_isLiveTranslationEnabled.value) {
            try {
                translateRepository.translate(text)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error translating text: ${e.message}")
                text
            }
        } else text
    }

    fun loadSettings() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                _isLiveTranslationEnabled.value = sharedPreferenceRepository.getBoolean(
                    SpConstant.LIVE_TRANSLATION_KEY,
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

    fun getHistory(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = historyRepo.getInstance(id)
                _chatHistoryModel.value = result
                result?.let {
                    _messages.value = it.messages
                }
                _uiState.value = UiState.Idle
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Something went wrong."
                )
            }
        }
    }

    fun onNewChat() {
        if (_messages.value.isNotEmpty()) {
            _chatHistoryModel.value = null
            _messages.value = emptyList()
        } else {
            _uiState.value = UiState.Error(
                toastResId = R.string.error_empty_chat,
                log = "Chat is empty."
            )
        }
    }
}