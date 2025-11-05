package com.omarkarimli.cora.domain.repository

import com.omarkarimli.cora.ui.theme.AppTheme

interface ThemeRepository {
    suspend fun applyTheme(theme: AppTheme)
}