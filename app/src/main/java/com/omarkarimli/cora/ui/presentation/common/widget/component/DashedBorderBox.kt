package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.dashedBorder
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun DashedBorderWidget(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String? = null,
    onClick: () -> Unit = {},
    innerWidget: (@Composable () -> Unit)? = null
) {
    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    Column(
        modifier = modifier
            .padding(vertical = Dimens.PaddingSmall)
            .dashedBorder(brush = SolidColor(contentColor))
            .noRippleClickable(onClick = onClick.performHaptic())
            .padding(Dimens.PaddingMedium)
            .clip(RoundedCornerShape(Dimens.CornerRadiusLarge)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(Dimens.IconSizeLarge)
        )
        title?.let {
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Text(
                text = it,
                style = AppTypography.titleMedium,
                color = contentColor
            )
        }
        innerWidget?.let {
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            it()
        }
    }
}