package com.omarkarimli.cora.domain.models.validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R
import com.omarkarimli.cora.utils.TextFieldConstants

data class ValidatableField(
    val value: String,
    val isTextWidget: Boolean = true,
    val minLength: Int = 0,
    val maxLength: Int = TextFieldConstants.MAX_FIELD_LENGTH_LARGE,
    val isSingleLine: Boolean = false,
    val allowGaps: Boolean = true,
    val allowSpecialChars: Boolean = true,
    val allowCapitalLetters: Boolean = true,
    val onlyEnglishLetters: Boolean = false,
) {
    @Composable
    fun validate(): ValidationResult {
        val isMinLengthError = minLength > 0 && (value.isBlank() || value.length < minLength)
        val maxCharError = value.length > maxLength
        val allowGapsError = !allowGaps && value.contains(" ")
        val specialCharsError = !allowSpecialChars && value.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        val capitalLettersError = !allowCapitalLetters && value.any { it.isUpperCase() }
        val onlyEnglishLettersError = onlyEnglishLetters && value.any { it.isLetter() && it !in 'a'..'z' && it !in 'A'..'Z' }

        val textWidgetError = !isTextWidget && value.isBlank()

        return when {
            textWidgetError -> ValidationResult(false, stringResource(R.string.required))

            isMinLengthError -> ValidationResult(
                false,
                stringResource(R.string.error_min_length, minLength)
            )
            maxCharError -> ValidationResult(
                false,
                stringResource(R.string.error_max_length, maxLength)
            )
            allowGapsError -> ValidationResult(false, stringResource(R.string.error_no_gaps))
            specialCharsError -> ValidationResult(
                false,
                stringResource(R.string.error_no_special_chars)
            )
            capitalLettersError -> ValidationResult(
                false,
                stringResource(R.string.error_no_capital_letters)
            )
            onlyEnglishLettersError -> ValidationResult(
                false,
                stringResource(R.string.error_only_english_letters)
            )

            else -> ValidationResult(true, null)
        }
    }
}