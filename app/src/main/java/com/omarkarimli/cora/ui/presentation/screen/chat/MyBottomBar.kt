package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.CreditConditions
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.domain.models.ValidationResult
import com.omarkarimli.cora.ui.presentation.common.widget.component.MyFilledTextField
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.performHaptic

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyBottomBar(
    messageModel: MessageModel,
    creditConditions: CreditConditions,
    expanded: Boolean,
    isLoading: Boolean,
    isSelecting: Boolean,
    images: SnapshotStateList<ImageModel>,
    onDisableSelecting: () -> Unit,
    onToggleImageGeneration: () -> Unit,
    onSend: (MessageModel) -> Unit,
    onDismissDropDown: () -> Unit,
    onTextChange: (String) -> Unit,
    onAttach: () -> Unit,
    onRemoveAttach: (ImageModel) -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchImagePicker: () -> Unit,
    onPickVoux: () -> Unit
) {
    val sendEnable = !isLoading
            && messageModel.text.isNotBlank()
            && creditConditions.messageChars

    val topOptions = listOf(
        StandardListItemModel(
            id = 0,
            title = stringResource(R.string.pick_voux),
            leadingIcon = Icons.Rounded.Checkroom,
            onClick = onPickVoux
        ),
        StandardListItemModel(
            id = 1,
            title = stringResource(R.string.camera),
            leadingIcon = Icons.Outlined.CameraAlt,
            onClick = onLaunchCamera
        ),
        StandardListItemModel(
            id = 2,
            title = stringResource(R.string.photos),
            leadingIcon = Icons.Outlined.Image,
            onClick = onLaunchImagePicker
        )
    )

    val bottomOptions = listOf(
        StandardListItemModel(
            id = 0,
            title = stringResource(R.string.image_generation),
            leadingIcon = Icons.Outlined.AddPhotoAlternate,
            endingIcon = if (messageModel.imageGeneration) Icons.Rounded.Done else null,
            onClick = onToggleImageGeneration
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium)
            .animateContentSize(),
    ) {
        AnimatedVisibility(
            visible = !isSelecting,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens.PaddingMedium),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismissDropDown,
                    shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
                ) {
                    topOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { item.title?.let { Text(it) } },
                            leadingIcon = {
                                item.leadingIcon?.let {
                                    Icon(it, contentDescription = item.title)
                                }
                            },
                            onClick = {
                                item.onClick()
                                onDismissDropDown()
                            }.performHaptic()
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.PaddingSmall)
                    )
                    bottomOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { item.title?.let { Text(it) } },
                            leadingIcon = {
                                item.leadingIcon?.let {
                                    Icon(it, contentDescription = item.title)
                                }
                            },
                            trailingIcon = {
                                item.endingIcon?.let {
                                    Icon(it, contentDescription = item.title)
                                }
                            },
                            onClick = {
                                item.onClick()
                                onDismissDropDown()
                            }.performHaptic()
                        )
                    }
                }

                FilledTonalIconButton(
                    modifier = Modifier.padding(bottom = Dimens.PaddingExtraSmall),
                    onClick = onAttach.performHaptic(),
                    shape = MaterialShapes.Pill.toShape()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.attach)
                    )
                }
                Spacer(Modifier.width(Dimens.PaddingExtraSmall))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    AttachWidget(images, onRemove = onRemoveAttach)
                    TagContents(
                        items = bottomOptions.filter { it.endingIcon != null }
                    )
                    MyFilledTextField(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        label = null,
                        helper = stringResource(R.string.ask_voux),
                        field = ValidatableField(messageModel.text),
                        validationResult = ValidationResult(true),
                        onValueChange = onTextChange
                    )
                }
                Spacer(Modifier.width(Dimens.PaddingSmall))
                FilledTonalIconButton(
                    modifier = Modifier.padding(bottom = Dimens.PaddingExtraSmall),
                    enabled = sendEnable,
                    onClick = {
                        val newMessageModel = messageModel.copy(images = images.toList())
                        onSend(newMessageModel)
                    }.performHaptic(),
                    shape = MaterialShapes.Clover4Leaf.toShape(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    if (isLoading) {
                        LoadingIndicator()
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = stringResource(R.string.send)
                        )
                    }
                }
            }
        }
        SelectionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            isSelecting = isSelecting,
            onToggle = onDisableSelecting
        )
    }
}