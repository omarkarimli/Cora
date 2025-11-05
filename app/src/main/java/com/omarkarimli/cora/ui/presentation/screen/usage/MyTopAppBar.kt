package com.omarkarimli.cora.ui.presentation.screen.usage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController,
    currentSubscriptionModel: SubscriptionModel?
) {
    val isTopAppBarMinimized = scrollBehavior.state.collapsedFraction > 0.5

    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_prev)
                )
            }
        },
        title = {
            Text(
                stringResource(Screen.Usage.titleResId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = if (isTopAppBarMinimized) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            currentSubscriptionModel?.let {
                Text(
                    modifier = Modifier
                        .padding(end = Dimens.PaddingSmall)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(Dimens.CornerRadiusMedium)
                        )
                        .padding(horizontal = Dimens.PaddingSmall, vertical = Dimens.PaddingExtraSmall),
                    text = it.title,
                    style = AppTypography.titleSmall,
                    color = MaterialTheme.colorScheme.surface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}