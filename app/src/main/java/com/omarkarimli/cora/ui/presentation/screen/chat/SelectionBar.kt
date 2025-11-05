package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun SelectionBar(
    modifier: Modifier = Modifier,
    isSelecting: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val contentColor = MaterialTheme.colorScheme.onSurface

    AnimatedVisibility(
        visible = isSelecting,
        enter = fadeIn() +
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
        exit = fadeOut() +
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.PaddingExtraLarge)
        ) {
            Row(
                modifier = modifier
                    .background(
                        color = containerColor,
                        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge)
                    )
                    .padding(Dimens.PaddingSmall)
                    .noRippleClickable(
                        onClick = onToggle.performHaptic()
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = Dimens.PaddingSmall,
                        end = Dimens.PaddingMedium
                    ),
                    text = stringResource(R.string.choose_photo_to_voux),
                    color = contentColor,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialShapes.Circle.toShape()
                        )
                        .padding(
                            horizontal = Dimens.PaddingExtraSmall,
                            vertical = Dimens.PaddingExtraSmall
                        )
                        .noRippleClickable(
                            onClick = onToggle.performHaptic(),
                        ),
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.disable_selecting),
                    tint = contentColor,
                )
            }
        }
    }
}