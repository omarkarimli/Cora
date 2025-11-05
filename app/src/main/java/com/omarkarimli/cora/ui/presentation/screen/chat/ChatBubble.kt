package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // Import Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // Added import
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.data.local.Converters
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.presentation.common.widget.component.IconWithBg
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.isWebUrl
import com.omarkarimli.cora.utils.toAnnotatedString

@Composable
fun ChatBubble(
    message: MessageModel
) {
    val navController = LocalNavController.current
    val isUserMe = message.isFromMe
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isUserMe) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSizeMedium)
                    .align(Alignment.Bottom),
                painter = painterResource(R.drawable.app_icon_light),
                contentDescription = stringResource(R.string.app_icon),
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        }

        Column(
            modifier = Modifier.animateContentSize(),
            horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            if (message.images.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    items(message.images) {imageModel -> // Renamed it to imageModel for clarity
                        Box(
                            modifier = Modifier
                                .size(Dimens.IconSizeExtraLarge + Dimens.PaddingSmall)
                                .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        val initialPage = message.images.indexOf(imageModel)
                                        navController.navigate(
                                            "${Screen.FullScreenImageViewer.route}/${Converters().fromImageModels(message.images)}?initialPage=$initialPage"
                                        )
                                    },
                                model = imageModel.imageUrl,
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.image_placeholder),
                                error = painterResource(id = R.drawable.image_placeholder),
                                contentScale = ContentScale.Crop
                            )

                            IconWithBg(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(
                                        top = Dimens.PaddingExtraSmall,
                                        end = Dimens.PaddingExtraSmall
                                    ),
                                imageVector =
                                    if (imageModel.imageUrl.isWebUrl()) Icons.Outlined.Language
                                    else Icons.Outlined.Devices,
                                contentDescription = if (imageModel.imageUrl.isWebUrl()) stringResource(R.string.image_from_web) else stringResource(R.string.image_from_device), // Updated
                                shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                containerSize = Dimens.IconBackgroundSizeSmall,
                                iconSize = Dimens.IconSizeExtraSmall
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (isUserMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = Dimens.CornerRadiusLarge,
                            topEnd = Dimens.CornerRadiusLarge,
                            bottomStart = if (isUserMe) Dimens.CornerRadiusLarge else Dimens.CornerRadiusSmall,
                            bottomEnd = if (isUserMe) Dimens.CornerRadiusSmall else Dimens.CornerRadiusLarge
                        )
                    )
                    .widthIn(max = Dimens.MaxBubbleWidth)
                    .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
                contentAlignment = Alignment.CenterStart
            ) {
                val annotatedString = message.text.toAnnotatedString()
                ClickableText(
                    text = annotatedString,
                    style = AppTypography.bodyMedium.copy(
                        color = if (isUserMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = {
                        annotatedString.getStringAnnotations("URL", it, it)
                            .firstOrNull()?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                    }
                )
            }
        }
    }
}
