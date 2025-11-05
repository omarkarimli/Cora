package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.domain.models.ValidationResult
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun MyFilledTextField(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
    textStyle: TextStyle = AppTypography.bodyLarge,
    containerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
    helperColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    textFieldColor: Color = MaterialTheme.colorScheme.onSurface,
    expanded: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: ImageVector? = null,
    leadingIconContentDescription: String? = null,
    label: String? = null,
    helper: String,
    field: ValidatableField,
    validationResult: ValidationResult,
    onValueChange: (String) -> Unit = {},
    onClick: (() -> Unit)? = null,
    endingWidgets: List<@Composable () -> Unit> = emptyList()
) {
    val finalIsError = !validationResult.isValid
    var isFocused by remember { mutableStateOf(false) }
    val p = if (endingWidgets.isEmpty()) Dimens.PaddingMedium else Dimens.PaddingSmall

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        label?.let {
            Text(it, style = AppTypography.titleMedium)
        }

        val borderColor = when {
            finalIsError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        }

        BasicTextField(
            value = field.value,
            onValueChange = onValueChange,
            modifier = Modifier
                .onFocusChanged { focusState ->
                    isFocused = if (!readOnly) focusState.isFocused else false
                }
                .background(containerColor, shape)
                .border(
                    width = Dimens.StrokeWidthExtraSmall,
                    color = borderColor,
                    shape = shape
                ),
            singleLine = field.isSingleLine,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = textFieldColor),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(
                            start = Dimens.PaddingMedium,
                            end = p,
                            top = p,
                            bottom = p
                        )
                        .animateContentSize()
                        .then(
                            onClick?.let {
                                Modifier.noRippleClickable(
                                    onClick = { it() }.performHaptic()
                                )
                            } ?: Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leadingIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = leadingIconContentDescription,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Box(modifier = Modifier.width(Dimens.PaddingSmall))
                    }

                    AnimatedVisibility(
                        modifier = Modifier.weight(1f),
                        visible = expanded,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        Box {
                            if (field.value.isEmpty() && !isFocused) {
                                Text(
                                    text = helper,
                                    style = textStyle,
                                    color = helperColor
                                )
                            }
                            innerTextField()
                        }
                    }

                    if (endingWidgets.isNotEmpty()) {
                        endingWidgets.forEach { item ->
                            Box(modifier = Modifier.width(Dimens.PaddingSmall))
                            item()
                        }
                    }
                }
            }
        )

        if (finalIsError) {
            validationResult.errorMessage?.let {
                Text(
                    text = it,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = Dimens.PaddingMedium)
                )
            }
        }
    }
}
