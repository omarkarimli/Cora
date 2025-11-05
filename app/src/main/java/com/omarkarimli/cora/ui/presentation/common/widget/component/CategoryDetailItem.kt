package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.CategoryDetailModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable

@Composable
fun CategoryDetailItem(
    categoryDetailModel: CategoryDetailModel,
    onClick: () -> Unit,
    onLongClick: (CategoryDetailModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(
                onClick = onClick,
                onLongClick = { onLongClick(categoryDetailModel) }
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall, Alignment.Top)
    ) {
        val index = 0
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(max = Dimens.MaxStaggeredHeight)
                .clip(RoundedCornerShape(Dimens.CornerRadiusLarge)),
            model = categoryDetailModel.category.imageModels[index].imageUrl,
            contentDescription = stringResource(R.string.category_detail_item_image, index),
            placeholder = painterResource(id = R.drawable.image_placeholder),
            error = painterResource(id = R.drawable.image_placeholder),
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
            text = categoryDetailModel.category.title,
            style = AppTypography.titleMedium,
            softWrap = true,
            maxLines = 2
        )
        Text(
            modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
            text = categoryDetailModel.description,
            style = AppTypography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}