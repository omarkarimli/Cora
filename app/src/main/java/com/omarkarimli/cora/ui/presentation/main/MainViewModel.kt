package com.omarkarimli.cora.ui.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarkarimli.cora.domain.repository.LangRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.repository.ThemeRepository
import com.omarkarimli.cora.ui.theme.AppTheme
import com.omarkarimli.cora.utils.SpConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val themeRepository: ThemeRepository,
    val langRepository: LangRepository
) : ViewModel() {

    private val _currentLang = MutableStateFlow("en")
    val currentLang: StateFlow<String> = _currentLang.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppTheme.System)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _isDynamicColorEnabled = MutableStateFlow(false)
    val isDynamicColorEnabled: StateFlow<Boolean> = _isDynamicColorEnabled.asStateFlow()

    init {
        loadInitialTheme()
        loadInitialDynamicColor()
    }

    fun loadInitialTheme() {
        viewModelScope.launch {
            // Load theme setting
            val savedThemeName = sharedPreferenceRepository.getString(SpConstant.THEME_KEY, "system")
            val initialTheme = when (savedThemeName) {
                "light" -> AppTheme.Light
                "dark" -> AppTheme.Dark
                else -> AppTheme.System
            }
            _currentTheme.value = initialTheme
        }
    }

    fun  loadInitialDynamicColor() {
        viewModelScope.launch {
            val isDynamicColorEnabled = sharedPreferenceRepository.getBoolean(SpConstant.DYNAMIC_COLOR_KEY, false)
            _isDynamicColorEnabled.value = isDynamicColorEnabled
        }
    }

    fun onThemeChange(newTheme: AppTheme) {
        viewModelScope.launch {
            _currentTheme.value = newTheme
            sharedPreferenceRepository.saveString(SpConstant.THEME_KEY, newTheme.name.lowercase())
            themeRepository.applyTheme(newTheme)
        }
    }

    fun onDynamicColorToggle(isEnabled: Boolean) {
        viewModelScope.launch {
            _isDynamicColorEnabled.value = isEnabled
            sharedPreferenceRepository.saveBoolean(SpConstant.DYNAMIC_COLOR_KEY, isEnabled)
        }
    }

    fun onLangChange(newLang: String) {
        viewModelScope.launch {
            setLang(newLang)
            sharedPreferenceRepository.saveString(SpConstant.LANG_KEY, newLang)
        }
    }

    fun setLang(lang: String) {
        _currentLang.value = lang
    }
}