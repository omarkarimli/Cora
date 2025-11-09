@file:OptIn(ExperimentalMaterial3Api::class)
package com.omarkarimli.cora.ui.presentation.screen.chatHistory

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.domain.models.ValidationResult
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.EmptyWidget
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.MyFilledTextField
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.ConfirmSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.DeleteChatHistoryItemSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.SheetContent
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.showToast
import kotlinx.coroutines.launch

@Composable
fun ChatHistoryScreen() {
    val currentScreen = Screen.ChatHistory.route
    val viewModel: ChatHistoryViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val pagingItems = viewModel.paginatedItems.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetContent by remember { mutableStateOf<SheetContent>(SheetContent.None) }

    fun showSheet(content: SheetContent) {
        sheetContent = content
        coroutineScope.launch { sheetState.show() }
    }

    fun hideSheet() {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                sheetContent = SheetContent.None
            }
        }
    }

    @Composable
    fun ObserveData() {
        LaunchedEffect(uiState) {
            when (val currentState = uiState) {
                UiState.Loading -> { /* Handle loading if needed */ }
                is UiState.Success -> {
                    if (currentState.canToast) {
                        context.showToast(currentState.message)
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopAppBar(
                scrollBehavior = scrollBehavior,
                navController = navController,
                onClearAllClicked = { showSheet(SheetContent.Confirm) },
                enabled = pagingItems.itemCount > 0
            )
        }
    ) { innerPadding ->
        ScrollContent(
            innerPadding = innerPadding,
            navController = navController,
            viewModel = viewModel,
            paginatedItems = pagingItems,
            searchQuery = searchQuery,
            showItemDeleteSheet = { showSheet(SheetContent.DeleteChatHistoryItem(it)) },
        )

        if (sheetContent != SheetContent.None) {
            ModalBottomSheet(
                onDismissRequest = { hideSheet() },
                sheetState = sheetState
            ) {
                when (val content = sheetContent) {
                    is SheetContent.Confirm -> ConfirmSheetContent(
                        title = stringResource(R.string.clear_all),
                        description = stringResource(R.string.are_you_sure),
                        onConfirm = {
                            viewModel.clearAll()
                            hideSheet()
                        }
                    )
                    is SheetContent.DeleteChatHistoryItem -> DeleteChatHistoryItemSheetContent(
                        title = stringResource(R.string.delete_item),
                        description = stringResource(R.string.are_you_sure),
                        onConfirm = {
                            viewModel.deleteItem(content.item)
                            hideSheet()
                        },
                        item = content.item
                    )
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
    onClearAllClicked: () -> Unit,
    enabled: Boolean
) {
    val isTopAppBarMinimized = scrollBehavior.state.collapsedFraction > 0.5

    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_to_prev))
            }
        },
        title = {
            Text(
                stringResource(Screen.ChatHistory.titleResId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = if (isTopAppBarMinimized) AppTypography.headlineSmall else AppTypography.headlineMedium
            )
        },
        actions = {
            if (enabled) {
                FilledTonalIconButton(
                    onClick = onClearAllClicked,
                    modifier = Modifier
                        .width(Dimens.IconSizeExtraLarge)
                        .height(Dimens.IconSizeLarge),
                    shape = IconButtonDefaults.filledShape
                ) {
                    Icon(
                        Icons.Rounded.ClearAll,
                        modifier = Modifier.size(Dimens.IconSizeSmall),
                        contentDescription = stringResource(R.string.clear_all)
                    )
                }
                Spacer(Modifier.size(Dimens.PaddingSmall))
            }
        }
    )
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: ChatHistoryViewModel,
    paginatedItems: LazyPagingItems<ChatHistoryItemModel>,
    searchQuery: String,
    showItemDeleteSheet: (ChatHistoryItemModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = innerPadding.calculateTopPadding() - Dimens.PaddingMedium)
    ) {
        Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))
        MyFilledTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingMedium),
            shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
            leadingIcon = Icons.Outlined.Search,
            label = null,
            helper = stringResource(R.string.search),
            field = ValidatableField(searchQuery),
            validationResult = ValidationResult(true),
            onValueChange = { newQuery -> viewModel.setSearchQuery(newQuery) }
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        when {
            paginatedItems.loadState.refresh is LoadState.Loading -> LoadingContent()
            paginatedItems.itemCount == 0 && paginatedItems.loadState.refresh is LoadState.NotLoading -> {
                EmptyWidget(
                    imageVector = Icons.Outlined.Search,
                    text = stringResource(R.string.no_item_found)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = Dimens.PaddingMedium,
                        end = Dimens.PaddingMedium,
                        top = Dimens.PaddingSmall,
                        bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge
                    )
                ) {
                    items(
                        count = paginatedItems.itemCount
                    ) { index ->
                        paginatedItems[index]?.let {
                            ChatHistoryItem(
                                item = it,
                                onClick = {
                                    val id = it.id
                                    navController.navigate("${Screen.Chat.route}?chatHistoryId=$id")
                                },
                                onLongClick = {
                                    showItemDeleteSheet(it)
                                }
                            )
                        }
                    }

                    paginatedItems.apply {
                        if (loadState.append is LoadState.Loading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    ContainedLoadingIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                        if (loadState.append is LoadState.Error) {
                            item {
                                val error = (loadState.append as LoadState.Error).error
                                Log.e("ChatHistoryScreen", "Error: $error")
                                EmptyWidget(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    text = stringResource(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}