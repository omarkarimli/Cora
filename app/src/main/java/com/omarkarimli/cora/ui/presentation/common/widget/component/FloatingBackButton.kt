package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun FloatingBackButton(
    innerPadding: PaddingValues,
    navController: NavController
) {
    IconButton(
        onClick = { navController.navigateUp() },
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_to_prev))
    }
}