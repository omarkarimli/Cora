package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.utils.toAnnotatedString

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = AppTypography.bodyLarge,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    collapsedMaxLines: Int = 2,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var hasVisualOverflow by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = text.toAnnotatedString(),
            style = textStyle,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    hasVisualOverflow = true
                }
            }
        )

        if (hasVisualOverflow) {
            val buttonText = if (isExpanded) stringResource(R.string.show_less) else stringResource(R.string.read_more)
            Text(
                text = buttonText,
                style = textStyle,
                color = indicatorColor,
                modifier = Modifier.clickable {
                    isExpanded = !isExpanded
                }
            )
        }
    }
}