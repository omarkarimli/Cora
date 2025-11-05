package com.omarkarimli.cora.ui.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.omarkarimli.cora.ui.navigation.AppNavigation
import com.omarkarimli.cora.ui.theme.AppTheme
import com.omarkarimli.cora.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var initialShare: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Check if the app was launched with a sharing intent
        if (intent?.action == Intent.ACTION_SEND && intent.type == Constants.MIME_TYPE_TEXT) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            sharedText?.let { initialShare = it }
        }

        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val currentTheme by mainViewModel.currentTheme.collectAsState()
            val isDynamicColorEnabled by mainViewModel.isDynamicColorEnabled.collectAsState()
            val currentLang by mainViewModel.currentLang.collectAsState()

            fun loadInitialLanguage() {
                val currentLanguage = mainViewModel.langRepository.getLanguageCode()
                mainViewModel.setLang(currentLanguage)
            }

            LifecycleEventEffect(Lifecycle.Event.ON_START) {
                loadInitialLanguage()
            }

            LaunchedEffect(currentLang) {
                mainViewModel.langRepository.changeLanguage(currentLang)
            }

            AppTheme(
                dynamicColor = isDynamicColorEnabled,
                darkTheme = when (currentTheme) {
                    AppTheme.Dark -> true
                    AppTheme.Light -> false
                    AppTheme.System -> isSystemInDarkTheme()
                }
            ) {
                AppNavigation(mainViewModel, initialShare)
            }
        }
    }
}