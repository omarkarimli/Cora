@file:OptIn(ExperimentalMaterial3Api::class)

package com.omarkarimli.cora.ui.presentation.screen.usage

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.TopInfo
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageScreen() {
    val currentScreen = Screen.Usage.route
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val viewModel: UsageViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val userModel by viewModel.userModel.collectAsStateWithLifecycle()

    @Composable
    fun ObserveData() {
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.getUser()
        }

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
                    context.showToast(context.getString(toastResId))
                    Log.e(currentScreen, log)
                    viewModel.resetUiState()
                }
                UiState.Idle -> { /* Hide any loading indicators */ }
            }
        }
    }

    ObserveData()
    Scaffold(
        topBar = {
            MyTopAppBar(
                scrollBehavior = scrollBehavior,
                navController = navController,
                currentSubscriptionModel = userModel?.currentSubscription
            )
        }
    ) { innerPadding ->
        ScrollContent(
            innerPadding = innerPadding,
            context = context,
            viewModel = viewModel,
            uiState = uiState,
            userModel = userModel
        )

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    context: Context,
    viewModel: UsageViewModel,
    uiState: UiState,
    userModel: UserModel?
) {
    val layoutDirection = LocalLayoutDirection.current
    val currentSubscriptionModel = userModel?.currentSubscription
    val currentUsageData = userModel?.usageData

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection)
            ),
        isRefreshing = uiState is UiState.Loading,
        onRefresh = { viewModel.getUser() }
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
            contentPadding = PaddingValues(
                bottom = Dimens.PaddingExtraLarge * 4
            ),
        ) {
            if (currentSubscriptionModel != null && currentUsageData != null) {
                val items = listOf(
                    hashMapOf(
                        StandardListItemModel(
                            id = 0,
                            title = context.getString(R.string.attaches),
                            description = context.getString(R.string.usage_item_attaches_desc),
                            leadingIcon = Icons.Rounded.Add,
                            endingText = "${currentUsageData.attaches}/${currentSubscriptionModel.maxUsageData.attaches}"
                        ) to (currentUsageData.attaches.toFloat() / currentSubscriptionModel.maxUsageData.attaches.toFloat())
                    ),
                    hashMapOf(
                        StandardListItemModel(
                            id = 1,
                            title = context.getString(R.string.usage_item_message_chars_title),
                            description = context.getString(R.string.usage_item_message_chars_desc),
                            leadingIcon = Icons.Outlined.Bolt,
                            endingText = "${currentUsageData.messageChars}/${currentSubscriptionModel.maxUsageData.messageChars}"
                        ) to (currentUsageData.messageChars.toFloat() / currentSubscriptionModel.maxUsageData.messageChars.toFloat())
                    ),
                    hashMapOf(
                        StandardListItemModel(
                            id = 2,
                            title = context.getString(R.string.ads_enabled),
                            leadingIcon = Icons.Outlined.BubbleChart,
                            endingIcon = if (currentSubscriptionModel.adsEnabled) Icons.Outlined.Done else Icons.Outlined.Block
                        ) to (currentUsageData.messageChars.toFloat() / currentSubscriptionModel.maxUsageData.messageChars.toFloat())
                    )
                )

                items(items) {
                    UsageDataRow(item = it)
                }
            } else if (uiState !is UiState.Loading) {
                item {
                    TopInfo()
                }
            }
        }
    }
}