package com.omarkarimli.cora.ui.presentation.screen.upgrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.CreditConditions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.TabModel
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.utils.capitalize
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

@HiltViewModel
class UpgradeViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _userModel = MutableStateFlow<UserModel?>(null)
    val userModel: StateFlow<UserModel?> = _userModel.asStateFlow()

    val creditConditions: StateFlow<CreditConditions> = firestoreRepository.creditConditions

    private val _subscriptionModels = MutableStateFlow<List<SubscriptionModel>>(emptyList())
    val subscriptionModels: StateFlow<List<SubscriptionModel>> = _subscriptionModels.asStateFlow()

    private val _tabs = MutableStateFlow<List<TabModel>>(emptyList())
    val tabs: StateFlow<List<TabModel>> = _tabs.asStateFlow()

    private val _selectedTab = MutableStateFlow<TabModel?>(null)
    val selectedTab: StateFlow<TabModel?> = _selectedTab.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    init {
        getUserAndSubscriptions()
    }

    private suspend fun translate(sourceText: String): String {
        return try {
            val result = translateRepository.translate(sourceText)
            result
        } catch (e: Exception) {
            // Log the error but return the original text on failure
            e.printStackTrace()
            sourceText
        }
    }

    private fun getUserAndSubscriptions() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                // 1. Fetch user data
                val user = firestoreRepository.getUser()
                if (user == null) {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.error_something_went_wrong,
                        log = "User data not found in UpgradeViewModel."
                    )
                    return@launch
                }
                _userModel.value = user

                // 2. Fetch subscription types (tabs)
                val subscriptionTypes = firestoreRepository.getSubscriptionTypes().map { key ->
                    // FIX: Calling the SUSPENDING translate function here.
                    // The map operation now waits for each translation.
                    TabModel(
                        value = translate(key.lowercase().capitalize()),
                        key = key
                    )
                }

                if (subscriptionTypes.isEmpty()) {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.not_available,
                        log = "No subscription types available in UpgradeViewModel."
                    )
                    return@launch
                }
                _tabs.value = subscriptionTypes

                // 3. Set the initial selected tab and fetch models for it
                val initialTab = subscriptionTypes.first()
                _selectedTab.value = initialTab
                _subscriptionModels.value = firestoreRepository.getSubscriptionModels(initialTab.key).reversed()

                // Success
                _uiState.value = UiState.Success(message = SuccessType.GET_SUBSCRIPTIONS)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = e.message ?: "Unknown error in getUserAndSubscriptions, UpgradeViewModel."
                )
            }
        }
    }

    fun getSubscriptionModels(tabModel: TabModel) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                _subscriptionModels.value = firestoreRepository
                    .getSubscriptionModels(tabModel.key)
                    .reversed()
                _selectedTab.value = tabModel
                _uiState.value = UiState.Success(message = SuccessType.GET_SUBSCRIPTIONS)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_load_subscriptions,
                    log = "Error getting subscription models: ${e.message}"
                )
            }
        }
    }

    fun onSelectSubscription(selectedSubscription: SubscriptionModel) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                userModel.value?.let {
                    // Update User
                    val prevSubscriptions = it.subscriptions.toMutableList()
                    val updatedSubscriptions = prevSubscriptions.apply {
                        add(selectedSubscription)
                    }
                    val updatedUserModel = it.copy(
                        subscriptions = updatedSubscriptions
                    )
                    updateUser(updatedUserModel)

                    _uiState.value = UiState.Success(
                        message = SuccessType.SELECT_SUBSCRIPTION,
                        route = Screen.Success.route
                    )
                } ?: run {
                    _uiState.value = UiState.Error(
                        toastResId = R.string.error_something_went_wrong,
                        log = "Error selecting subscription: User model is null"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    toastResId = R.string.error_something_went_wrong,
                    log = "Error selecting subscription: ${e.message}"
                )
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
}