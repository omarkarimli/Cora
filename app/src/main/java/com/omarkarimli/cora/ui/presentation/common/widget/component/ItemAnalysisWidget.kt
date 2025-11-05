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
import coil.compose.AsyncImage
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ItemAnalysisModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable

@Composable
fun ItemAnalysisWidget(
    itemAnalysis: ItemAnalysisModel,
    onClick: () -> Unit,
    onLongClick: (ItemAnalysisModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(
                onClick = onClick,
                onLongClick = { onLongClick(itemAnalysis) }
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall, Alignment.Top)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(max = Dimens.MaxStaggeredHeight)
                .clip(RoundedCornerShape(Dimens.CornerRadiusLarge)),
            model = itemAnalysis.imagePath,
            contentDescription = stringResource(R.string.analyzed_image),
            placeholder = painterResource(id = R.drawable.image_placeholder),
            error = painterResource(id = R.drawable.image_placeholder),
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
            text = itemAnalysis.title,
            style = AppTypography.titleMedium,
            softWrap = true,
            maxLines = 2
        )
        if (itemAnalysis.parts.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
                text = stringResource(R.string.item_analysis_parts_count, itemAnalysis.parts.size),
                style = AppTypography.bodyMedium,
                maxLines = 1
            )
        }
    }
}