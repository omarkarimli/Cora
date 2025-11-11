package com.omarkarimli.cora.domain.models.validation

data class ValidationResult(
    val isValid: Boolean = true,
    val errorMessage: String? = null
)