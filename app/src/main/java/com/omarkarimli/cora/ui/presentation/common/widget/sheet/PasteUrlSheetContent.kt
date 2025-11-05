package com.omarkarimli.cora.ui.presentation.common.widget.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.ui.presentation.common.widget.component.MyFilledTextField
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun PasteUrlSheetContent(
    onConfirm: (String) -> Unit,
    buttonText: String = stringResource(R.string.confirm)
) {
    var field by remember { mutableStateOf(ValidatableField("", minLength = 0, isSingleLine = true)) }
    val validationResult = field.validate()
    val isValueBlank by remember { derivedStateOf { field.value.isBlank() } }

    Column(
        modifier = Modifier.padding(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            stringResource(R.string.paste_url),
            textAlign = TextAlign.Start,
            style = AppTypography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        MyFilledTextField(
            label = stringResource(R.string.link_of_image),
            field = field,
            validationResult = validationResult,
            helper = stringResource(R.string.required),
            onValueChange = { field = ValidatableField(it) }
        )
        WideButton(
            text = buttonText,
            onClick = { onConfirm(field.value) }.performHaptic(
                defaultHaptic = HapticFeedbackType.Confirm
            ),
            enabled = validationResult.isValid && !isValueBlank
        )
    }
}
