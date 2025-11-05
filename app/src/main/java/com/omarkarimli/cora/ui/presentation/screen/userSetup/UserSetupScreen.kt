package com.omarkarimli.cora.ui.presentation.screen.userSetup

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.MyFilledTextField
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.presentation.screen.profile.ProfilePicture
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.Constants.MAX_FIELD_LENGTH_SMALL
import com.omarkarimli.cora.utils.Constants.MIN_FIELD_LENGTH
import com.omarkarimli.cora.utils.sendEmail
import com.omarkarimli.cora.utils.showToast
import com.omarkarimli.cora.utils.validateFields
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSetupScreen(userModel: UserModel) {
    val currentScreen = Screen.UserSetup.route
    val viewModel: UserSetupViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()

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
    var genderInput by remember { mutableStateOf(ValidatableField("", isTextWidget = false)) }

    val requiredFields = listOf(usernameInput, genderInput)

    val areRequiredFieldsValid = requiredFields.validateFields()
    val isButtonEnabled = subscriptions.isNotEmpty() && areRequiredFieldsValid

    val onContinue = {
        if (isButtonEnabled) {
            viewModel.onContinue(
                userModel.copy(
                    personalInfo = userModel.personalInfo.copy(
                        username = usernameInput.value,
                        bio = bioInput.value,
                        gender = genderInput.value
                    ),
                    subscriptions = subscriptions,
                    usageData = UsageDataModel(
                        webSearchResultCount = subscriptions.firstOrNull()?.maxUsageData?.webSearchResultCount ?: 1
                    )
                )
            )
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
            is UiState.ActionRequired -> {}
            UiState.Idle -> { /* Hide any loading indicators */ }
        }
    }

    Scaffold(
        topBar = { MyTopAppBar(context) }
    ) { innerPadding ->
        ScrollContent(
            innerPadding = innerPadding,
            userModel = userModel,
            usernameInput = usernameInput,
            onUsernameChange = { usernameInput = it },
            bioInput = bioInput,
            onBioChange = { bioInput = it },
            genderInput = genderInput,
            onGenderChange = { genderInput = it },
            onContinue = onContinue,
            btnEnabled = isButtonEnabled
        )

        if (uiState == UiState.Loading) LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(context: Context) {
    val appName = stringResource(id = R.string.app_name)

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = appName.uppercase(Locale.ROOT),
                style = AppTypography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
        },
        actions = {
            IconButton(
                onClick = { context.sendEmail(appName) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = stringResource(R.string.help),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues,
    userModel: UserModel,
    usernameInput: ValidatableField,
    onUsernameChange: (ValidatableField) -> Unit,
    bioInput: ValidatableField,
    onBioChange: (ValidatableField) -> Unit,
    genderInput: ValidatableField,
    onGenderChange: (ValidatableField) -> Unit,
    onContinue: () -> Unit,
    btnEnabled: Boolean = false
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
            .padding(horizontal = Dimens.PaddingMedium)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)
    ) {
        ProfilePicture(profilePictureUrl = userModel.personalInfo.profilePictureUrl)
        MyFilledTextField(
            label = stringResource(R.string.username),
            helper = stringResource(R.string.required),
            field = usernameInput,
            validationResult = usernameInput.validate(),
            onValueChange = { onUsernameChange(usernameInput.copy(value = it)) }
        )
        GenderSelectionChipGroup(
            field = genderInput,
            validationResult = genderInput.validate(),
            onGenderSelected = { onGenderChange(genderInput.copy(value = it)) }
        )
        MyFilledTextField(
            label = stringResource(R.string.bio),
            helper = stringResource(R.string.optional),
            field = bioInput,
            validationResult = bioInput.validate(),
            onValueChange = { onBioChange(bioInput.copy(value = it)) }
        )
        WideButton(
            modifier = Modifier.padding(vertical = Dimens.PaddingSmall),
            text = stringResource(R.string.continue_title),
            onClick = onContinue,
            enabled = btnEnabled
        )
        Spacer(
            modifier = Modifier.height(
                innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge + Dimens.PaddingMedium
            )
        )
    }
}