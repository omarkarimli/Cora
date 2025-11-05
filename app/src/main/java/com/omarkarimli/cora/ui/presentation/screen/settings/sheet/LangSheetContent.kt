package com.omarkarimli.cora.ui.presentation.screen.settings.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.appLanguages
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun LangSheetContent(
    currentLang: String,
    onLangChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingMedium
        )
    ) {
        item {
            Text(
                stringResource(R.string.languages),
                textAlign = TextAlign.Start,
                style = AppTypography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        }
        items(appLanguages) { option ->
            SelectableSheetItem(
                text = option.displayLanguage,
                isSelected = option.code == currentLang,
                onClick = {
                    if (option.code != currentLang) onLangChange(option.code)
                }
            )
        }
    }
}