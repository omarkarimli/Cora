package com.omarkarimli.cora.ui.presentation.screen.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.omarkarimli.cora.BuildConfig.LEGAL_DOCUMENTS_URL
import com.omarkarimli.cora.BuildConfig.WEBSITE
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.navigation.LocalNavController
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.onSurfaceLight
import com.omarkarimli.cora.ui.theme.primaryLight
import com.omarkarimli.cora.ui.theme.surfaceLight
import com.omarkarimli.cora.utils.openUrl
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val navController = LocalNavController.current

    Scaffold(
        containerColor = surfaceLight,
        topBar = {
            MyTopAppBar(navController)
        }
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(
    navController: NavController
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = surfaceLight,
            navigationIconContentColor = onSurfaceLight,
            titleContentColor = onSurfaceLight,
            actionIconContentColor = onSurfaceLight
        ),
        title = {},
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_prev) // Externalized string
                )
            }
        }
    )
}

@Composable
private fun ScrollContent(
    innerPadding: PaddingValues
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + Dimens.PaddingExtraLarge + Dimens.PaddingMedium,
            start = Dimens.PaddingLarge,
            end = Dimens.PaddingLarge
        )
    ) {
        item {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(Locale.ROOT),
                style = AppTypography.headlineMedium.copy(color = primaryLight),
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
        }
        item {
            Text(
                modifier = Modifier.padding(vertical = Dimens.PaddingMedium),
                text = stringResource(id = R.string.about_main_text),
                style = AppTypography.bodyMedium.copy(color = onSurfaceLight),
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = { context.openUrl(WEBSITE) }
                    ),
                text = WEBSITE,
                style = AppTypography.bodyMedium,
                color = primaryLight
            )
        }
        item {
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = { context.openUrl(LEGAL_DOCUMENTS_URL) }
                    ),
                text = stringResource(R.string.legal_doc),
                style = AppTypography.bodyMedium,
                color = primaryLight
            )
        }
    }
}