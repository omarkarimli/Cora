package com.omarkarimli.cora.ui.presentation.common.widget.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun DeleteChatHistoryItemSheetContent(
    title: String,
    description: String,
    onConfirm: (ChatHistoryItemModel) -> Unit,
    item: ChatHistoryItemModel,
    buttonText: String = stringResource(R.string.confirm)
) {
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
            title,
            textAlign = TextAlign.Start,
            style = AppTypography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            description,
            style = AppTypography.bodyMedium
        )
        WideButton(
            modifier = Modifier.padding(top = Dimens.PaddingSmall),
            text = buttonText,
            onClick = { onConfirm(item) }.performHaptic(
                defaultHaptic = HapticFeedbackType.Confirm
            ),
        )
    }
}