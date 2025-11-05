package com.omarkarimli.cora.ui.presentation.screen.userSetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R // Import R class for string resources
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.domain.models.ValidationResult
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelectionChipGroup(
    field: ValidatableField,
    validationResult: ValidationResult,
    onGenderSelected: (String) -> Unit
) {
    val finalIsError = !validationResult.isValid

    val genders = listOf(
        StandardListItemModel(
            id = 0,
            title = stringResource(R.string.gender_male),
            description = "male", // key
        ),
        StandardListItemModel(
            id = 1,
            title = stringResource(R.string.gender_female),
            description = "female",
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Text(
            stringResource(R.string.gender),
            style = AppTypography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            genders.forEach { gender ->
                FilterChip(
                    onClick = { gender.description?.let { onGenderSelected(it) } },
                    label = {
                        Text(
                            gender.title ?: "",
                            style = AppTypography.bodyLarge
                        )
                    },
                    selected = field.value == gender.description,
                    leadingIcon = if (field.value == gender.description) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.selected_title),
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

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