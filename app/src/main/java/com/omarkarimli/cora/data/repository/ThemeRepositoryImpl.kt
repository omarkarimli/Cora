package com.omarkarimli.cora.data.repository

import androidx.appcompat.app.AppCompatDelegate
import com.omarkarimli.cora.domain.repository.ThemeRepository
import com.omarkarimli.cora.ui.theme.AppTheme
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor() : ThemeRepository {
    override suspend fun applyTheme(theme: AppTheme) {
        when (theme) {
            AppTheme.System -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            AppTheme.Light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppTheme.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}