package com.omarkarimli.cora.ui.presentation.screen.settings.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.AppTheme
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun DarkModeSheetContent(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    // These strings are used as keys for logic and matching currentTheme.name
    val optionKeys = listOf("System", "Light", "Dark")
    val currentSelectedKey by remember {
        mutableStateOf(currentTheme.name.lowercase().replaceFirstChar { it.uppercase() })
    }

    Column(
        modifier = Modifier.padding(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        ),
    ) {
        Text(
            stringResource(R.string.dark_mode),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        optionKeys.forEach { optionKey ->
            val displayOptionText = when (optionKey) {
                "System" -> stringResource(R.string.system)
                "Light" -> stringResource(R.string.light)
                "Dark" -> stringResource(R.string.dark)
                else -> optionKey // Fallback, though ideally all keys are mapped
            }
            SelectableSheetItem(
                text = displayOptionText,
                isSelected = optionKey == currentSelectedKey,
                onClick = {
                    val newTheme = when (optionKey) {
                        "System" -> AppTheme.System
                        "Light" -> AppTheme.Light
                        "Dark" -> AppTheme.Dark
                        else -> AppTheme.System // Default case
                    }
                    if (newTheme != currentTheme) {
                        onThemeChange(newTheme)
                    }
                }
            )
        }
    }
}