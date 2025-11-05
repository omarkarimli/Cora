package com.omarkarimli.cora.ui.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.data.local.Converters
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun ProfilePicture(profilePictureUrl: String?) {
    val navController = LocalNavController.current
    val shape = MaterialShapes.Circle.toShape()

    val imageModifier = Modifier
        .padding(top = Dimens.PaddingExtraLarge)
        .size(Dimens.LargeProfilePic)
        .border(
            width = Dimens.StrokeWidthMedium,
            color = MaterialTheme.colorScheme.surface,
            shape = shape
        )
        .clip(shape)

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Banner()
        if (profilePictureUrl != null) {
            AsyncImage(
                modifier = imageModifier
                    .align(Alignment.Center)
                    .clickable {
                        navController.navigate(
                            "${Screen.FullScreenImageViewer.route}/${Converters().fromImageModels(listOf(
                                ImageModel(profilePictureUrl)))}"
                        )
                    },
                model = profilePictureUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.image_placeholder),
                error = painterResource(id = R.drawable.image_placeholder),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = null,
                modifier = imageModifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun Banner() {
    val shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.BannerHeight)
            .clip(shape)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.pattern),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = shape
                )
        )
    }
}