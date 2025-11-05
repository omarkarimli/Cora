package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.boxShadow
import com.omarkarimli.cora.utils.performHaptic
import kotlin.collections.forEach

@Composable
fun TagContents(items: List<StandardListItemModel>) {
    if (items.isNotEmpty()) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            items.forEach { item ->
                TagItem(item)
            }
        }
    }
}

@Composable
fun TagItem(item: StandardListItemModel) {
    Row(
        modifier = Modifier
            .boxShadow(
                shadowColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
            )
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
            )
            .padding(
                top = Dimens.PaddingSmall,
                bottom = Dimens.PaddingSmall,
                start = Dimens.PaddingSmall,
                end = Dimens.PaddingExtraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.leadingIcon?.let { imageVector ->
            Icon(
                modifier = Modifier.size(Dimens.IconSizeSmall),
                imageVector = imageVector,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        }
        item.title?.let { text ->
            Text(
                text = text,
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingExtraSmall))
        }
        IconButton(
            modifier = Modifier.size(Dimens.IconSizeSmall),
            onClick = item.onClick.performHaptic()
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.surface
            )
        }
    }
}