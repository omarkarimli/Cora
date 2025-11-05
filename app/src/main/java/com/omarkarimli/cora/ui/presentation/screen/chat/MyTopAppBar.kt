package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.widget.sheet.SheetContent
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun MyTopAppBar(
    navController: NavController,
    isSelecting: Boolean,
    onShowSheet: (SheetContent) -> Unit,
    onFabEnabling: () -> Unit,
    onNewChat: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                stringResource(Screen.Chat.titleResId),
                style = AppTypography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            if (!isSelecting) {
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
        },
        actions = {
            if (!isSelecting) {
                var expanded by remember { mutableStateOf(false) }

                val options = listOf(
                    StandardListItemModel(
                        id = 0,
                        title = stringResource(R.string.new_chat),
                        leadingIcon = Icons.Outlined.AddCircleOutline,
                        onClick = onNewChat
                    ),
                    StandardListItemModel(
                        id = 1,
                        title = stringResource(R.string.history),
                        leadingIcon = Icons.Outlined.History,
                        onClick = { navController.navigate(Screen.ChatHistory.route) }
                    ),
                    StandardListItemModel(
                        id = 2,
                        title = stringResource(R.string.report_issue),
                        leadingIcon = Icons.Outlined.BugReport,
                        onClick = { onShowSheet(SheetContent.ReportIssue) }
                    ),
                    StandardListItemModel(
                        id = 3,
                        title = stringResource(R.string.enable_selecting),
                        leadingIcon = Icons.Rounded.AutoAwesome,
                        onClick = onFabEnabling
                    )
                )

                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.more)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
                ) {
                    options.forEach { item ->
                        DropdownMenuItem(
                            text = { item.title?.let { Text(it) } },
                            leadingIcon = { item.leadingIcon?.let { Icon(it, contentDescription = item.title) } }, // Added contentDescription
                            onClick = {
                                item.onClick()
                                expanded = false
                            }.performHaptic()
                        )
                    }
                }
            }
        }
    )
}