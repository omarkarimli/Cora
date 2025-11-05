package com.omarkarimli.cora.ui.presentation.screen.settings.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun SelectableSheetItem(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    icon: ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick.performHaptic()
            )
            .padding(
                top = Dimens.PaddingSmall,
                bottom = Dimens.PaddingSmall,
                end = Dimens.PaddingSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier
                    .size(Dimens.IconSizeMedium)
                    .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
            )
            Box(modifier = Modifier.width(Dimens.PaddingSmall))
        }
        Text(
            text = text,
            modifier = Modifier.padding(start = Dimens.PaddingSmall),
            style = MaterialTheme.typography.bodyLarge
        )
        if (isSelected) {
            Box(modifier = Modifier.weight(1f))
            Icon(
                Icons.Rounded.Done,
                contentDescription = stringResource(R.string.selected_title),
                modifier = Modifier.size(Dimens.IconSizeSmall),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}