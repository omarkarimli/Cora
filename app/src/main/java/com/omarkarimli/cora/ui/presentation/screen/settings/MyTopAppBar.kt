package com.omarkarimli.cora.ui.presentation.screen.settings

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.navigation.Screen
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.sendEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    context: Context,
    navController: NavHostController
) {
    val appName = stringResource(id = R.string.app_name)
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
                stringResource(Screen.Settings.titleResId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = if (isTopAppBarMinimized) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            FilledIconButton(
                onClick = { context.sendEmail(appName) },
                modifier = Modifier.size(Dimens.IconSizeLarge),
                shape = IconButtonDefaults.filledShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.QuestionMark,
                    contentDescription = stringResource(R.string.help),
                    modifier = Modifier.size(Dimens.IconSizeSmall),
                )
            }
            Spacer(Modifier.size(Dimens.PaddingSmall))
            FilledTonalIconButton(
                onClick = { navController.navigate(Screen.Profile.route) },
                modifier = Modifier
                    .width(Dimens.IconSizeExtraLarge)
                    .height(Dimens.IconSizeLarge),
                shape = IconButtonDefaults.filledShape
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = stringResource(R.string.profile),
                    modifier = Modifier.size(Dimens.IconSizeSmall),
                )
            }
            Spacer(Modifier.size(Dimens.PaddingSmall))
        }
    )
}