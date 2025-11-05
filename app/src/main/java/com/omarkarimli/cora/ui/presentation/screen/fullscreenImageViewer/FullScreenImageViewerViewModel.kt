package com.omarkarimli.cora.ui.presentation.screen.fullscreenImageViewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.R // Ensure R is imported
import com.omarkarimli.cora.domain.repository.DownloadRepository
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import javax.inject.Inject

@HiltViewModel
class FullScreenImageViewerViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun downloadImage(imageUrl: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            var success = false
            try {
                downloadRepository.downloadImage(imageUrl)

                _uiState.value = UiState.Success(
                    message = SuccessType.DOWNLOAD_COMPLETE,
                    canToast = true,
                )
                success = true
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    log = e.message ?: "Download failed for imageUrl: $imageUrl",
                    toastResId = R.string.error_something_went_wrong
                )
            } finally {
                if (success) { // Only reset to Idle if the operation was successful
                    resetUiState()
                }
            }
        }
    }
}
