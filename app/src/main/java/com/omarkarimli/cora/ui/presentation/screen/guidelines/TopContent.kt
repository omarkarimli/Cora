package com.omarkarimli.cora.ui.presentation.screen.guidelines

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.FrontHand
import androidx.compose.material.icons.rounded.ThumbDownAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource // Added import
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.presentation.common.widget.component.AnimatedText
import com.omarkarimli.cora.ui.presentation.common.widget.component.FadeOverlay
import com.omarkarimli.cora.ui.presentation.common.widget.component.IconsStack
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.boxShadow
import com.omarkarimli.cora.utils.toGradientText

@Composable
fun TopContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.PaddingMedium,
                end = Dimens.PaddingMedium,
                top = Dimens.PaddingLarge,
                bottom = Dimens.PaddingSmall
            )
            .boxShadow()
            .clip(RoundedCornerShape(Dimens.CornerRadiusExtraLarge))
    ) {
        Box(
            modifier = Modifier.matchParentSize()
        ) {
            Image(
                modifier = Modifier.matchParentSize(),
                painter = painterResource(id = R.drawable.i1),
                contentDescription = stringResource(R.string.background), // Externalized string
                contentScale = ContentScale.Crop
            )
            FadeOverlay(
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimens.PaddingLarge,
                    end = Dimens.PaddingLarge,
                    bottom = Dimens.PaddingLarge,
                    top = Dimens.PaddingSmall
                ),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconsStack(
                iconList = listOf(
                    Icons.Rounded.FrontHand,
                    Icons.Rounded.Accessibility,
                    Icons.Rounded.ThumbDownAlt
                )
            )
            AnimatedText(
                texts = stringArrayResource(id = R.array.guideline_mottos),
                style = AppTypography.headlineLarge.toGradientText()
            )
        }
    }
}