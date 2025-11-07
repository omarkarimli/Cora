package com.omarkarimli.cora.ui.presentation.screen.splash

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.Durations
import com.omarkarimli.cora.utils.showToast
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun SplashScreen() {
    val currentScreen = Screen.Splash.route
    val viewModel: SplashViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is UiState.Success -> {
                if (currentState.canToast) {
                    context.showToast(currentState.message)
                }
                currentState.route?.let {
                    // Navigate and clear the back stack up to the splash screen
                    navController.navigate(it) {
                        popUpTo(currentScreen) { inclusive = true }
                    }
                }

                Log.d(currentScreen, "Success: ${'$'}{successState.message}")
                viewModel.resetUiState()
            }
            is UiState.Error -> {
                val log = currentState.log
                val toastResId = currentState.toastResId

                context.showToast(context.getString(toastResId))

                Log.e(currentScreen, log)
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    // 5. Compose UI Structure
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        MainContent(
            innerPadding
        )
    }
}

@Composable
fun MainContent(
    innerPadding: PaddingValues
) {
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(Durations.TEXT_CHANGE_DELAY)
            showText = !showText
            break
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(innerPadding)
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = showText,
            enter = fadeIn(animationSpec = tween(durationMillis = Durations.TEXT_FADE)),
            exit = fadeOut(animationSpec = tween(durationMillis = Durations.TEXT_FADE))
        ) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(Locale.ROOT),
                style = AppTypography.displaySmall.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = !showText,
            enter = fadeIn(animationSpec = tween(durationMillis = Durations.TEXT_FADE)),
            exit = fadeOut(animationSpec = tween(durationMillis = Durations.TEXT_FADE))
        ) {
            Image(
                modifier = Modifier.size(Dimens.IconSizeExtraLarge * 2),
                painter = painterResource(
                    if (isSystemInDarkTheme()) R.drawable.app_icon_night
                    else R.drawable.app_icon_light
                ),
                contentDescription = stringResource(id = R.string.app_name)
            )
        }
    }
}
