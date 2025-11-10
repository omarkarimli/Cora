package com.omarkarimli.cora.ui.presentation.screen.chat

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import com.omarkarimli.cora.ui.presentation.common.widget.component.EmptyWidget
import com.omarkarimli.cora.ui.presentation.common.widget.component.LoadingContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.PermissionSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.ReportIssueSheetContent
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.SheetContent
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.PermissionManager
import com.omarkarimli.cora.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatHistoryId: Int? = null,
    initialShare: String? = null
) {
    val currentScreen = Screen.Chat.route
    val viewModel: ChatViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val userModel by viewModel.userModel.collectAsState()
    val creditConditions by viewModel.creditConditions.collectAsState()
    val messages by viewModel.messages.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var sendMessageModel by remember { mutableStateOf(MessageModel(text = "", isFromMe = true)) }

    // For Camera Temporary
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val images = remember { mutableStateListOf<ImageModel>() }

    val sheetState = rememberModalBottomSheetState()
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

    fun onPickImage(imageModel: ImageModel) {
        if (images.contains(imageModel)) {
            viewModel.setError(R.string.error_image_already_added, "Image already added.")
        } else {
            images.add(imageModel)
        }
        hideSheet()
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUri ->
            val takeFlag: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION

            try {
                context.contentResolver.takePersistableUriPermission(imageUri, takeFlag)
            } catch (e: SecurityException) {
                viewModel.setError(
                    R.string.error_failed_persistent_access,
                    e.message ?: "Failed to get persistent access to the image."
                )
                return@let
            }

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    withContext(Dispatchers.Main) {
                        onPickImage(
                            ImageModel(
                                imageUrl = imageUri.toString(),
                                sourceUrl = imageUri.toString()
                            )
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        viewModel.setError(
                            R.string.error_load_image,
                            e.message ?: "Failed to load image."
                        )
                    }
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && tempImageUri != null) {
            tempImageUri?.let { onPickImage(
                ImageModel(
                    imageUrl = it.toString(),
                    sourceUrl = it.toString()
                )
            ) }
        } else {
            viewModel.setError(R.string.error_take_photo, "Failed to take a photo.")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (!isGranted) {
            showSheet(SheetContent.Permission)
        }
    }

    fun onAttach() {
        if (creditConditions.attaches) {
            if (images.size < 3) {
                expanded = !expanded
            } else {
                viewModel.setError(R.string.error_max_images_reached, "You can only add up to 3 images.")
            }
        } else {
            viewModel.setError(R.string.error_attach_limit_reached, "You have reached your attach limit.")
        }
    }

    fun onTextChange(text: String) {
        // Correctly updates the state, triggering recomposition
        sendMessageModel = sendMessageModel.copy(text = text, images = sendMessageModel.images, isFromMe = true)
    }

    fun onLaunchCamera() {
        if (PermissionManager.hasCameraPermission(context)) {
            val file = File(context.cacheDir, "temp_image.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun onLaunchImagePicker() {
        if (PermissionManager.hasStoragePermission(context)) {
            pickImageLauncher.launch("image/*")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    fun handleInitialShare(initialShare: String?) {
        if (initialShare != null) {
            sendMessageModel = sendMessageModel.copy(text = "Give me details about this link: $initialShare")
        }
    }

    @Composable
    fun ObserveData() {
        LifecycleEventEffect(Lifecycle.Event.ON_START) {
            viewModel.loadSettings()
            if (userModel == null) {
                viewModel.getUser()
            }
            handleInitialShare(initialShare)
        }
        LaunchedEffect(key1 = true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!PermissionManager.hasNotificationPermission(context)) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        LaunchedEffect(chatHistoryId) {
            chatHistoryId?.let {
                viewModel.getHistory(it)
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
                is UiState.Idle -> {}
            }
        }
    }

    ObserveData()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            MyTopAppBar(
                navController = navController,
                onShowSheet = { content -> showSheet(content) },
                onNewChat = { viewModel.onNewChat() }
            )
        },
        bottomBar = {
            MyBottomBar(
                messageModel = sendMessageModel,
                creditConditions = creditConditions,
                expanded = expanded,
                isLoading = uiState is UiState.Loading,
                attachEnable = !(sendMessageModel.imageGeneration || sendMessageModel.webSearch),
                images = images,
                onToggleImageGeneration = {
                    sendMessageModel = sendMessageModel.copy(imageGeneration = !sendMessageModel.imageGeneration)
                },
                onToggleWebSearch = {
                    sendMessageModel = sendMessageModel.copy(webSearch = !sendMessageModel.webSearch)
                },
                onSend = { messageModel ->
                    viewModel.onSend(messageModel)
                    images.clear()
                    onTextChange("")
                },
                onDismissDropDown = { expanded = false },
                onTextChange = { onTextChange(it) },
                onAttach = { onAttach() },
                onRemoveAttach = { images.remove(it) },
                onLaunchCamera = { onLaunchCamera() },
                onLaunchImagePicker = { onLaunchImagePicker() }
            )
        }
    ) { innerPadding ->
        if (messages.isEmpty() && uiState !is UiState.Loading) {
            EmptyWidget(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                text = stringResource(R.string.ask_cora)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding()
                    ),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingLarge * 2
                )
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
            }
        }

        if (sheetContent != SheetContent.None) {
            ModalBottomSheet(
                onDismissRequest = { hideSheet() },
                sheetState = sheetState
            ) {
                when (sheetContent) {
                    is SheetContent.ReportIssue -> ReportIssueSheetContent(
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
                                    R.string.error_something_went_wrong,
                                    "Profile not found when reporting issue."
                                )
                            }
                        }
                    )
                    is SheetContent.Permission -> PermissionSheetContent(
                        onHide = { hideSheet() }
                    )
                    else -> {}
                }
            }
        }

        if (uiState is UiState.Loading) LoadingContent()
    }
}
