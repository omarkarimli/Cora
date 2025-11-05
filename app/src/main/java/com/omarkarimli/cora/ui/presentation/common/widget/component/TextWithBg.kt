package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun TextWithBg(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = AppTypography.labelMedium,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    shape: Shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
) {
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = shape
            )
            .padding(
                horizontal = Dimens.PaddingSmall,
                vertical = Dimens.PaddingExtraSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = textStyle,
            color = contentColor,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}