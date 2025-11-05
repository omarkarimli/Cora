package com.omarkarimli.cora.domain.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class StandardListItemModel(
    val id: Int = 0,
    val leadingIcon: ImageVector? = null,
    val title: String? = null,
    val description: String? = null,
    val endingIcon: ImageVector? = null,
    val endingText: String? = null,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val images: List<ImageModel> = emptyList(),
    val onClick: () -> Unit = {}
)