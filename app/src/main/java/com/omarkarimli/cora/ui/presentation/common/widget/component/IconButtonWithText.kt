package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun IconButtonWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier
                .width(Dimens.IconSizeExtraLarge)
                .height(Dimens.IconSizeLarge),
            shape = IconButtonDefaults.filledShape
        ) {
            Icon(
                modifier = Modifier.size(Dimens.IconSizeSmall),
                imageVector = icon,
                contentDescription = text
            )
        }
        Text(
            text = text,
            style = AppTypography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}