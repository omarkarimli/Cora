package com.omarkarimli.cora.ui.presentation.screen.auth

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes // Added import
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.omarkarimli.cora.R // Ensured R is imported
import com.omarkarimli.cora.data.local.Converters
import com.omarkarimli.cora.ui.presentation.common.state.SuccessType
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.AuthRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.utils.SpConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun setError(@StringRes toastResId: Int, log: String) {
        _uiState.value = UiState.Error(toastResId = toastResId, log = log)
    }

    fun getGoogleSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient.signInIntent
    }

    fun handleGoogleAuthResult(initialUserModel: UserModel) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(initialUserModel.idToken, null)
                authRepository.signInWithCredential(credential)

                val getUser = firestoreRepository.getUser()

                if (getUser != null
                    && getUser.personalInfo.username.isNotEmpty()
                    && getUser.personalInfo.gender.isNotEmpty())
                {
                    _uiState.value = UiState.Success(
                        message = SuccessType.SIGN_IN,
                        route = Screen.Chat.route,
                        canToast = true
                    )

                    sharedPreferenceRepository.saveBoolean(SpConstant.LOGIN_KEY, true)
                } else {
                    _uiState.value = UiState.Success(
                        message = SuccessType.SIGN_UP,
                        route = "${Screen.UserSetup.route}/${Converters().fromUserModel(initialUserModel)}"
                    )
                }
            } catch (e: Exception) {
                setError(toastResId = R.string.error_google_sign_in_failed, log = e.message ?: "Google Sign-In failed")
            }
        }
    }
}