package com.omarkarimli.cora.ui.presentation.screen.subscriptionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import javax.inject.Inject

@HiltViewModel
class SubscriptionHistoryViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _subscriptionModels = MutableStateFlow<List<SubscriptionModel>>(emptyList())
    val subscriptionModels: StateFlow<List<SubscriptionModel>> = _subscriptionModels.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    init {
        getSubscriptions()
    }

    private fun getSubscriptions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            var success = false
            try {
                val userResult = firestoreRepository.getUser()
                userResult?.let { user ->
                    _subscriptionModels.value = user.subscriptions
                    _uiState.value = UiState.Success(
                        message = SuccessType.GET_SUBSCRIPTIONS
                    )
                    success = true
                } ?: run {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.error_something_went_wrong, // Using existing key
                        log = "Profile not found when trying to fetch subscriptions."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_load_subscriptions,
                    log = e.message ?: "Unknown error fetching subscriptions."
                )
            } finally {
                if (success) {
                    resetUiState() // Only reset to Idle if the operation was successful
                }
            }
        }
    }
}
