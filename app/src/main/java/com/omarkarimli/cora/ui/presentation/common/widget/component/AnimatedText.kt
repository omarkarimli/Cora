package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.omarkarimli.cora.R
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.utils.Durations
import kotlinx.coroutines.delay

@Composable
fun AnimatedText(
    modifier: Modifier = Modifier,
    style: TextStyle = AppTypography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
    texts: Array<String> = stringArrayResource(id = R.array.onboarding_mottos),
    textAlign: TextAlign = TextAlign.Center
) {
    var currentText by remember { mutableStateOf(texts[0]) }

    LaunchedEffect(key1 = Unit) {
        var index = 0
        while (true) {
            delay(Durations.TEXT_CHANGE_DELAY)
            index = (index + 1) % texts.size
            currentText = texts[index]
        }
    }

    AnimatedContent(
        modifier = modifier,
        targetState = currentText,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = fadeIn(animationSpec = tween(durationMillis = Durations.TEXT_FADE)),
                initialContentExit = fadeOut(animationSpec = tween(durationMillis = Durations.TEXT_FADE))
            )
        }
    ) { targetMotto ->
        Text(
            text = targetMotto,
            style = style,
            softWrap = true,
            textAlign = textAlign
        )
    }
}