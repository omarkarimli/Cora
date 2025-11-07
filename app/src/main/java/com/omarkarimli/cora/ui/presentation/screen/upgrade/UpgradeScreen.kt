package com.omarkarimli.cora.ui.presentation.screen.upgrade

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.History
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
import com.omarkarimli.cora.R // Import R class for string resources
import com.omarkarimli.cora.domain.models.CreditConditions
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.TabModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.EmptyWidget
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.TabRow
import com.omarkarimli.cora.ui.presentation.common.widget.component.TextWithBg
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeScreen() {
    val currentScreen = Screen.Upgrade.route
    val viewModel: UpgradeViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val uiState by viewModel.uiState.collectAsState()
    val creditConditions by viewModel.creditConditions.collectAsState()
    val userModel by viewModel.userModel.collectAsState()
    val subscriptionModels by viewModel.subscriptionModels.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

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
            is UiState.ActionRequired -> {}
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopAppBar(scrollBehavior, navController)
        }
    ) { innerPadding ->
        userModel?.let { user -> // Renamed for clarity
            ScrollContent(
                innerPadding = innerPadding,
                userModel = user,
                creditConditions = creditConditions,
                subscriptionModels = subscriptionModels,
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { newTab -> viewModel.getSubscriptionModels(newTab) },
                onSelectSubscription = { selectedSubscription ->
                    viewModel.onSelectSubscription(selectedSubscription)
                }
            )
        }

        if (uiState is UiState.Loading) {
            LoadingContent()
        } else if (tabs.isEmpty()) {
            EmptyWidget(
                text = stringResource(R.string.not_available)
            )
        }
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
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.SubscriptionHistory.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = stringResource(R.string.open_history)
                )
            }
        },
        title = {
            Text(
                stringResource(Screen.Upgrade.titleResId),
                style =
                    if (isTopAppBarMinimized) AppTypography.headlineSmall
                    else AppTypography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    userModel: UserModel,
    creditConditions: CreditConditions,
    subscriptionModels: List<SubscriptionModel>,
    tabs: List<TabModel>,
    selectedTab: TabModel?,
    onTabSelected: (TabModel) -> Unit,
    onSelectSubscription: (SubscriptionModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
        contentPadding = PaddingValues(
            start = Dimens.PaddingLarge,
            end = Dimens.PaddingLarge,
            top = innerPadding.calculateTopPadding() + Dimens.PaddingSmall,
            bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge + Dimens.PaddingMedium
        )
    ) {
        if (!creditConditions.isCreditActive) {
            item {
                TextWithBg(
                    text = stringResource(R.string.error_no_active_subscription),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        selectedTab?.let {
            item {
                TabRow(
                    tabs = tabs,
                    selectedTab = it,
                    onTabSelected = onTabSelected
                )
            }
        }
        items(subscriptionModels) { item ->
            val currentSubscriptionTitle = userModel.currentSubscription.title
            SubscriptionItem(
                subscriptionModel = item,
                isCurrent = (item.title == currentSubscriptionTitle),
                onClick = { onSelectSubscription(item) }
            )
        }
    }
}