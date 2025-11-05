package com.omarkarimli.cora.ui.presentation.screen.auth

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.PersonalInfoModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.AnimatedText
import com.omarkarimli.cora.ui.presentation.common.widget.component.FadeOverlay
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.onSurfaceLight
import com.omarkarimli.cora.ui.theme.surfaceLight
import com.omarkarimli.cora.utils.boxShadow
import com.omarkarimli.cora.utils.showToast
import com.omarkarimli.cora.utils.toGradientText
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    val currentScreen = Screen.Auth.route
    val viewModel: AuthViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        coroutineScope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data).await()
                task.idToken?.let { idToken ->
                    viewModel.handleGoogleAuthResult(
                        UserModel(
                            idToken = idToken,
                            personalInfo = PersonalInfoModel(
                                firstName = task.givenName ?: "",
                                lastName = task.familyName ?: "",
                                email = task.email ?: "",
                                profilePictureUrl = "${task.photoUrl}"
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                val msg = "Google Sign-In failed"
                viewModel.setError(
                    toastResId = R.string.error_google_sign_in_failed, // Updated to use StringRes
                    log = e.message ?: msg
                )
            }
        }
    }

    LaunchedEffect(uiState) {
        when (val currentState = uiState) { // Added currentState for clarity
            UiState.Loading -> { /* Handle loading if needed */ }
            is UiState.Success -> {
                val successState = currentState // Use currentState
                if (successState.canToast) {
                    context.showToast(successState.message)
                }
                successState.route?.let {
                    navController.navigate(it) {
                        popUpTo(currentScreen) { inclusive = true }
                    }
                }

                Log.d(currentScreen, "Success: ${successState.message}")
                viewModel.resetUiState()
            }
            is UiState.Error -> {
                val errorState = currentState // Use currentState
                val log = errorState.log
                val toastResId = errorState.toastResId // Renamed for clarity

                context.showToast(context.getString(toastResId)) // Updated to use getString()

                Log.e(currentScreen, log)
                viewModel.resetUiState()
            }
            is UiState.ActionRequired -> {}
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold { innerPadding ->
        ScrollContent(
            innerPadding = innerPadding,
            context = context,
            onGetStarted = {
                val signInIntent = viewModel.getGoogleSignInIntent(context)
                googleSignInLauncher.launch(signInIntent)
            }
        )

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    context: Context,
    onGetStarted: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.onboarding),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        FadeOverlay(
            tint = surfaceLight,
            alpha = 0.3f
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = Dimens.PaddingExtraLarge + Dimens.PaddingMedium),
            text = stringResource(id = R.string.app_name).uppercase(Locale.ROOT),
            style = AppTypography.titleLarge,
            color = surfaceLight,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(innerPadding)
                .padding(vertical = Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            AnimatedText(
                modifier = Modifier.padding(
                    bottom = Dimens.PaddingLarge,
                    start = Dimens.PaddingLarge,
                    end = Dimens.PaddingLarge
                ),
                style = AppTypography.headlineSmall.toGradientText(Dimens.gradientColors2),
                textAlign = TextAlign.Start
            )
            WideButton(
                modifier = Modifier
                    .padding(horizontal = Dimens.PaddingLarge)
                    .boxShadow(
                        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
                        shadowColor = onSurfaceLight.copy(alpha = 0.3f)
                    ),
                text = stringResource(R.string.get_started),
                onClick = onGetStarted,
                containerColor = onSurfaceLight,
                contentColor = surfaceLight
            )
            LegalDoc(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimens.PaddingMedium),
                context = context
            )
        }
    }
}