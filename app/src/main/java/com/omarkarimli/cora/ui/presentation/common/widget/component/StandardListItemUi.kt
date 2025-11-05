package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun StandardListItemUi(
    modifier: Modifier = Modifier,
    item: StandardListItemModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = Dimens.PaddingMedium,
                horizontal = Dimens.PaddingSmall
            )
            .noRippleClickable(
                onClick = { item.onClick() }.performHaptic()
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        IconWithBg(
            imageVector = item.leadingIcon
        )
        Column(modifier = Modifier.weight(1f)) {
            item.title?.let {
                Text(
                    text = it,
                    style = AppTypography.titleMedium,
                    softWrap = true
                )
            }
            item.description?.let {
                Text(
                    text = it,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = true
                )
            }
        }
        item.endingText?.let {
            Text(
                text = it,
                style = AppTypography.titleMedium,
                softWrap = true
            )
        }
        item.endingIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = item.title ?: stringResource(R.string.continue_title),
                modifier = Modifier.size(Dimens.IconSizeMedium)
            )
        }
    }
}