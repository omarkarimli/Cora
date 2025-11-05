package com.omarkarimli.cora.domain.models

data class ValidationResult(
    val isValid: Boolean = true,
    val errorMessage: String? = null
)
