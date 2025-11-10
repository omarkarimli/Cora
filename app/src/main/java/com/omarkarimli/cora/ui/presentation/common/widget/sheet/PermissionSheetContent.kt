package com.omarkarimli.cora.ui.presentation.common.widget.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.openAppSettings
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun PermissionSheetContent(
    titleStringId: Int = R.string.permission_denied,
    descStringId: Int = R.string.permission_denied_message,
    onHide: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Text(
            stringResource(titleStringId),
            textAlign = TextAlign.Start,
            style = AppTypography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            stringResource(descStringId),
            style = AppTypography.bodyMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.PaddingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        ) {
            // Cancel
            WideButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = onHide
            )
            // Grant Permission
            WideButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.allow),
                onClick = {
                    onHide()
                    context.openAppSettings()
                }.performHaptic(
                    defaultHaptic = HapticFeedbackType.Confirm
                ),
            )
        }
    }
}