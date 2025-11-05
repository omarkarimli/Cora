package com.omarkarimli.cora.ui.presentation.screen.fullscreenImageViewer

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImageViewerScreen(
    imageModels: List<ImageModel>,
    initialPage: Int = 0
) {
    val currentScreen = Screen.FullScreenImageViewer.route
    val viewModel: FullScreenImageViewerViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { imageModels.size }
    )

    LaunchedEffect(uiState) {
        when (val currentState = uiState) { // Renamed for clarity
            UiState.Loading -> { /* Handle loading if needed */ }
            is UiState.Success -> {
                val successState = currentState // Use renamed variable
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
                val errorState = currentState // Use renamed variable
                val log = errorState.log
                val toastResId = errorState.toastResId // This should be an Int

                context.showToast(context.getString(toastResId)) // Correctly get string from resource ID

                Log.e(currentScreen, log)
                viewModel.resetUiState()
            }
            is UiState.ActionRequired -> {}
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MyTopAppBar(
                context,
                navController,
                viewModel,
                pagerState,
                imageModels
            )
        },
        bottomBar = {
            MyBottomBar(
                context = context,
                sourceText = imageModels[pagerState.currentPage].sourceUrl
            )
        }
    ) { innerPadding ->
        ClothImagePager(
            innerPadding = innerPadding,
            pagerState = pagerState,
            imageModels = imageModels,
        )

        if (uiState is UiState.Loading) LoadingContent()
    }
}