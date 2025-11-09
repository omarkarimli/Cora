package com.omarkarimli.cora.ui.presentation.common.widget.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R
import com.omarkarimli.cora.utils.openAppSettings

@Composable
fun PermissionAlertDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.permission_denied)) },
        text = { Text(stringResource(R.string.permission_denied_message)) },
        confirmButton = {
            TextButton(
                colors  = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                ),
                onClick = {
                    onDismissRequest()
                    context.openAppSettings()
                }
            ) {
                Text(stringResource(R.string.grant_permission))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}