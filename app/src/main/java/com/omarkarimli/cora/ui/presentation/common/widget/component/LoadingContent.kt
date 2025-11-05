package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = false,
                onClick = {}
            )
    ) {
        ContainedLoadingIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}