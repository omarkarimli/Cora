package com.omarkarimli.cora.ui.presentation.common.state

import androidx.annotation.StringRes

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(
        val message: String,
        val route: String? = null,
        val canToast: Boolean = false
    ) : UiState()
    data class Error(
        @StringRes val toastResId: Int,
        val log: String,
        val route: String? = null
    ) : UiState()

    // Add other common UI states as needed, e.g., Dialog, etc.
}