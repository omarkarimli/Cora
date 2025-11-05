package com.omarkarimli.cora.ui.presentation.common.widget.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.omarkarimli.cora.utils.Constants.MIN_FIELD_LENGTH
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun ReportIssueSheetContent(
    onConfirm: (description: String) -> Unit,
    buttonText: String = stringResource(R.string.confirm)
) {
    var field by remember { mutableStateOf(ValidatableField("", minLength = MIN_FIELD_LENGTH)) }
    val validationResult = field.validate()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        horizontalAlignment = Alignment.Start,
        contentPadding = PaddingValues(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingSmall
        )
    ) {
        item {
            Text(
                stringResource(R.string.report_issue),
                textAlign = TextAlign.Start,
                style = AppTypography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        item {
            MyFilledTextField(
                label = stringResource(R.string.describe),
                field = field,
                validationResult = validationResult,
                helper = stringResource(R.string.required),
                onValueChange = { field = field.copy(value = it) }
            )
        }
        item {
            WideButton(
                text = buttonText,
                onClick = { onConfirm(field.value) }.performHaptic(
                    defaultHaptic = HapticFeedbackType.Confirm
                ),
                enabled = validationResult.isValid
            )
        }
    }
}
