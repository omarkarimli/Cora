package com.omarkarimli.cora.ui.presentation.screen.fullscreenImageViewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.onSurfaceLight
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClothImagePager(
    innerPadding: PaddingValues,
    pagerState: PagerState,
    imageModels: List<ImageModel>
) {
    val zoomState = rememberZoomState()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(onSurfaceLight)
            .padding(top = innerPadding.calculateTopPadding()),
        pageSpacing = Dimens.PaddingMedium
    ) { pageIndex ->
        val currentImageModel = imageModels[pageIndex]

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(zoomState),
            model = currentImageModel.imageUrl,
            contentDescription = stringResource(R.string.enlarged_image, pageIndex + 1),
            placeholder = painterResource(id = R.drawable.image_placeholder),
            error = painterResource(id = R.drawable.image_placeholder),
            contentScale = ContentScale.Fit
        )
    }
}