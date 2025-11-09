package com.omarkarimli.cora.ui.presentation.screen.subscriptionhistory

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.StandardListItemUi
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.showToast
import com.omarkarimli.cora.utils.toStandardListItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionHistoryScreen() {
    val currentScreen = Screen.SubscriptionHistory.route
    val viewModel: SubscriptionHistoryViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()

    val subscriptionModels by viewModel.subscriptionModels.collectAsState()

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            UiState.Loading -> { /* Handle loading if needed */ }
            is UiState.Success -> {
                if (currentState.canToast) {
                    context.showToast(currentState.message) // Ensure message is a String
                }
                currentState.route?.let {
                    navController.navigate(it) {
                        popUpTo(currentScreen) { inclusive = true }
                    }
                }
                Log.d(currentScreen, "Success: ${currentState.message}")
                viewModel.resetUiState()
            }
            is UiState.Error -> {
                val log = currentState.log
                val toastResId = currentState.toastResId
                context.showToast(context.getString(toastResId)) // Correctly get string from resource ID
                Log.e(currentScreen, log)
                viewModel.resetUiState()
            }
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopAppBar(scrollBehavior, navController)
        }
    ) { innerPadding ->
        ScrollContent(
            innerPadding,
            subscriptionModels
        )

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController
) {
    val isTopAppBarMinimized = scrollBehavior.state.collapsedFraction > 0.5
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_to_prev))
            }
        },
        title = {
            Text(
                stringResource(Screen.SubscriptionHistory.titleResId),
                style = if (isTopAppBarMinimized) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    subscriptionModels: List<SubscriptionModel>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        contentPadding = PaddingValues(
            start = Dimens.PaddingLarge,
            end = Dimens.PaddingLarge,
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge + Dimens.PaddingMedium
        )
    ) {
        itemsIndexed(subscriptionModels) { index, item ->
            StandardListItemUi(
                item = item.toStandardListItemModel(index)
            )
        }
    }
}