package com.omarkarimli.cora.ui.presentation.screen.success

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.MyLottieAnimation
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen() {
    val currentScreen = Screen.Success.route
    val viewModel: SuccessViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val currentState = uiState) { // Renamed for clarity
            UiState.Loading -> { /* Handle loading if needed */ }
            is UiState.Success -> {
                val successState = currentState // Use renamed variable
                if (successState.canToast) {
                    context.showToast(successState.message) // Ensure message is a String
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
                val errorState = currentState // Use renamed variable
                val log = errorState.log
                val toastResId = errorState.toastResId // This should now correctly be an Int

                context.showToast(context.getString(toastResId)) // Correctly get string from resource ID

                Log.e(currentScreen, log)
                viewModel.resetUiState()
            }
            is UiState.ActionRequired -> {}
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold(
        bottomBar = {
            WideButton(
                modifier = Modifier
                    .padding(
                        start = Dimens.PaddingLarge,
                        end = Dimens.PaddingLarge,
                        bottom = Dimens.PaddingMedium
                    ),
                onClick = { viewModel.onContinue(Screen.Chat.route) }
            )
        }
    ) { innerPadding ->
        ScrollContent(innerPadding)

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge + Dimens.PaddingMedium
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            MyLottieAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                resId = R.raw.success
            )
        }
        item {
            Text(
                text = stringResource(R.string.success),
                style = AppTypography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Text(
                text = stringResource(R.string.success_message),
                style = AppTypography.bodyMedium
            )
        }
    }
}