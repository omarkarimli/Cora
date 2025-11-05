package com.omarkarimli.cora.ui.presentation.screen.fullscreenImageViewer

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.copyToClipboard
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.openUrl

@Composable
fun MyBottomBar(
    modifier: Modifier = Modifier,
    context: Context,
    sourceText: String
) {
    if (sourceText.trim().isNotBlank())
    {
        Row(
            modifier = modifier
                .padding(Dimens.PaddingMedium)
                .padding(bottom = Dimens.PaddingSmall)
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(Dimens.CornerRadiusMedium)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall, Alignment.CenterHorizontally)
        ) {
            IconButton(
                onClick = { context.copyToClipboard(sourceText) },
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = stringResource(R.string.link_to_source), // Externalized string
                )
            }
            Text(
                modifier = Modifier
                    .noRippleClickable(
                        onClick = { context.openUrl(sourceText) }
                    ),
                text = sourceText,
                style = AppTypography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}