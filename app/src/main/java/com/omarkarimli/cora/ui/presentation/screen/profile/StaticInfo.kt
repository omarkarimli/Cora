package com.omarkarimli.cora.ui.presentation.screen.profile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.ui.theme.AppTypography

@Composable
fun StaticInfo(userModel: UserModel) {
    Text(
        text = "${userModel.personalInfo.firstName} ${userModel.personalInfo.lastName}",
        style = AppTypography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = userModel.personalInfo.email,
        style = AppTypography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}