package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun AttachWidget(
    imageModels: MutableList<ImageModel>,
    onRemove: (ImageModel) -> Unit
) {
    AnimatedVisibility(
        visible = imageModels.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        LazyRow(
            modifier = Modifier.padding(bottom = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            items(imageModels) { 
                Box(
                    modifier = Modifier
                        .size(Dimens.IconSizeExtraLarge + Dimens.IconSizeSmall)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(Dimens.CornerRadiusMedium)),
                        model = it.imageUrl,
                        contentDescription = "Attachment ${imageModels.indexOf(it) + 1}",
                        placeholder = painterResource(id = R.drawable.image_placeholder),
                        error = painterResource(id = R.drawable.image_placeholder),
                        contentScale = ContentScale.Crop
                    )

                    FilledTonalIconButton(
                        modifier = Modifier.align(Alignment.TopEnd).padding(Dimens.PaddingExtraSmall).size(Dimens.IconSizeSmall),
                        onClick = { onRemove(it) },
                        shape = MaterialShapes.Circle.toShape(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.remove_attachment) // Externalized string
                        )
                    }
                }
            }
        }
    }
}
