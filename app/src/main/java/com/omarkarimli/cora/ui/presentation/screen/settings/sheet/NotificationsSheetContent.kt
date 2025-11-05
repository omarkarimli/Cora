package com.omarkarimli.cora.ui.presentation.screen.settings.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun NotificationsSheetContent(
    isNotificationsEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    // These strings are used as keys for logic
    val optionKeys = listOf("On", "Off")
    val currentSelectedKey = if (isNotificationsEnabled) "On" else "Off"

    Column(
        modifier = Modifier.padding(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        ),
    ) {
        Text(
            stringResource(R.string.notifications),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        optionKeys.forEach { optionKey ->
            val displayOptionText = when (optionKey) {
                "On" -> stringResource(R.string.on)
                "Off" -> stringResource(R.string.off)
                else -> optionKey // Fallback
            }
            SelectableSheetItem(
                text = displayOptionText,
                isSelected = optionKey == currentSelectedKey,
                onClick = {
                    // Logic still uses "On"/"Off" keys
                    onToggle(optionKey == "On")
                }
            )
        }
    }
}