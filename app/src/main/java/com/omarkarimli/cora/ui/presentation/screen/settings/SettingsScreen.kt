package com.omarkarimli.cora.ui.presentation.screen.settings

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Rule
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.models.SettingsSection
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.StandardListItemUi
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.ConfirmSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.PermissionSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.ReportIssueSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.SheetContent
import com.omarkarimli.cora.ui.presentation.main.MainViewModel
import com.omarkarimli.cora.ui.presentation.screen.settings.sheet.DarkModeSheetContent
import com.omarkarimli.cora.ui.presentation.screen.settings.sheet.LangSheetContent
import com.omarkarimli.cora.ui.presentation.screen.settings.sheet.SavingPathSheetContent
import com.omarkarimli.cora.ui.presentation.screen.settings.sheet.ToggleSheetContent
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.PermissionManager
import com.omarkarimli.cora.utils.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(mainViewModel: MainViewModel) {
    val currentScreen = Screen.Settings.route
    val viewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val uiState by viewModel.uiState.collectAsState()
    val userModel by viewModel.userModel.collectAsState()
    val isLiveTranslationEnabled by viewModel.isLiveTranslationEnabled.collectAsState()
    val savingPath by viewModel.savingPath.collectAsState()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()
    val currentLang by mainViewModel.currentLang.collectAsState()
    val currentTheme by mainViewModel.currentTheme.collectAsStateWithLifecycle()
    val isDynamicColorEnabled by mainViewModel.isDynamicColorEnabled.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
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

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        viewModel.onNotificationsToggle(isGranted)
        if (isGranted) {
            hideSheet()
        } else {
            showSheet(SheetContent.Permission)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = PermissionManager.hasNotificationPermission(context)
            if (isNotificationsEnabled != hasPermission) {
                viewModel.onNotificationsToggle(hasPermission)
            }
        }
    }

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
                val errorState = uiState as UiState.Error
                val log = errorState.log
                val toastResId = errorState.toastResId

                context.showToast(context.getString(toastResId))

                Log.e(currentScreen, log)
                viewModel.resetUiState()
            }
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopAppBar(scrollBehavior, context, navController)
        }
    ) { innerPadding ->
        ScrollContent(
            innerPadding = innerPadding,
            navController = navController,
            onShowSheet = { content -> showSheet(content) }
        )

        if (sheetContent != SheetContent.None) {
            ModalBottomSheet(
                onDismissRequest = { hideSheet() },
                sheetState = sheetState
            ) {
                when (sheetContent) {
                    SheetContent.SavingPath -> SavingPathSheetContent(
                        savingPath = savingPath,
                        onToggle = { savingPath ->
                            viewModel.onSavingPathToggle(savingPath)
                            hideSheet()
                        }
                    )
                    SheetContent.Languages -> LangSheetContent(
                        currentLang = currentLang,
                        onLangChange = { newLang ->
                            viewModel.onLangChange(
                                onChange = {
                                    mainViewModel.onLangChange(newLang)
                                }
                            )
                            hideSheet()
                        }
                    )
                    SheetContent.LiveTranslation -> ToggleSheetContent(
                        titleStringId = R.string.live_translation,
                        descStringId = R.string.desc_live_translation,
                        enabled = isLiveTranslationEnabled,
                        onToggle = { isEnabled ->
                            viewModel.onLiveTranslationToggle(isEnabled)
                            hideSheet()
                        }
                    )
                    SheetContent.Notifications -> ToggleSheetContent(
                        enabled = isNotificationsEnabled,
                        onToggle = { isEnabled ->
                            if (isEnabled) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (PermissionManager.hasNotificationPermission(context)) {
                                        viewModel.onNotificationsToggle(true)
                                        hideSheet()
                                    } else {
                                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.onNotificationsToggle(true)
                                    hideSheet()
                                }
                            } else {
                                viewModel.onNotificationsToggle(false)
                                hideSheet()
                            }
                        }
                    )
                    SheetContent.DarkMode -> DarkModeSheetContent(
                        currentTheme = currentTheme,
                        onThemeChange = { newTheme ->
                            mainViewModel.onThemeChange(newTheme)
                            hideSheet()
                        }
                    )
                    SheetContent.DynamicColor -> ToggleSheetContent(
                        titleStringId = R.string.dynamic_color,
                        descStringId = R.string.desc_dynamic_color,
                        enabled = isDynamicColorEnabled,
                        onToggle = { isEnabled ->
                            mainViewModel.onDynamicColorToggle(isEnabled)
                            hideSheet()
                        }
                    )
                    SheetContent.ResetSettings -> ConfirmSheetContent(
                        title = stringResource(R.string.reset_settings),
                        description = stringResource(R.string.are_you_sure),
                        onConfirm = {
                            viewModel.onResetSettings(mainViewModel)
                            hideSheet()
                        }
                    )
                    SheetContent.ReportIssue -> ReportIssueSheetContent(
                        onConfirm = { description ->
                            userModel?.let {
                                viewModel.onReportIssue(
                                    ReportIssueModel(
                                        idToken = it.idToken,
                                        personalInfo = it.personalInfo,
                                        description = description
                                    )
                                )
                                hideSheet()
                            } ?: run {
                                viewModel.setError(
                                    toastResId = R.string.error_something_went_wrong,
                                    log = "Profile not found when trying to report an issue."
                                )
                            }
                        }
                    )
                    SheetContent.Permission -> PermissionSheetContent(
                        onHide = { hideSheet() }
                    )
                    else -> {}
                }
            }
        }

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    navController: NavController,
    onShowSheet: (SheetContent) -> Unit
) {
    val sections = listOf(
        SettingsSection(
            title = stringResource(R.string.usage) + " & " + stringResource(R.string.account),
            items = listOf(
                StandardListItemModel(
                    id = 0,
                    leadingIcon = Icons.Outlined.Subscriptions,
                    title = stringResource(R.string.subscriptions),
                    description = stringResource(R.string.desc_subscriptions),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { navController.navigate(Screen.Upgrade.route) }
                ),
                StandardListItemModel(
                    id = 1,
                    leadingIcon = Icons.Outlined.DataUsage,
                    title = stringResource(R.string.usage),
                    description = stringResource(R.string.desc_usage),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { navController.navigate(Screen.Usage.route) }
                ),
                StandardListItemModel(
                    id = 2,
                    leadingIcon = Icons.AutoMirrored.Outlined.Rule,
                    title = stringResource(R.string.guidelines),
                    description = stringResource(R.string.desc_guidelines),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { navController.navigate(Screen.Guidelines.route) }
                )
            )
        ),
        SettingsSection(
            title = stringResource(R.string.general),
            items = listOf(
                StandardListItemModel(
                    id = 3,
                    leadingIcon = Icons.Outlined.SaveAlt,
                    title = stringResource(R.string.saving_path),
                    description = stringResource(R.string.desc_saving_path),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.SavingPath) }
                ),
                StandardListItemModel(
                    id = 4,
                    leadingIcon = Icons.Outlined.Notifications,
                    title = stringResource(R.string.notifications),
                    description = stringResource(R.string.desc_notifications),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.Notifications) }
                ),
                StandardListItemModel(
                    id = 5,
                    leadingIcon = Icons.Outlined.Language,
                    title = stringResource(R.string.languages),
                    description = stringResource(R.string.desc_lang),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.Languages) }
                ),
                StandardListItemModel(
                    id = 6,
                    leadingIcon = Icons.Outlined.Translate,
                    title = stringResource(R.string.live_translation),
                    description = stringResource(R.string.desc_live_translation),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.LiveTranslation) }
                ),
                StandardListItemModel(
                    id = 7,
                    leadingIcon = Icons.Outlined.DarkMode,
                    title = stringResource(R.string.dark_mode),
                    description = stringResource(R.string.desc_dark_mode),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.DarkMode) }
                ),
                StandardListItemModel(
                    id = 8,
                    leadingIcon = Icons.Outlined.InvertColors,
                    title = stringResource(R.string.dynamic_color),
                    description = stringResource(R.string.desc_dynamic_color),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.DynamicColor) }
                )
            )
        ),
        SettingsSection(
            title = stringResource(R.string.support) + " & " + stringResource(R.string.about),
            items = listOf(
                StandardListItemModel(
                    id = 9,
                    leadingIcon = Icons.Outlined.BugReport,
                    title = stringResource(R.string.report_issue),
                    description = stringResource(R.string.desc_report_issue),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.ReportIssue) }
                ),
                StandardListItemModel(
                    id = 10,
                    leadingIcon = Icons.Outlined.SettingsBackupRestore,
                    title = stringResource(R.string.reset_settings),
                    description = stringResource(R.string.desc_reset_settings),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { onShowSheet(SheetContent.ResetSettings) }
                ),
                StandardListItemModel(
                    id = 11,
                    leadingIcon = Icons.Outlined.Info,
                    title = stringResource(R.string.about),
                    description = stringResource(R.string.desc_about),
                    endingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = { navController.navigate(Screen.About.route) }
                )
            )
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingMedium
        )
    ) {
        sections.forEach { section ->
            item {
                Text(
                    modifier = Modifier.padding(
                        top = Dimens.PaddingMedium,
                        bottom = Dimens.PaddingSmall
                    ),
                    text = section.title,
                    style = AppTypography.titleMedium
                )
            }

            // Add the list items in the section
            items(section.items) { item ->
                StandardListItemUi(item = item)
            }
        }
    }
}