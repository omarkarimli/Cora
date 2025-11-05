package com.omarkarimli.cora.ui.presentation.screen.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun PartSheetContent(
    options: List<ImageModel>,
    onToggle: (ImageModel) -> Unit
) {
    Column(
        modifier = Modifier.padding(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        ),
    ) {
        Text(
            stringResource(R.string.found_items),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = Dimens.PaddingSmall)
                    .clickable(
                        onClick = { onToggle(option) }
                    ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(Dimens.IconSizeExtraLarge)
                        .clip(RoundedCornerShape(Dimens.CornerRadiusMedium)),
                    model = option.imageUrl,
                    contentDescription = stringResource(R.string.part_attachment, options.indexOf(option) + 1),
                    placeholder = painterResource(id = R.drawable.image_placeholder),
                    error = painterResource(id = R.drawable.image_placeholder),
                    contentScale = ContentScale.Crop
                )
                Text(
                    modifier = Modifier.padding(start = Dimens.PaddingMedium),
                    text = option.sourceUrl,
                    style = AppTypography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}