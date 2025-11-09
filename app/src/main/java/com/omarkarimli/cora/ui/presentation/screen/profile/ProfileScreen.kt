package com.omarkarimli.cora.ui.presentation.screen.profile

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.MyFilledTextField
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.ConfirmSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.SheetContent
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.TextFieldConstants.MAX_FIELD_LENGTH_SMALL
import com.omarkarimli.cora.utils.TextFieldConstants.MIN_FIELD_LENGTH
import com.omarkarimli.cora.utils.showToast
import com.omarkarimli.cora.utils.validateFields
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val currentScreen = Screen.Profile.route
    val viewModel: ProfileViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val userModel by viewModel.userModel.collectAsState()

    var usernameInput by remember { mutableStateOf(
        ValidatableField(
            "",
            minLength = MIN_FIELD_LENGTH,
            maxLength = MAX_FIELD_LENGTH_SMALL,
            isSingleLine = true,
            allowGaps = false,
            allowSpecialChars = false,
            allowCapitalLetters = false,
            onlyEnglishLetters = true
        )
    ) }
    var bioInput by remember { mutableStateOf(ValidatableField("")) }

    var isFormDirty by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetContent by remember { mutableStateOf<SheetContent>(SheetContent.None) }

    val requiredFields = listOf(usernameInput)
    val areRequiredFieldsValid = requiredFields.validateFields()

    val onSave = {
        if (areRequiredFieldsValid) {
            userModel?.let { currentUserModel ->
                viewModel.updateUser(
                    currentUserModel.copy(
                        personalInfo = currentUserModel.personalInfo.copy(
                            username = usernameInput.value,
                            bio = bioInput.value
                        )
                    )
                )
            }
        }
    }

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
        LaunchedEffect(userModel) {
            userModel?.let {
                usernameInput = usernameInput.copy(value = it.personalInfo.username)
                bioInput = bioInput.copy(value = it.personalInfo.bio)
            }
        }

        LaunchedEffect(usernameInput, bioInput, userModel) {
            isFormDirty = userModel?.let { model ->
                usernameInput.value != model.personalInfo.username || bioInput.value != model.personalInfo.bio
            } ?: false
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
    }

    ObserveData()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopAppBar(
                navController = navController,
                onSave = onSave,
                btnEnabled = isFormDirty && areRequiredFieldsValid
            )
        }
    ) { innerPadding ->
        userModel?.let { model ->
            ScrollContent(
                innerPadding,
                model,
                usernameInput,
                bioInput,
                onUsernameChange = { usernameInput = it },
                onBioChange = { bioInput = it },
                onSignOut = { showSheet(SheetContent.Confirm) }
            )
        }

        if (sheetContent != SheetContent.None) {
            ModalBottomSheet(
                onDismissRequest = { hideSheet() },
                sheetState = sheetState
            ) {
                when (sheetContent) {
                    SheetContent.Confirm -> ConfirmSheetContent(
                        title = stringResource(R.string.sign_out),
                        description = stringResource(R.string.are_you_sure),
                        onConfirm = {
                            viewModel.signOut()
                            hideSheet()
                        }
                    )
                    else -> {}
                }
            }
        }

        if (uiState is UiState.Loading) LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(
    navController: NavController,
    onSave: () -> Unit,
    btnEnabled: Boolean = true
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = {
            Text(
                stringResource(Screen.Profile.titleResId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_prev)
                )
            }
        },
        actions = {
            if (btnEnabled) {
                FilledTonalIconButton(
                    onClick = onSave,
                    modifier = Modifier
                        .width(Dimens.IconSizeExtraLarge)
                        .height(Dimens.IconSizeLarge),
                    shape = IconButtonDefaults.filledShape
                ) {
                    Icon(
                        Icons.Rounded.Done,
                        contentDescription = stringResource(R.string.save),
                        modifier = Modifier.size(Dimens.IconSizeSmall),
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
    userModel: UserModel,
    usernameInput: ValidatableField,
    bioInput: ValidatableField,
    onUsernameChange: (ValidatableField) -> Unit,
    onBioChange: (ValidatableField) -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        ProfilePicture(profilePictureUrl = userModel.personalInfo.profilePictureUrl)
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        StaticInfo(userModel) // Assuming this is defined elsewhere
        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
        MyFilledTextField(
            label = stringResource(R.string.username),
            helper = stringResource(R.string.required),
            field = usernameInput,
            validationResult = usernameInput.validate(),
            onValueChange = { onUsernameChange(usernameInput.copy(value = it)) }
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        MyFilledTextField(
            label = stringResource(R.string.bio),
            helper = stringResource(R.string.optional),
            field = bioInput,
            validationResult = bioInput.validate(),
            onValueChange = { onBioChange(bioInput.copy(value = it)) }
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
        WideButton(
            text = stringResource(R.string.sign_out),
            onClick = onSignOut
        )
        Box(
            modifier = Modifier.height(
                innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge + Dimens.PaddingMedium
            )
        )
    }
}