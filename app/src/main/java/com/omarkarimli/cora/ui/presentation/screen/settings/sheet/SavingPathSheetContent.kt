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
fun SavingPathSheetContent(
    savingPath: String,
    onToggle: (String) -> Unit
) {
    val optionKeys = listOf("photos", "downloads")

    Column(
        modifier = Modifier.padding(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        ),
    ) {
        Text(
            stringResource(R.string.saving_path),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        optionKeys.forEach { optionKey ->
            val displayOptionText = when (optionKey) {
                "photos" -> stringResource(R.string.photos)
                "downloads" -> stringResource(R.string.downloads)
                else -> optionKey // Fallback
            }
            SelectableSheetItem(
                text = displayOptionText,
                isSelected = optionKey == savingPath,
                onClick = {
                    onToggle(optionKey)
                }
            )
        }
    }
}