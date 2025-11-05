package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.omarkarimli.cora.utils.verticalFade

@Composable
fun FadeOverlay(
    alpha: Float = 0.8f,
    tint: Color = MaterialTheme.colorScheme.surface
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalFade(
                tint = tint,
                alpha = alpha
            )
    )
}