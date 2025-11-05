package com.omarkarimli.cora.ui.presentation.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.StandardListItemUi
import com.omarkarimli.cora.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    val currentScreen = Screen.Admin.route
    val viewModel: AdminViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()

    val items = listOf(
        StandardListItemModel(
            id = 0,
            leadingIcon = Icons.Outlined.DataObject,
            title = stringResource(R.string.guidelines),
            onClick = { viewModel.setGuidelines() }
        ),
        StandardListItemModel(
            id = 1,
            leadingIcon = Icons.Outlined.DataObject,
            title = stringResource(R.string.subscriptions),
            onClick = { viewModel.setSubscriptions() }
        )
    )

    LaunchedEffect(uiState) {
        when (uiState) {
            UiState.Loading -> { /* Handle loading if needed */ }
            is UiState.Success -> {
                val successState = uiState as UiState.Success
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
                val log = (uiState as UiState.Error).log
                val toastResId = (uiState as UiState.Error).toastResId

                context.showToast(context.getString(toastResId))

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
            MyTopAppBar(scrollBehavior)
        }
    ) { innerPadding ->
        ScrollContent(innerPadding, items)

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    val isTopAppBarMinimized = scrollBehavior.state.collapsedFraction > 0.5
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = {
            Text(
                stringResource(Screen.Admin.titleResId),
                style = if (isTopAppBarMinimized) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
private fun ScrollContent(innerPadding: PaddingValues, items: List<StandardListItemModel>) {
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
        items(items) {
            StandardListItemUi(item = it)
        }
    }
}