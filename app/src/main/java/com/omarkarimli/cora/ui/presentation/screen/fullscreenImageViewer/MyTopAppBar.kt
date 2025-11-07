package com.omarkarimli.cora.ui.presentation.screen.fullscreenImageViewer

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // Added import
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.omarkarimli.cora.R // Added import
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.onShare

@Composable
fun MyTopAppBar(
    context: Context,
    navController: NavController,
    viewModel: FullScreenImageViewerViewModel,
    pagerState: PagerState,
    imageModels: List<ImageModel>
) {
    val containerColor = Color.Black
    val contentColor = Color.White
    val currentItem = imageModels[pagerState.currentPage]

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor
        ),
        title = {},
        navigationIcon = {
            Text(
                modifier = Modifier.padding(start = Dimens.PaddingLarge),
                text = "${pagerState.currentPage + 1}/${imageModels.size}",
                style = AppTypography.bodyMedium,
                textAlign = TextAlign.Start,
                color = contentColor
            )
        },
        actions = {
            IconButton(
                onClick = {
                    viewModel.downloadImage(currentItem.imageUrl)
                },
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.SaveAlt,
                    contentDescription = stringResource(R.string.download) // Externalized string
                )
            }

            IconButton(
                onClick = {
                    context.onShare(
                        context = context,
                        imagePaths = listOf(currentItem.imageUrl)
                    )
                },
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.IosShare,
                    contentDescription = stringResource(R.string.share) // Externalized string
                )
            }

            IconButton(
                onClick = { navController.navigateUp() },
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.back_to_prev) // Externalized string
                )
            }
            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        }
    )
}