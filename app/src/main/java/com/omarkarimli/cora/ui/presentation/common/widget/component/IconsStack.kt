package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.boxShadow

@Composable
fun IconsStack(
    iconList: List<ImageVector> = listOf(
        Icons.Rounded.FormatQuote,
        Icons.Rounded.Bolt,
        Icons.Rounded.AutoAwesome
    )
) {
    val overlap = Dimens.PaddingExtraLarge + Dimens.PaddingSmall
    val shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium),
        contentAlignment = Alignment.Center
    ) {
        IconWithBg(
            modifier = Modifier
                .offset(x = -overlap)
                .boxShadow(),
            imageVector = iconList[2],
            shape = shape
        )

        IconWithBg(
            modifier = Modifier
                .offset(x = overlap)
                .boxShadow(),
            imageVector = iconList[1],
            shape = shape,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )

        IconWithBg(
            modifier = Modifier.boxShadow(),
            imageVector = iconList[0],
            containerSize = Dimens.IconBackgroundSizeLarge,
            iconSize = Dimens.IconSizeLarge,
            shape = shape,
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        )
    }
}