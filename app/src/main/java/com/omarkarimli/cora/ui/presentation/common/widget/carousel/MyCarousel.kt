package com.omarkarimli.cora.ui.presentation.common.widget.carousel

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.CarouselModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.Durations
import com.omarkarimli.cora.ui.theme.onSurfaceLight
import com.omarkarimli.cora.ui.theme.surfaceLight
import com.omarkarimli.cora.utils.noRippleClickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCarousel(
    coroutineScope: CoroutineScope,
    carouselItems: List<CarouselModel>,
    onClick: (CarouselModel) -> Unit,
    isPaused: Boolean
) {
    val viewModel: MyCarouselViewModel = hiltViewModel()

    val translatedItems by viewModel.translatedItems.collectAsState()

    val carouselState = rememberCarouselState { carouselItems.count() }
    val autoScrollJob = remember { mutableStateOf<Job?>(null) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.translate(carouselItems)
    }

    LaunchedEffect(key1 = isPaused) {
        // Only launch the coroutine if the carousel is not paused
        if (!isPaused) {
            val autoScrollJob = coroutineScope.launch {
                while (isActive) {
                    delay(Durations.CAROUSEL_CHANGE)
                    if (!carouselState.isScrollInProgress) {
                        val currentItem = carouselState.currentItem
                        val nextItem = if (currentItem == carouselItems.lastIndex) {
                            0
                        } else {
                            currentItem + 1
                        }
                        carouselState.animateScrollToItem(nextItem)
                    }
                }
            }
            // Ensure the job is cancelled when the effect is disposed or the key changes
            try {
                awaitCancellation()
            } finally {
                autoScrollJob.cancel()
            }
        }
    }

    if (carouselItems.isNotEmpty()) {
        HorizontalCenteredHeroCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.CarouselHeight)
                // Add a pointer input to detect user interaction
                .pointerInput(Unit) {
                    // We use detectTapGestures to capture any touch event on the carousel
                    detectTapGestures(
                        onPress = {
                            // User has touched the screen, cancel the auto-scroll job
                            autoScrollJob.value?.cancel()
                        }
                    )
                },
            state = carouselState,
            itemSpacing = Dimens.PaddingSmall,
            userScrollEnabled = true,
            contentPadding = PaddingValues(horizontal = Dimens.PaddingMedium)
        ) { i ->
            val item = carouselItems[i]
            val translatedItem = translatedItems.getOrElse(i) { item }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .maskClip(MaterialTheme.shapes.extraLarge)
                    .noRippleClickable(
                        onClick = { onClick(item) }
                    )
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(),
                    model = item.imagePath,
                    contentDescription = translatedItem.title,
                    placeholder = painterResource(id = R.drawable.image_placeholder),
                    error = painterResource(id = R.drawable.image_placeholder),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter // Top seeing
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Dimens.PaddingMedium),
                    text = translatedItem.title,
                    color = surfaceLight,
                    style = AppTypography.titleLarge.copy(
                        shadow = Shadow(
                            color = onSurfaceLight,
                            blurRadius = Dimens.PaddingMedium.value
                        )
                    ),
                )
            }
        }
    }
}