package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun IconWithBg(
    modifier: Modifier = Modifier,
    paintResId: Int = R.drawable.app_icon_light,
    imageVector: ImageVector? = null,
    contentDescription: String? = null, // Added contentDescription parameter
    iconSize: Dp = Dimens.IconSizeMedium,
    containerSize: Dp = Dimens.IconBackgroundSizeMedium,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    shape: Shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
) {
    Box(
        modifier = modifier
            .size(containerSize)
            .background(
                color = containerColor,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription, // Used here
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
        } else {
            Icon(
                painter = painterResource(id = paintResId),
                contentDescription = contentDescription, // And here
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}