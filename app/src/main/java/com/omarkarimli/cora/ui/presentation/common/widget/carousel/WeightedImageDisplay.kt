package com.omarkarimli.cora.ui.presentation.common.widget.carousel

import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.Durations
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun WeightedImageDisplay(
    images: List<Painter>,
    height: Dp = Dimens.WeightedCarouselHeightLarge,
    itemSpacing: Dp = Dimens.PaddingMedium,
    cornerRadius: Dp = Dimens.CornerRadiusExtraLarge,
    btnEnabled: Boolean = true
) {
    val selectedIndex = remember { mutableIntStateOf(0) }
    val isAnimationPlaying = remember { mutableStateOf(true) }

    val weights = remember {
        listOf(
            Animatable(1f),
            Animatable(1f),
            Animatable(1f)
        )
    }

    LaunchedEffect(Unit) {
        weights[0].snapTo(1f)
        weights[1].snapTo(1f)
        weights[2].snapTo(1f)
    }

    LaunchedEffect(isAnimationPlaying.value) {
        if (isAnimationPlaying.value) {
            var currentIndex = selectedIndex.intValue

            while (isActive && isAnimationPlaying.value) {
                selectedIndex.intValue = currentIndex

                weights.forEachIndexed { index, animatableWeight ->
                    val targetWeight = if (index == currentIndex) 2f else 0.5f
                    if (animatableWeight.value != targetWeight) {
                        animatableWeight.animateTo(
                            targetValue = targetWeight,
                            animationSpec = tween(durationMillis = Durations.WEIGHTED_ANIM, easing = LinearEasing)
                        )
                    }
                }
                delay(2000)

                // Move to the next index, cycling back to 0
                currentIndex = (currentIndex + 1) % weights.size
            }
        }
    }

    Box(
        modifier = Modifier.height(height)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            images.forEachIndexed { index, imageResource ->
                val currentWeight by weights[index].asState()

                Box(
                    modifier = Modifier
                        .weight(currentWeight)
                        .clip(RoundedCornerShape(cornerRadius))
                ) {
                    Image(
                        painter = imageResource,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (btnEnabled) {
            FilledIconButton(
                onClick = { isAnimationPlaying.value = !isAnimationPlaying.value },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Dimens.PaddingSmall, end = Dimens.PaddingExtraSmall)
                    .size(Dimens.IconSizeExtraLarge),
                shape = MaterialShapes.Circle.toShape(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = if (isAnimationPlaying.value) Icons.Outlined.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isAnimationPlaying.value) "Pause Animation" else "Play Animation",
                    modifier = Modifier.size(Dimens.IconSizeSmall)
                )
            }
        }
    }
}