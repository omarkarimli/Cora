package com.omarkarimli.cora.ui.presentation.screen.chatHistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.toDateTimeString

@Composable
fun ChatHistoryItem(
    item: ChatHistoryItemModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium)
            .noRippleClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = item.title,
            style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Normal),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = item.timestamp.toDateTimeString(),
            style = AppTypography.bodySmall,
            softWrap = false
        )
    }
}