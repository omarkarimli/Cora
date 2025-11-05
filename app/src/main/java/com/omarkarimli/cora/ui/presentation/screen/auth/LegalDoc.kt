package com.omarkarimli.cora.ui.presentation.screen.auth

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // Added import
import com.omarkarimli.cora.R // Added import
import com.omarkarimli.cora.BuildConfig.LEGAL_DOCUMENTS_URL
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.onSurfaceLight
import com.omarkarimli.cora.ui.theme.primaryLight
import com.omarkarimli.cora.utils.openUrl

@Composable
fun LegalDoc(
    modifier: Modifier = Modifier,
    context: Context,
    normalColor: Color = onSurfaceLight,
    linkColor: Color = primaryLight
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.auth_legal_doc_agreement_prefix), // Externalized string
            style = AppTypography.bodyMedium,
            color = normalColor
        )
        Text(
            modifier = Modifier
                .clickable(
                    onClick = { context.openUrl(LEGAL_DOCUMENTS_URL) }
                ),
            text = stringResource(R.string.legal_doc), // Externalized string
            style = AppTypography.bodyMedium,
            color = linkColor
        )
    }
}