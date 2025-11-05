package com.omarkarimli.cora.ui.presentation.common.widget.pager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun DotIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    Row(
        modifier = modifier
            .padding(bottom = Dimens.PaddingMedium)
            .wrapContentHeight()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .padding(Dimens.PaddingExtraSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val isCurrentPage = pagerState.currentPage == iteration
            val color by animateColorAsState(
                targetValue = if (isCurrentPage) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                },
                animationSpec = tween(durationMillis = 300)
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .height(Dimens.DotIndicatorSizeMedium)
                    .width(
                        if (isCurrentPage) Dimens.DotIndicatorSizeLarge
                        else Dimens.DotIndicatorSizeMedium
                    )
            )
        }
    }
}